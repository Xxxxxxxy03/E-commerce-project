package com.offcn.pay.controller;

import com.alibaba.fastjson.JSON;
import com.offcn.entity.Result;
import com.offcn.entity.StatusCode;
import com.offcn.pay.service.PayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    //注入服务
    @Autowired
    private PayService payService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;
    //预下单方法
    @RequestMapping("/create/native")
    public Result<Map> createNative(@RequestParam Map<String, String> parameters) {

        //调用服务
        Map<String, String> map = payService.createNative(parameters);
        return new Result<>(true, StatusCode.OK, "预下单成功！", map);
    }

    //定义接收支付宝异步通知方法
    @RequestMapping("/notify/url")
    public Result reciveAliPayNotify(HttpServletRequest request){
        HashMap<String, String> map = new HashMap<>();
        //从request获取全部请求参数名称
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()){
        //    把指针指向下一个节点
            String paramName = parameterNames.nextElement();

            map.put(paramName,request.getParameter(paramName));

            System.out.println("name:"+paramName+" value"+request.getParameter(paramName));
        }
        //把map转换json字符串
        String jsonStr = JSON.toJSONString(map);
        //调用消息工具对象
        rabbitTemplate.convertAndSend(env.getProperty("mq.pay.exchange.seckillorder"),env.getProperty("mq.pay.routing.seckillkey"),jsonStr);
        return new Result(true,StatusCode.OK,"感谢马云",map);
    }

    //关闭交易
    @PostMapping("/closepay")
    public Result<Map<String,String>> closePay(@RequestParam Long out_trade_no){
        Map<String, String> map = payService.closePay(out_trade_no);
        return new Result<>(true,StatusCode.OK,"关闭交易成功",map);
    }
}
