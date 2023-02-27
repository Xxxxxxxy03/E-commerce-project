package com.offcn.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {

    //有效期为
    public static final Long JWT_TTL = 36000000L;//60 *60 *1000*10 十小时

    //Jwt令牌密码
    public static final String JWT_KEY = "ujiuye123456";
    //生成jwt令牌
    /**
     *
     * @param id 编号
     * @param subject  主题
     * @param ttlMillis  令牌的有效期 单位是毫秒
     * @return  令牌字符串
     */
    public static String createJWT(String id, String subject, Long ttlMillis){
        //指定签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //当前系统时间
        long nowMillis = System.currentTimeMillis();
        //令牌签发时间
        Date now = new Date(nowMillis);

        //判断用户输入有效期是否为空
        if(ttlMillis==null){
            //设置令牌有效期为默认有效期 1 小时
            ttlMillis=JwtUtil.JWT_TTL;
        }

        //计算过期时间毫秒值
        long expMillis = nowMillis + ttlMillis;
        //转换日期时间对象
        Date expDate = new Date(expMillis);

        //生成秘钥
        SecretKey secretKey = generalKey();

        //调用工具类，生成Jwt令牌
        JwtBuilder builder = Jwts.builder()
                .setId(id)//唯一的ID
                .setSubject(subject)//主题可以是JSON数据
                .setIssuer("admin")//签发者
                .setIssuedAt(now)//签发时间
                .signWith(signatureAlgorithm, secretKey)//签名算法以及密匙
                .setExpiration(expDate); //设置过期时间
        return builder.compact();
    }

    //加密密码
    public static SecretKey generalKey(){
        //把明文密码做成base64转码
        byte[] encodedKey = Base64.getEncoder().encode(JwtUtil.JWT_KEY.getBytes());
        //把base64后的数组执行加密
        SecretKey key = new SecretKeySpec(encodedKey,0, encodedKey.length,"AES");
        return key;
    }


   //解析验证令牌
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    //public static void main(String[] args) throws Exception {
    //    String jwt = JwtUtil.createJWT("1001", "测试令牌", null);
    //    System.out.println(jwt);
    //    Claims claims = JwtUtil.parseJWT(jwt);
    //    System.out.println(claims);
    //}
}