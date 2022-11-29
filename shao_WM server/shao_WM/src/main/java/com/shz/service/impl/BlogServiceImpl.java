package com.shz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.dto.ScrollResult;
import com.shz.dto.UserDTO;
import com.shz.entity.Blog;
import com.shz.entity.Follow;
import com.shz.entity.User;
import com.shz.mapper.BlogMapper;
import com.shz.service.BlogService;
import com.shz.service.FollowService;
import com.shz.service.UserService;
import com.shz.utils.OSSUtil;
import com.shz.utils.RedisFurryAndPageQueryUtil;
import com.shz.utils.RedisIdWorker;
import com.shz.utils.UserUtil;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.shz.utils.RedisConstants.*;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Resource
    RedisFurryAndPageQueryUtil redisFurryAndPageQueryUtil;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    UserService userService;
    @Resource
    FollowService followService;
    @Resource
    RedisIdWorker redisIdWorker;
    @Resource
    RedissonClient redissonClient;

    @Transactional
    @Override
    public void deleteAllCache(int uid) {
        //keys命令在高并发场景会造成线程堵塞,post,get请求很慢
        List<String> keyList = new ArrayList<>();
        try {
            redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match("history:blog*")
                        .count(Integer.MAX_VALUE).build());
                while (cursor.hasNext()) {
                    keyList.add(new String(cursor.next()));
                }
                return null;
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        redisTemplate.delete(keyList);
        redisTemplate.delete("blog");
        redisTemplate.delete("blog:list");

        redisTemplate.delete("blog:" + uid);
        redisTemplate.delete("blog:my:list:" + uid);

    }

    @Transactional
    @Override
    public void deleteOneCache(Long bid) {
        redisTemplate.delete(CACHE_BLOG_KEY + bid);
        redisTemplate.delete("blog:liked:" + bid);
        List<Follow> list = followService.query().eq("fuid", UserUtil.getCurrentUser().getUid()).list();
        for (Follow follow : list) {
            redisTemplate.opsForZSet().remove("feed:" + follow.getUid(), bid);
        }
    }
    @Transactional
    @Override
    public boolean deleteThisBlog(Long bid) {
        int uid = UserUtil.getCurrentUser().getUid();
        boolean remove = removeById(bid);
        ((BlogService) AopContext.currentProxy()).deleteAllCache(uid);
        ((BlogService) AopContext.currentProxy()).deleteOneCache(bid);
        return remove;
    }
    //因为是博客,用户同时在线访问，redis此时失效，不加锁的话，redis会加入重复数据
    @Override
    public Result getMyBlogs(Integer page, Integer count, String keywords) {
        String tableName = "blog";
        int uid = UserUtil.getCurrentUser().getUid();
        if ("".equals(keywords) || keywords.length() == 0) {
            Long size = stringRedisTemplate.opsForList().size(tableName + ":my:list:" + uid);
            List<String> list = stringRedisTemplate.opsForList().range(tableName + ":my:list:" + uid, (long) (page - 1) * count, (long) (page - 1) * count + count - 1);
            if (size == 0) {
                Page<Blog> pages = query().eq("uid", uid).orderByDesc("createTime").page(new Page<>(page, count));
                List<Blog> records = pages.getRecords();
                for (Blog blog : records) {
                    blog.setUser(getAUser(blog));
                    isBlogLiked(blog);
                }
                if (records.size() != 0) {
                    RLock lock = redissonClient.getLock("lock:blog:my:" + uid);
                    boolean isLock = lock.tryLock();
                    if(!isLock){
                        getMyBlogs(page,count,keywords);
                    }
                    try {
                        List<Blog> blogs = query().eq("uid", uid).orderByDesc("createTime").list();
                        for (Blog blog : blogs) {
                            stringRedisTemplate.opsForList().rightPush(tableName + ":my:list:" + uid, JSONUtil.toJsonStr(blog));
                            blog.setUser(getAUser(blog));
                            isBlogLiked(blog);
                        }
                        stringRedisTemplate.expire(tableName + ":my:list:" + uid, MY_BLOGLIST_TTL, TimeUnit.HOURS);
                    }finally {
                        lock.unlock();
                    }

                }
                return new Result(records, (int) pages.getTotal());
            }
            List<Blog> res = new ArrayList<>();
            for (String s : list) {
                Blog blog = JSONUtil.toBean(s, Blog.class);
                blog.setUser(getAUser(blog));
                isBlogLiked(blog);
                res.add(blog);
            }
            return new Result(res, size.intValue());

        }
        Page<Blog> pages1 = redisFurryAndPageQueryUtil.find(keywords, page, count, tableName + ":" + uid, Blog.class);
        if (pages1.getTotal() == 0) {
            Page<Blog> pages2 = query().like("blogTitle", keywords).orderByDesc("createTime")
                    .eq("uid", uid)
                    .page(new Page<>(page, count));
            List<Blog> records = pages2.getRecords();
            for (Blog record : records) {
                record.setUser(getAUser(record));
                isBlogLiked(record);
            }
            if (records.size() != 0) {
                RLock lock = redissonClient.getLock("lock:blog:my:hash:" + uid);
                boolean isLock = lock.tryLock();
                if(!isLock){
                    return getMyBlogs(page,count,keywords);
                }
                try{
                    List<Blog> list = query().eq("uid", uid).list();
                    for (Blog blog : list) {
                        redisTemplate.opsForHash().put(tableName + ":" + uid, blog.getBlogTitle(), JSONUtil.toJsonStr(blog));
                    }
                }finally {
                    lock.unlock();
                }

            }
            return new Result(records, (int) pages2.getTotal());
        }
        return getScrollResult(pages1);
    }
    @Transactional
    @Override
    public boolean saveThisBlog(Blog blog) {
        Blog b = query().eq("blogTitle", blog.getBlogTitle()).one();
        if(b!=null) return false;
        int uid = UserUtil.getCurrentUser().getUid();
        long bid = redisIdWorker.nextId("blog");
        blog.setUid(uid);
        blog.setBid(bid);
        blog.setLiked(0);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        blog.setCreateTime(timestamp);
        boolean isSuccess = save(blog);
        List<Follow> follows = followService.query().eq("fuid", uid).list();
        for (Follow follow : follows) {
            int fuid = follow.getUid();
            String key = "feed:" + fuid;
            stringRedisTemplate.opsForZSet().add(key, String.valueOf(bid), System.currentTimeMillis());
        }
        ((BlogService) AopContext.currentProxy()).deleteAllCache(uid);
        return isSuccess;
    }


    private User getAUser(Blog blog) {
        int uid = blog.getUid();
        User user = userService.getAUserOrUserCache(uid);
        User res = new User();
        res.setUid(user.getUid());
        res.setUsername(user.getUsername());
        res.setPhone(user.getPhone());
        res.setEmail(user.getEmail());
        res.setAvatar("http://" + OSSUtil.SUFFER_URL + user.getAvatar());
        return res;
    }

    private Result getScrollResult(Page<Blog> pages1) {
        List<Blog> res = new ArrayList<>();
        for (Object o : pages1.getRecords().toArray()) {
            JSONObject jsonObject = JSONUtil.parseObj(o);
            Blog blog = jsonObject.toBean(Blog.class);
            User user = getAUser(blog);
            blog.setUser(user);
            isBlogLiked(blog);
            res.add(blog);
        }
        return new Result(res, (int) pages1.getTotal());
    }

    private void isBlogLiked(Blog blog) {
        int uid = UserUtil.getCurrentUser().getUid();
        String key = BLOG_LIKED_USER + blog.getBid();
        Double score = stringRedisTemplate.opsForZSet().score(key, String.valueOf(uid));
        blog.setLike(score != null);

    }

    @Override
    public Result getBlogByState(Integer page, Integer count, String keywords) {
        String tableName = "blog";
        if ("".equals(keywords) || keywords.length() == 0) {
            Long size = stringRedisTemplate.opsForList().size(tableName + ":list");
            List<String> list = stringRedisTemplate.opsForList().range(tableName + ":list", (long) (page - 1) * count, (long) (page - 1) * count + count - 1);
            if (list.size() == 0) {
                Page<Blog> pages = query().orderByDesc("createTime").page(new Page<>(page, count));
                List<Blog> records = pages.getRecords();
                for (Blog blog : records) {
                    blog.setUser(getAUser(blog));
                    isBlogLiked(blog);
                }
                if (records.size() != 0) {
                    RLock lock = redissonClient.getLock("lock:blog:" + UserUtil.getCurrentUser().getUid());
                    boolean isLock = lock.tryLock();
                    if(!isLock){
                        return getBlogByState(page,count,keywords);
                    }
                    try{
                        List<Blog> blogs = query().orderByDesc("createTime").list();
                        for (Blog blog : blogs) {
                            stringRedisTemplate.opsForList().rightPush(tableName + ":list", JSONUtil.toJsonStr(blog));
                            blog.setUser(getAUser(blog));
                            isBlogLiked(blog);
                        }
                        stringRedisTemplate.expire(tableName + ":list", BLOGLIST_TTL, TimeUnit.HOURS);

                    }finally {
                        lock.unlock();
                    }

                }
                return new Result(records, (int) pages.getTotal());
            }
            List<Blog> res = new ArrayList<>();
            for (String s : list) {
                Blog blog = JSONUtil.toBean(s, Blog.class);
                blog.setUser(getAUser(blog));
                isBlogLiked(blog);
                res.add(blog);
            }
            return new Result(res, size.intValue());

        }
        Page<Blog> pages1 = redisFurryAndPageQueryUtil.find(keywords, page, count, tableName, Blog.class);
        if (pages1.getTotal() == 0) {
            Page<Blog> pages2 = query().like("blogTitle", keywords).orderByDesc("createTime")
                    .page(new Page<>(page, count));
            List<Blog> records = pages2.getRecords();
            for (Blog record : records) {
                record.setUser(getAUser(record));
                isBlogLiked(record);
            }
            if (records.size() != 0) {
                RLock lock = redissonClient.getLock("lock:blog:hash:" + UserUtil.getCurrentUser().getUid());
                boolean isLock = lock.tryLock();
                if(!isLock){
                    return getBlogByState(page,count,keywords);
                }
                try {
                    List<Blog> list = query().list();
                    for (Blog blog : list) {
                        redisTemplate.opsForHash().put(tableName, blog.getBlogTitle(), JSONUtil.toJsonStr(blog));
                    }
                }finally {
                    lock.unlock();
                }

            }
            return new Result(records, (int) pages2.getTotal());
        }
        return getScrollResult(pages1);
    }

    @Transactional
    @Override
    public boolean updateBlog(Blog blog) {
        int uid = UserUtil.getCurrentUser().getUid();
        blog.setUid(uid);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        blog.setCreateTime(timestamp);
        boolean update = updateById(blog);
        ((BlogService) AopContext.currentProxy()).deleteAllCache(uid);
        redisTemplate.delete(CACHE_BLOG_KEY + blog.getBid());
        return update;

    }

    @Transactional
    @Override
    public RespBean likeThisBlog(Long bid) {
        String key = BLOG_LIKED_USER + bid;
        int uid = UserUtil.getCurrentUser().getUid();
        Double score = stringRedisTemplate.opsForZSet().score(key, String.valueOf(uid));
        if (score == null) {
            boolean isSuccess = update(new UpdateWrapper<Blog>().setSql("liked=liked+1").eq("bid", bid));
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().add(key, String.valueOf(uid), System.currentTimeMillis());
            }
        } else {
            boolean isSuccess = update(new UpdateWrapper<Blog>().setSql("liked=liked-1").eq("bid", bid));
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().remove(key, String.valueOf(uid));
            }
        }
        stringRedisTemplate.delete(CACHE_BLOG_KEY + bid);
        ((BlogService) AopContext.currentProxy()).deleteAllCache(uid);
        return new RespBean("success", "");
    }

    @Override
    public List<UserDTO> queryBlogLikes(Long bid) {
        String key = BLOG_LIKED_USER + bid;
        Set<String> top10 = stringRedisTemplate.opsForZSet().range(key, 0, 9);
        if (top10 == null || top10.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> ids = top10.stream().map(Integer::valueOf).collect(Collectors.toList());
        String join = StrUtil.join(",", ids);
        List<User> users = userService.query()
                .in("uid", ids).last("order by field(uid," + join + ")").list();
        return UserUtil.toUserDTOs(users);
    }

    @Override
    public ScrollResult queryBlogOfFollow(Long max, Integer offset) {
        Integer userId = UserUtil.getCurrentUser().getUid();
        String key = "feed:" + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 6);
        if (typedTuples == null || typedTuples.isEmpty()) {
            return null;
        }
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0;
        int os = 1;
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            ids.add(Long.valueOf(tuple.getValue()));
            long time = tuple.getScore().longValue();
            if (time == minTime) {
                os++;
            } else {
                minTime = time;
                os = 1;
            }
        }

        String idStr = StrUtil.join(",", ids);
        List<Blog> blogs = query().in("bid", ids).last("ORDER BY FIELD(bid," + idStr + ")").list();

        for (Blog blog : blogs) {
            blog.setUser(getAUser(blog));
            isBlogLiked(blog);
        }

        ScrollResult res = new ScrollResult();
        res.setList(blogs);
        res.setOffset(os);
        res.setMinTime(minTime);

        return res;
    }

    @Override
    public Blog getABlog(Long bid) {
        String key = CACHE_BLOG_KEY + bid;
        String blogJson = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(blogJson)) {
            Blog blog = JSONUtil.toBean(blogJson, Blog.class);
            isBlogLiked(blog);
            return blog;
        }
        Blog b = query().eq("bid", bid).one();
        if (b == null) {
            return null;
        }
        isBlogLiked(b);
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(b));
        return b;
    }
    @Transactional
    @Override
    public boolean deleteBlogs(Long[] bids) {
        int uid = UserUtil.getCurrentUser().getUid();
        List<Long> list = new ArrayList<>(Arrays.asList(bids));
        boolean remove = remove(new QueryWrapper<Blog>().in("bid", list));
        ((BlogService) AopContext.currentProxy()).deleteAllCache(uid);
        for (Long bid : bids) {
            ((BlogService) AopContext.currentProxy()).deleteOneCache(bid);
        }
        return remove;

    }


}
