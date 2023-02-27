package com.offcn.pay.controller;

import com.offcn.entity.Result;
import com.offcn.entity.StatusCode;
import com.offcn.order.feign.OrderFeign;
import com.offcn.order.feign.PayLogFeign;
import com.offcn.order.pojo.PayLog;
import com.offcn.pay.service.AliPayService;
import com.offcn.utils.IdWorker;
import com.offcn.utils.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private PayLogFeign payLogFeign;

    @Autowired
    private TokenDecode tokenDecode;

    @Autowired
    private OrderFeign orderFeign;

    @RequestMapping("/createNative")
    public Result createNative() {

        String userName = tokenDecode.getUserINfo().get("user_name");
        Result result = payLogFeign.searchPayLogFromRedis(userName);
        System.out.println("当前登录的用户：" + userName);
        // String userId = "xy";
        // Result result = payLogFeign.searchPayLogFromRedis(userId);

        if(result != null && result.isFlag()){
           PayLog payLog = (PayLog) result.getData();
            try {
                Map map = aliPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
                return new Result(true, StatusCode.OK, "预下单成功！", map);
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, StatusCode.ERROR, "预下单失败！");
            }
        }
        return new Result(false, StatusCode.ERROR, "预下单失败！");
    }

    //    查询支付状态请求方法
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        int x = 0;
        while (true){
        //    调用服务查询交易状态
            Map<String,String> map = aliPayService.queryPayStatus(out_trade_no);

        //    判断map是否为空
            if(map == null){
                result = new Result(false,StatusCode.ERROR,"调用查询交易状态服务失败！");
                break;
            }else{

            //    不为空，继续判断查询结果
                if(map != null && map.get("tradeStatus") != null && map.get("tradeStatus").equals("TRADE_SUCCESS")){
                    result = new Result(true,StatusCode.OK,"查询交易状态成功！");

                    orderFeign.updateOrderStatus(map.get("out_trade_no"),map.get("trade_no"));
                    break;
                }
                if(map != null && map.get("tradeStatus") != null && map.get("tradeStatus").equals("TRADE_FINISHED")){
                    result = new Result(false,StatusCode.ERROR,"交易完成！");
                    break;
                }
                if(map != null && map.get("tradeStatus") != null && map.get("tradeStatus").equals("TRADE_CLOSED")){
                    result = new Result(false,StatusCode.ERROR,"查询交易退款！");
                    break;
                }
                //每次查询完，x累加1
                x++;

                //判断x最大值
                if(x > 10 ){
                    result = new Result(false,StatusCode.ERROR,"交易查询超时！");
                    break;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
            return result;
    }
}
