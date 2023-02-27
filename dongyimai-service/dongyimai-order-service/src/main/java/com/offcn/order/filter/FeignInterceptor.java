package com.offcn.order.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    //    判断是否为空
        if(servletRequestAttributes!=null){
        //    读取对象
            HttpServletRequest request = servletRequestAttributes.getRequest();
        //    获取全部请求头名称集合
            Enumeration<String> headerNames = request.getHeaderNames();
        //    遍历请求头名称枚举对象
            while (headerNames.hasMoreElements()){
            //    把指针指向下一个节点
                String name = headerNames.nextElement();
            //    获取对应请求头的值
                String value = request.getHeader(name);
            //    把获取到请求头数据，封装给feign调用对象 requestTemplate
                requestTemplate.header(name,value);
            }
        }
    }
}
