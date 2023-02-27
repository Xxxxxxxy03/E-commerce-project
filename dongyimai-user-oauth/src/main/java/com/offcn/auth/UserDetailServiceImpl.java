package com.offcn.auth;

import com.offcn.entity.Result;
import com.offcn.user.feign.UserFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component(value = "userDetailService")
public class UserDetailServiceImpl implements UserDetailsService {

    //注入用户为服务feign接口
    @Autowired
    private UserFeign userFeign;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Result<com.offcn.user.pojo.User> result = userFeign.findByUsername(username);
        if(result !=null && result.isFlag()){
            //获取用户对象
            com.offcn.user.pojo.User loginUser = result.getData();
            //创建权限字符串
            String perms = "ADMIN,USER";
            //获取用户密码
            String password = loginUser.getPassword();
            return new User(username,password,AuthorityUtils.commaSeparatedStringToAuthorityList(perms));

            // return new JdbcClientDetailsService();
        }
       /*  //创建临时测试密码
        String pwd = new BCryptPasswordEncoder().encode("123");
        //创建权限字符串
        String perms = "ADMIN,USER"; */

        return null;
    }
}
