package com.offcn.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.offcn.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private AlipayClient alipayClient;

    @Value("${alipay.notifyUrl}")
    private String notifyUrl;

    @Override
    public Map<String, String> createNative(Map<String, String> parameters) {

        HashMap<String, String> result = new HashMap<>();
        //创建封装预下单请求参数封装对象
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();

        //设置异步通知地址
        request.setNotifyUrl(notifyUrl);

        JSONObject jsonObject = new JSONObject();
        //封装参数一：订单号
        jsonObject.put("out_trade_no", parameters.get("out_trade_no"));
        //封装参数二：下单金额
        jsonObject.put("total_amount", parameters.get("total_fee"));
        //    封装参数3：商品名称
        jsonObject.put("subject", "东易买秒杀测试商品");

        //封装参数4：body 传递预下单的用户名
        jsonObject.put("body", parameters.get("username"));
        //关联设置参数
        request.setBizContent(jsonObject.toJSONString());


        //    调用客户端对象，打出预下单请求
        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            //获取响应的二维码地址
            result.put("qrcode", response.getQrCode());
            //订单号
            result.put("out_trade_no", response.getOutTradeNo());
            //金额
            result.put("total_fee", parameters.get("total_fee"));
        } else {
            System.out.println("预下单异常：" + response.getBody());
        }
        return result;
    }

    @Override
    public Map<String, String> closePay(Long out_trade_no) {
        HashMap<String, String> map = new HashMap<>();
        //创建关闭预下单支付请求参数封装对象
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        //设置参数
        request.setBizContent("{" +
                "    \"out_trade_no\":\""+out_trade_no+"\"," +
                "    \"trade_no\":\"\"}"); //设置业务参数

        //发出关闭交易请求
        AlipayTradeCancelResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            //把交易订单号封装到map
            map.put("out_trade_no",out_trade_no + "");
            map.put("code",response.getCode());
        }else{
            System.out.println("关闭交易失败："+ response.getBody());
        }
        return map;
    }
}
