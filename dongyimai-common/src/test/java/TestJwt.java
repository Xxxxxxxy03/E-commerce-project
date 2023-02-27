import io.jsonwebtoken.*;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;

public class TestJwt {

    @Test
    public void testCreateJwt(){
        JwtBuilder builder= Jwts.builder()
                .setId("1001")//设置唯一编号
                .setSubject("测试令牌")//主题
                .setIssuer("奕老师")//签发人
                .setIssuedAt(new Date())//设置签发日期
                .signWith(SignatureAlgorithm.HS256,"ujiuye");//设置签名使用HS256算法，并设置SecretKey(字符串)
        //构建并返回一个字符串
        System.out.println( builder.compact());
    }

    @Test
    public void testParseToken(){
        //String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMDAyIiwic3ViIjoi5rWL6K-V5Luk54mMIiwiZXhwIjoxNjc2NTgxMzIxfQ.WLnWDDbN9XrQ15lFiMkz2NbPMht_Vf4dD9A-UNj0lLc";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMDAzIiwic3ViIjoi6Ieq5a6a5LmJ5Luk54mMIiwiaXNzIjoi5aWV6ICB5biIIiwiYWRkcmVzcyI6IuWMl-S6rOaYjOW5s-WfuuWcsCIsInBob25lIjoiNDU0NTQxMjEiLCJuYW1lIjoi5LyY5bCx5LiaIn0.TUgiyWz3rqqVBaR4NGTmpKHaoo_jGVwOmWa8srz9Bks";
        //String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMDAyIiwic3ViIjoi5rWL6K-V5Luk54mMIiwiZXhwIjoxNjc2NTMxNjc2fQ.SEe5Wug_VplobiWtaQmqzUGQlphHnj5rgCMKB8awMp0";
        //获取令牌解析工具对象
        JwtParser parser = Jwts.parser();
        //解析令牌获取载荷对象
        Claims claims = parser.setSigningKey("ujiuye")
                .parseClaimsJws(token)//关联令牌
                .getBody();//获取载荷
        System.out.println(claims);

    }

    //测试令牌过期时间设置
    @Test
    public void  testExpire(){
        JwtBuilder builder = Jwts.builder()
                .setId("1002")
                .setSubject("测试令牌")
                .signWith(SignatureAlgorithm.HS256, "ujiuye")
                //.setExpire(new Date(System.currentTimeMillis()+10*1000*100*50));
                .setExpiration(new Date());
        System.out.println(builder.compact());
    }

    //测试在令牌添加自定义数据
    @Test
    public void testCustomer(){
        //创建map封装自定义数据
        HashMap<String, Object> map = new HashMap<>();
        map.put("name","优就业");
        map.put("address","北京昌平基地");
        map.put("phone","45454121");

        String compact = Jwts.builder()
                .setId("1003")
                .setSubject("自定义令牌")
                .setIssuer("奕老师")
                .signWith(SignatureAlgorithm.HS256, "ujiuye")
                .addClaims(map)//添加自定义数据
                .compact();
        System.out.println(compact);

    }
}
