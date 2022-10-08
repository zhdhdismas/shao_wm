package com.shz.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shz.dto.RespBean;
import com.shz.entity.AliPay;
import com.shz.entity.Order;
import com.shz.mapper.OrderMapper;
import com.shz.service.OrderService;
import com.shz.utils.UserUtil;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("/alipay")
public class AliPayController {
    @Resource
    OrderService orderService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @GetMapping("/pay")
    public String pay(AliPay aliPay) {
        AlipayTradePagePayResponse response;
        try {
            //  发起API调用（以创建当面付收款二维码为例）
            response = Factory.Payment.Page()
                    .pay(aliPay.getSubject(), aliPay.getTraceNo(), aliPay.getTotalAmount(), "");
        } catch (Exception e) {
            System.err.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        return response.getBody();
    }
    @PostMapping("/notify")  // 注意这里必须是POST接口
    public String payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                // System.out.println(name + " = " + request.getParameter(name));
            }

            String tradeNo = params.get("out_trade_no");
            String gmtPayment = params.get("gmt_payment");
            String aliPaytradeNo = params.get("trade_no");



            // 支付宝验签
            if (Factory.Payment.Common().verifyNotify(params)) {
                // 验签通过
                System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));

//                 更新订单未已支付
//                orderMapper.updateState(tradeNo, 1, gmtPayment);
                String key = "my_order:"  + tradeNo;
                String s = stringRedisTemplate.opsForValue().get(key);
                if(!StrUtil.isNotBlank(s)){
                    return "error";
                }
                Boolean delete = stringRedisTemplate.delete(key);
                boolean save = orderService.save(JSONUtil.toBean(s, Order.class));
                if(Boolean.TRUE.equals(delete) && save){
                    orderService.update(new UpdateWrapper<Order>().eq("oid",tradeNo)
                            .set("gmtPayment",gmtPayment).set("aliPaytradeNo",aliPaytradeNo)
                           );
                }


            }
        }
        return "success";
    }


}
