package com.shz.utils;
//下列键为热点键没有做数据一致，其余键都做了数据一致
//feed:用来将博客投喂给粉丝 从feed流中获取博客id查库 zset
//sss: + seckill: 秒杀业务 在redis中实时用来存订单，判断是否重复下单  stream
//icr: 今日生成的博客或订单id数量  string(increment)
//blog:liked: 为喜欢这个博客的用户id按顺序前十位  zset
//user:sign: 月签到 bitmap（String）

//其他键都做了一致性
//其中分页查询的history中的zset和hash要保持过期时间的一致性，因为是根据hash来进行查询的
public class RedisConstants {
    public static final String CACHE_MENU_KEY = "cache:menu:";
    public static final Long MENUS_TTL = 200L;
    public static final Long TID_TTL = 180L;
    public static final Long MAX_LIMIT_CATEGORY = 10L;
    public static final Long CATEGORY_TTL = 180L;
    public static final Long LEVEL_TTL = 180L;
    public static final Long ADDRESS_TTL = 100L;
    public static final String CACHE_BLOG_KEY = "cache:blog:";
    public static final String CACHE_USER_KEY = "cache:user:";
    public static final Long CACHE_USER_TTL = 100L;
    public static final Long MY_BLOGLIST_TTL = 5L;
    public static final Long BLOGLIST_TTL = 5L;
    public static final String USER_SIGN_KEY = "user:sign:";
    public static final String USER_TEXT_KEY = "user:text:";
    public static final Long USER_TEXT_KEY_TTL = 5L;
    public static final String BLOG_LIKED_USER = "blog:liked:";
    public static final String CACHE_MENU_DETAIL_KEY = "cache:menu_detail:";


}
