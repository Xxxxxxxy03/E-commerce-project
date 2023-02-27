package com.offcn.pay.service;

import java.util.Map;

public interface AliPayService {

    /**
     *  定义预下单方法
     * @param out_trade_no 交易订单编号
     * @param total_fee  金额
     * @return
     */
    Map createNative(String out_trade_no,String total_fee);

    /**
     * 查询支付状态
     * @param out_trade_no
     */
    Map queryPayStatus(String out_trade_no);
}
