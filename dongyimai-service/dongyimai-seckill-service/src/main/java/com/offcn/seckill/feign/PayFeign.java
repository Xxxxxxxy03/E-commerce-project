package com.offcn.seckill.feign;

import com.offcn.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "pay-seckill",path = "/pay")
public interface PayFeign {

    //关闭交易
    @PostMapping("/closepay")
    public Result<Map<String,String>> closePay(@RequestParam Long out_trade_no);
}
