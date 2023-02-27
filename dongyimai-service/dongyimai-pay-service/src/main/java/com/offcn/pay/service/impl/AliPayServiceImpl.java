package com.offcn.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.offcn.pay.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AliPayServiceImpl implements AliPayService {

    //注入连接到支付宝网关客户端对象
    @Autowired
    private AlipayClient alipayClient;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //创建map封装返回数据
        HashMap<String, String> map = new HashMap<>();

        //创建预下单请求对象
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        JSONObject jsonObject = new JSONObject();
        //封装订单号
        jsonObject.put("out_trade_no", out_trade_no);
        //封装预下单金额
        //处理金额，从元转换成分 除法运算 除数是100
        //把字符串金额转换整数
        long total = Long.parseLong(total_fee);
        BigDecimal bigDecimal_Fen = new BigDecimal(total);
        BigDecimal bigDecimal_CS = new BigDecimal(100);
        BigDecimal bigDecimal_Yuan = bigDecimal_Fen.divide(bigDecimal_CS);
        System.out.println("预下单金额：" + bigDecimal_Yuan.doubleValue());
        jsonObject.put("total_amount", bigDecimal_Yuan.doubleValue());
        //封装参数三 商品名称
        jsonObject.put("subject", "电商平台测试商品");
        //把json对象封装请求参数封装对象
        request.setBizContent(jsonObject.toJSONString());

        try {
            //调用连接到支付宝客户端对象，发出预下单请求
            AlipayTradePrecreateResponse response = alipayClient.execute(request);

            //判断预下单是否成功
            if (response.isSuccess()) {
                // 获取支付宝网关响应的数据
                map.put("qrcode", response.getQrCode());
                map.put("out_trade_no", out_trade_no);
                map.put("total_fee", total_fee);
            } else {
                System.out.println("预下单失败:" + response.getBody());
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {

        HashMap<String, String> map = new HashMap<>();
        //创建查询交易状态请求参数封装对象
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        //设置查询参数
        //out_trade_no:电商平台生成交易订单号
        //trade_no:支付宝平台生成交易流水号
        //至少写一个
        request.setBizContent("{" +
                "    \"out_trade_no\":\"" + out_trade_no + "\"," +
                "    \"trade_no\":\"\"}");
        //调用支付宝客户端对象发出请求
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                //获取支付状态
                String tradeStatus = response.getTradeStatus();

                //    封装支付状态
                map.put("tradeStatus", tradeStatus);
                //    封装订单号
                map.put("out_trade_no", out_trade_no);
                //    支付宝返回交易流水号
                map.put("trade_no", response.getTradeNo());
            } else {
                System.out.println("查询支付状态失败：" + response.getBody());
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return map;
    }
}
