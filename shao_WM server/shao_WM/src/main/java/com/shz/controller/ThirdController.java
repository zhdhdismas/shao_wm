package com.shz.controller;

import com.shz.utils.HttpClientHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/map")
public class ThirdController {
    @GetMapping("/getSearch/{keywords}")
    protected Object getSearch(@PathVariable String keywords) throws IOException {
        String url =
                "https://restapi.amap.com/v3/assistant/inputtips?key=af5673ae5963621f939460b6f66040a6&keywords=";
        String text = URLEncoder.encode(keywords, "UTF-8");
        url += text;
//        HttpClientHelper.sendPost(url);
        return HttpClientHelper.sendGet(url);
    }

}
