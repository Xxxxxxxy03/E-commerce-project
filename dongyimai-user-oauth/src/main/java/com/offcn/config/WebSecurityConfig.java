package com.offcn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity//开启SpringSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //配置SpringSecurity拦截规则，登录窗口
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
                .anyRequest()//任何请求不拦截
                .and()
                .formLogin()//登录界面
                .loginPage("/oauth/login")//指定自定义登录框
                .and()
                .csrf().disable();//跨站攻击防御禁用:可以自定义登录框

       /* http.authorizeRequests()
                .antMatchers("/demo1","/demo2").permitAll()//放行
                .anyRequest()
                .authenticated()//需要认证
                .and().formLogin()
                .and()
                .csrf().disable();*/
    }

    //springSecurity的自定义认证管理对象重写
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    //定义登录账号密码
   /*  @Bean(name = "userDetailService")
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return createUserDetailService();
    }
 */
    //单独定义方法用来创建登录账号和密码
  /*   private UserDetailsService createUserDetailService() {
        //创建一个集合，存储多个账号信息
        List<UserDetails> list = new ArrayList<>();
        //创建用户1
        UserDetails user1 = User.withUsername("admin").password(passwordEncoder().encode("123")).authorities("ADMIN", "USER").build();
        UserDetails user2 = User.withUsername("user1").password(passwordEncoder().encode("123")).authorities("ADMIN", "USER").build();
        UserDetails user3 = User.withUsername("user2").password(passwordEncoder().encode("456")).authorities("USER").build();
        //用户加入集合
        list.add(user1);
        list.add(user2);
        list.add(user3);
        return new InMemoryUserDetailsManager(list);
    } */
    //声明一个Bcrypt密码加密器对象
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
