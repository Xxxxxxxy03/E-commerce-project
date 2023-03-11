package com.offcn.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    //定义令牌头
    private static final String AUTHORIZE_TOKEN = "Authorization";

    //把授权服务器登录页地址,定义成一个变量
    @Value("${USER_LOGIN_URL}")
    private String USER_LOGIN_URL ;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求，响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //获取网关请求路径
        String path = request.getURI().getPath();
        if (path.startsWith("/api/user/login") || path.startsWith("/api/brand/search")|| path.startsWith("/api/seckillGoods/getMenus")|| path.startsWith("/api/seckillOrder/one")||path.startsWith("/api/seckillOrder/addtest")) {
            //在白名单地址直接放行
            return chain.filter(exchange);
        }
        //从请求对象获取令牌
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        if (StringUtils.isEmpty(token)) {
            //再次尝试从请求参数获取令牌
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }

        //判断从请求头，请求参数都没有获取到令牌，尝试从cookie读取令牌
        if (StringUtils.isEmpty(token)) {
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            //判断cookie是否为空
            if (cookie != null) {
                token = cookie.getValue();
            }
        }
        //判断两次获取令牌依然为空，设置拒绝转发，拒绝访问
        if (StringUtils.isEmpty(token)) {
            //设置响应状态码
            // response.setStatusCode(HttpStatus.FORBIDDEN);//403禁止访问
            // return response.setComplete();

        //    获取原始访问地址
            String srcUrl = request.getURI().toString();
            System.out.println("原始访问地址："+srcUrl);

            String encodeSrcUrl = "";
            try {
                //把原始访问地址做urlEncode
                encodeSrcUrl = URLEncoder.encode(srcUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //    调用跳转方法
            return this.needAuthorization(USER_LOGIN_URL + "?FROM=" + encodeSrcUrl, exchange);
        }
        //如果令牌不为空，尝试解析令牌
        try {
            // Claims claims = JwtUtil.parseJWT(token);
            //判断令牌是否有Bearer 开头
            if (!StringUtils.isEmpty(token)) {
                //截取令牌前6位，判断是否是Bearer
                if (!token.substring(0, 6).toLowerCase().equals("bearer")) {
                    //手动增加Bearer开头
                    token = "Bearer " + token;
                }

            }
            //把令牌载荷转发到对应微服务
            request.mutate().header(AUTHORIZE_TOKEN, token);

            //解析成功，继续路由转发
            return chain.filter(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            //解析失败禁止访问
            // response.setStatusCode(HttpStatus.FORBIDDEN);//403禁止访问
            // return response.setComplete();

            return this.needAuthorization(USER_LOGIN_URL, exchange);
        }

    }

    //单独定义一个方法，没有权限的时候，我们要跳转到指定地址
    private Mono<Void> needAuthorization(String url, ServerWebExchange exchange) {
    //    获取到响应对象
        ServerHttpResponse response = exchange.getResponse();
        //设置响应状态码为跳转码   303
        response.setStatusCode(HttpStatus.SEE_OTHER);//告诉浏览器要请求另外一个地址
        //设置响应的另外请求的地址
        response.getHeaders().set("Location",url);

        return response.setComplete();
    }

    //过滤器执行顺序
    @Override
    public int getOrder() {
        return 0;
    }
}
