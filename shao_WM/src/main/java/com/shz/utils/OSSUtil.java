package com.shz.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Author: 洛尘大大
 * Description: OSSUtil封装类
 * Date: 2022/1/16
 */
public class OSSUtil {
    private final static String ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
    private final static String ACCESS_KEY_ID = "LTAI5tPzabw9rULKkVQE5yjP";
    private final static String ACCESS_KEY_SECRET = "aP3fyxtxmrTm1NRfXB1h3VccfGtrCj";
    private final static String BUCKET_NAME = "shzzz";
    public final static String SUFFER_URL = "shzzz.oss-cn-hangzhou.aliyuncs.com";
    private final static String PARENT_DIR = "img";


    /**
     * 获取OSS对象
     *
     * @return 返回OSS对象
     */
    public static OSS getOSSClient() {
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        // 验证仓库是否存在
        if (!ossClient.doesBucketExist(BUCKET_NAME)) {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(BUCKET_NAME);
            // 设置成可读写的
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicReadWrite);
            // 创建
            ossClient.createBucket(createBucketRequest);
        }
        return ossClient;
    }

    /**
     * 删除图片文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        ossClient.deleteObject(BUCKET_NAME, filePath);
        ossClient.shutdown();
        return true;
    }

    /**
     * 上传图片文件
     *
     * @param multipartFile 文件
     * @return 返回图片上传路径
     */
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static String uploadImg(MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        // 获取文件后缀
        int x = filename.lastIndexOf(".");
        String etx = "";
        if (x != -1) {
            etx = filename.substring(x);
        }
        //一个文件夹的最大容量为Integer.MAX_VALUE就是2的32次方-1个,所以按日期分文件夹比较好
        //上传的文件路径
        String filePath = PARENT_DIR + "/one/" + sdf.format(new Date()) + "/" + UUID.randomUUID().toString().replace("-", "") + etx;
        OSS ossClient = getOSSClient();


        String imgPath = "";
        try {

            imgPath = SUFFER_URL + filePath;
            ossClient.putObject(BUCKET_NAME, filePath, new ByteArrayInputStream(multipartFile.getBytes()));
//            // 将图片缩放为固定宽高100 px。
//            String style = "image/resize,m_fixed,w_650,h_400";
//            GetObjectRequest request = new GetObjectRequest(BUCKET_NAME, filePath);
//            request.setProcess(style);
//            ObjectMetadata object = ossClient.getObject(request, new File("scale.jpg"));
//            //创建file对象
//            File file = new File("scale.jpg");
//            //(File转MultipartFile)
//            //获取file对象的文件输入流
//            FileInputStream input = new FileInputStream(file);
//            MultipartFile multipartFiles =new MockMultipartFile("shz", file.getName(), "text/plain", IOUtils.toByteArray(input));
//            ossClient.putObject(BUCKET_NAME, filePath, new ByteArrayInputStream(multipartFiles.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭OSS对象
            ossClient.shutdown();
        }
        return filePath;
    }


}
