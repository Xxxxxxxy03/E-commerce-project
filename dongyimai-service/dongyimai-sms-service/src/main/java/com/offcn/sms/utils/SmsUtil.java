package com.offcn.sms.utils;

import com.offcn.utils.HttpUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SmsUtil {

    //读取配置文件定义的参数1：appcode
    @Value("${sms.appcode}")
    private String appcode;

    //读取配置文件订单参数2：tpl_id
    @Value("${sms.tpl_id}")
    private String tpl_id;

    //定义短信发送接口的主机地址
    private String host="http://dingxin.market.alicloudapi.com";
    //短信接口路径
    private String path="/dx/sendSms";
    //发送http请求方法
    private String method="POST";



    /**
     * 定义短信发送方法
     * @param mobile  接收的手机号码
     * @param code    验证码的实际值
     * @return  发送结果字符串
     */
    public String smsSend(String mobile,String code){

        //创建一个Map封装请求头数据
        Map<String, String> heads=new HashMap<>();
        //封装身份验证的请求头参数
        heads.put("Authorization","APPCODE "+appcode);

        //创建一个Map封装请求参数数据
        Map<String, String> querys=new HashMap<>();
        //封装参数1：mobile 接收短信的手机号码
        querys.put("mobile",mobile);
        //封装参数2:param 模板变量,如果模板内有多个变量使用","分开
        querys.put("param","code:"+code);
        //封装参数3：tpl_id 模板编号
        querys.put("tpl_id",tpl_id);

        //创建一个map封装请求体参数
        Map<String, String> bodys=new HashMap<>();

        try {
            //使用工具类HttpUtils发出 post请求
            HttpResponse response = HttpUtils.doPost(host, path, method, heads, querys, bodys);
            //http请求判断响应结果，使用响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            //判断响应状态吗是否等于200 成功
            if(statusCode==200){
                //获取响应数据封装对象
                HttpEntity entity = response.getEntity();
                //把响应封装对象转换成字符串
                String responseStr = EntityUtils.toString(entity, "UTF-8");
                return responseStr;
            }else {
                System.out.println("发送短信失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
