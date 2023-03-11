package com.offcn.pay.service;

import java.util.Map;

public interface PayService {

    //预下单方法
    Map<String,String> createNative(Map<String,String> parameters);

    //关闭支付
    Map<String,String> closePay(Long out_trade_no);
}
