package com.shz.controller;


import com.shz.dto.RespBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.shz.utils.OSSUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/upload")
public class UploadController {
//    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    @PostMapping("/img")
    public RespBean uploadImg(HttpServletRequest req,@RequestParam("file") MultipartFile image) {
        String s = OSSUtil.uploadImg(image);
        return new RespBean("success",s);
        //传统做法写入本地服务器中
//        StringBuffer url = new StringBuffer();
//        String filePath = "/menuimg/" + sdf.format(new Date ());
//        String imgFolderPath = req.getServletContext().getRealPath(filePath);
//        File imgFolder = new File(imgFolderPath);
//        if (!imgFolder.exists()) {
//            imgFolder.mkdirs();
//        }
//        url.append(req.getScheme())
//                .append("://")
//                .append(req.getServerName())
//                .append(":")
//                .append(req.getServerPort())
//                .append(req.getContextPath())
//                .append(filePath);
//        String imgName = UUID.randomUUID() + "_" + image.getOriginalFilename().replaceAll(" ", "");
//        try {
//            IOUtils.write(image.getBytes(), new FileOutputStream (new File(imgFolder, imgName)));
//            url.append("/").append(imgName);
//            return new RespBean("success", filePath+"/"+imgName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new RespBean("error", "上传失败!");
    }
    @DeleteMapping("/delete")
    public RespBean deleteImg(String imgUrl){
        boolean isSuccess = OSSUtil.deleteFile(imgUrl);
        String res=isSuccess?"success":"error";
        return new RespBean(res,"已删除文件");
    }
    @PostMapping("/imgMany")
    public List<String> UploadImgMany(HttpServletRequest req,@RequestParam("file") MultipartFile[] multipartFile){
        List<String> urls=new ArrayList<>();
        for (int i = 0; i < multipartFile.length; i++) {
            String s = OSSUtil.uploadImg(multipartFile[i]);
            System.out.println(s);
           String filePath="http://"+OSSUtil.SUFFER_URL+"/"+s;
            urls.add(s);
        }
        return urls;
    }

}
