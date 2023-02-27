package com.offcn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.endpoint.TokenKeyEndpoint;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.security.KeyPair;

@Configuration
@EnableAuthorizationServer//把当前应用作为认证授权服务器启用
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    //注入数据源对象
    @Autowired
    private DataSource dataSource;
    //把springSecurity认证对象注入
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;
    //密码加密器对象注入
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource(name = "keyProp")
    private KeyProperties keyProperties;

    //读取秘钥对对象方法
    private KeyPair getKeyPair(){
        return new KeyStoreKeyFactory(keyProperties.getKeyStore().getLocation(),keyProperties.getKeyStore().getPassword().toCharArray()).getKeyPair(keyProperties.getKeyStore().getAlias(),keyProperties.getKeyStore().getSecret().toCharArray());

    }

    //定义一个方法：jwt格式解析转换生成器
    private JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //关联秘钥对
        jwtAccessTokenConverter.setKeyPair(getKeyPair());
        return jwtAccessTokenConverter;
    }

    //授权服务器接口的权限认证方式
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients()//允许客户端通过认证之后访问接口
                .passwordEncoder(passwordEncoder)//设置密码加密方式
                .tokenKeyAccess("permitAll()")//令牌相关操作权限  全部具备
                .checkTokenAccess("permitAll()");//允许校验令牌  permitAll()全部权限
    }

    //访问连接到授权服务器账号密码
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        //修改授权服务器使用连接jdbc方式来获取客户端用户信息
     clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
      /*   clients.inMemory()
                .withClient("client1")
                .secret(passwordEncoder.encode("123"))//设置密码  启动加密器加密
                .resourceIds("oauth2-resource","other-resource")//允许操作资源编号
                .scopes("server","app","wx")//账号使用范围
                .authorizedGrantTypes("authorization_code","password","refresh_token","client_credentials","implicit")
                .redirectUris("http://localhost:9007")//登陆成功后跳转地址
                .and()
                .withClient("client2")
                .secret(passwordEncoder.encode("456"))//设置密码  启动加密器加密
                .resourceIds("oauth2-resource")//允许操作资源编号
                .scopes("server","app","wx")//账号使用范围
                .authorizedGrantTypes("authorization_code","password","client_credentials","implicit")
                .redirectUris("http://localhost:9007");//登陆成功后跳转地址 */
    }

    //令牌存储方式
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(new JwtTokenStore(jwtAccessTokenConverter()))//指定令牌的存储方式
                .accessTokenConverter(jwtAccessTokenConverter())//请求验证令牌使用jwt令牌解析转换器处理
                .userDetailsService(userDetailsService)//关联springSecurity自定义认证对象
                .authenticationManager(authenticationManager)//关联springSecurity认证管理对象
                .allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.POST);//接口支持请求方法
    }

    //声明一个暴露获取公钥接口
    @Bean
    public TokenKeyEndpoint tokenKeyEndpoint(){
        return new TokenKeyEndpoint(jwtAccessTokenConverter());
    }
}
