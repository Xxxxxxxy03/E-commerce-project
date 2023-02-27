package com.offcn;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

public class TestJwtToken {

    @Test
    public void testCreateJwtToken(){

        //声明秘钥文件文件名
        String key_location = "dongyimai.jks";

        //秘钥库密码
        String key_password = "dongyimai";

        //秘钥密码
        String keypwd = "dongyimai";

        //秘钥别名
        String alias = "dongyimai";

        //使用资源文件管理器读取秘钥文件
        ClassPathResource resource = new ClassPathResource(key_location);

        //使用密钥工厂对象读取秘钥
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, key_password.toCharArray());

        //从秘钥库获取指定别名的秘钥对密码
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, keypwd.toCharArray());

        //从秘钥对获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        //创建一个map,封装自定义数据
        Map<String, Object> map = new HashMap<>();
        map.put("id",1);
        map.put("name","ujiuye");
        map.put("roles","ADMIN");
        //把map转换为json字符串
        String json = JSON.toJSONString(map);

        //使用私钥生成签证的jwt令牌
        Jwt jwt = JwtHelper.encode(json, new RsaSigner(privateKey));
        //调用令牌对象,获取token
        String token = jwt.getEncoded();
        System.out.println(token);
    }

    //使用公钥验证jwt令牌
    @Test
    public void testParseToken(){
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkFETUlOIiwibmFtZSI6InVqaXV5ZSIsImlkIjoxfQ.Fq681H9GkVaH0HJWSkse1ORIXL3e_1DW71D7TMk8w0c8j6QthuKs7zOHplPm_jw6_c8bBuSgys0sfsHCc4DdLLo8inxiTVoWb4DSg3EG-OTgng3O86gcSBldJQmSmN1FKMrAQ-2hr3JJV8s63raCp3MraIij5JQ3FN0hbXFTvj9OgyJiG4W8tI-lVcf5-h8wT424XMx5AykMxny-XUxj4dcrXUWHSOxTa4aY12QqPYDbAfBjmfotrMAQMUZmgNxPcaGeMZluLYQ0c-lgxW9a8_VzbyVgTR6gbudnMhKsGUtaJkImg7Cp2nl-ha_x6uq5ht9LKuVQ0Q2cfX_B_y_zxA";
        // String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsib2F1dGgyLXJlc291cmNlIiwib3RoZXItcmVzb3VyY2UiXSwidXNlcl9uYW1lIjoiYWRtaW4iLCJzY29wZSI6WyJzZXJ2ZXIiLCJhcHAiLCJ3eCJdLCJleHAiOjE2NzY2NjkxODcsImF1dGhvcml0aWVzIjpbIlVTRVIiLCJBRE1JTiJdLCJqdGkiOiJlMzg4Y2I2NS02MzljLTRjMGItYmQ3MC1kYjljNDc0MzMwZWEiLCJjbGllbnRfaWQiOiJjbGllbnQxIn0.C1I43NZZSR753wyvM2UVlHeaBsKHf7b6fOshgTkfIB-KlEqaM1w2p9NmzCPVEIuJZqxMlqfEfd8ZoMe0eesNw0n4RZRY9Wa_U1m1ZXuvEYwPSVvrgjLbQhkOFVjVR322n0ybf6Lb4rMp2aFT4hfKcTlNmG_WBHV9WuO0bsSbz7eWQVE-cboZhzQAxnXYMn57a7qVF9VT1bGVstr2f1Soq0A_KGlcHDKjbPeqfLITph81Ux9rfevdogzjlFFO6NPsELm8GowOyY6cqez7B8EC0l1A_YZoizpR_vEAsjNpVsC3KZgNGxVqt4NOjkwYLZCie8ikX5Qtry4Ce7FAezTtjA";
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt4SFMXk28yMkbRHlOjAgUMgPmSN+eXjkDXo8iwgn4Rq68uI4hlgFbSfh5p8adLMC6QGKyKzm2nYD6tMAKKbQoYnll176QvMhvOoTFD7FMywVhA6O7ryEXXGF+sURshfIiLL8Ar5Lb2fpQTysU610CO/5nmTEgVClYMy6kTLIQcFYtvkahLuGS67p1BkKoC+99PQrYBwFWKfs6qEfk/t4V+tG4GXw6k1jgl0IARU62HgfFU09jbygHTiu5WzJw/nPqKekEM/v+xKPwTweu6LD7bzlaM1wh2lFtZEV6ldrMaKFRJ21YyLhl71pcd77ZjFN6e+RV8jAioSXiPJ97swsTQIDAQAB-----END PUBLIC KEY-----";
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));

        //获取载荷
        String claims = jwt.getClaims();
        System.out.println(claims);
    }
}
