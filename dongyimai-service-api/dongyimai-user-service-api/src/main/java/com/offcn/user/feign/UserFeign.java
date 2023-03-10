package com.offcn.user.feign;

import com.offcn.entity.Result;
import com.offcn.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user",path="user")
public interface UserFeign {

    //根据登录用户名获取信息
    @GetMapping("/findByUsername/{username}")
    public Result<User> findByUsername(@PathVariable("username") String username);

    /***
     * 添加用户积分
     * @param points
     * @return
     */
    @GetMapping(value = "/points/add")
    public Result addPoints(@RequestParam(value = "points")Integer points);
}
