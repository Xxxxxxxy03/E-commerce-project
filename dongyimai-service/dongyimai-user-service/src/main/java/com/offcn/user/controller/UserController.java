package com.offcn.user.controller;

import com.alibaba.fastjson.JSON;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.entity.StatusCode;
import com.offcn.user.pojo.User;
import com.offcn.user.service.UserService;
import com.offcn.utils.BCrypt;
import com.offcn.utils.JwtUtil;
import com.offcn.utils.PhoneFormatCheckUtils;
import com.offcn.utils.TokenDecode;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/****
 * @Author:ujiuye
 * @Description:
 * @Date 2021/2/1 14:19
 *****/
@Api(tags = "UserController")
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenDecode tokenDecode;

    /***
     * User分页条件搜索实现
     * @param user
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "User条件分页查询",notes = "分页条件查询User方法详情",tags = {"UserController"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true)
    })
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageResult<User>> findPage(@RequestBody(required = false) @ApiParam(name = "User对象",value = "传入JSON数据",required = false) User user, @PathVariable  int page, @PathVariable  int size){
        //调用UserService实现分页条件查询User
        PageResult<User> pageResult = userService.findPage(user, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /***
     * User分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @ApiOperation(value = "User分页查询",notes = "分页查询User方法详情",tags = {"UserController"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true)
    })
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageResult<User>> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用UserService实现分页查询User
        PageResult<User> pageResult = userService.findPage(page, size);
        return new Result<PageResult<User>>(true,StatusCode.OK,"查询成功",pageResult);
    }

    /***
     * 多条件搜索品牌数据
     * @param user
     * @return
     */
    @ApiOperation(value = "User条件查询",notes = "条件查询User方法详情",tags = {"UserController"})
    @PostMapping(value = "/search" )
    public Result<List<User>> findList(@RequestBody(required = false) @ApiParam(name = "User对象",value = "传入JSON数据",required = false) User user){
        //调用UserService实现条件查询User
        List<User> list = userService.findList(user);
        return new Result<List<User>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @ApiOperation(value = "User根据ID删除",notes = "根据ID删除User方法详情",tags = {"UserController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Long id){
        //调用UserService实现根据主键删除
        userService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改User数据
     * @param user
     * @param id
     * @return
     */
    @ApiOperation(value = "User根据ID修改",notes = "根据ID修改User方法详情",tags = {"UserController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @PutMapping(value="/{id}")
    public Result update(@RequestBody @ApiParam(name = "User对象",value = "传入JSON数据",required = false) User user,@PathVariable Long id){
        //设置主键值
        user.setId(id);
        //调用UserService实现修改User
        userService.update(user);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增User数据
     * @param user
     * @return
     */
    @ApiOperation(value = "User添加",notes = "添加User方法详情",tags = {"UserController"})
    @ApiImplicitParam(name = "smscode",value = "用户收到的验证码",required = true,type = "path")
    @PostMapping
    public Result add(@RequestBody  @ApiParam(name = "User对象",value = "传入JSON数据",required = true) User user,String smscode){
        boolean is = userService.checkSmsCode(user.getPhone(), smscode);
        if(is){
            //调用UserService实现添加User
            userService.add(user);
            return new Result(true,StatusCode.OK,"添加成功");
        }else{
            return new Result(false,StatusCode.ERROR,"验证码不正确！");
        }

    }

    /***
     * 根据ID查询User数据
     * @param id
     * @return
     */
    @ApiOperation(value = "User根据ID查询",notes = "根据ID查询User方法详情",tags = {"UserController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    // @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id){
        //调用UserService实现根据主键查询User
        User user = userService.findById(id);
        return new Result<User>(true,StatusCode.OK,"查询成功",user);
    }

    /***
     * 查询User全部数据
     * @return
     */
    @ApiOperation(value = "查询所有User",notes = "查询所User有方法详情",tags = {"UserController"})
    @GetMapping
    public Result<List<User>> findAll(HttpServletRequest request){
        //尝试从请求头获取网关转发令牌
        String s = request.getHeader("Authorization");
        System.out.println("获取到从网关转发令牌："+s);
        //调用UserService实现查询所有User
        List<User> list = userService.findAll();
        return new Result<List<User>>(true, StatusCode.OK,"查询成功",list) ;
    }

    @ApiOperation(value = "生成验证码",notes = "生成验证码",tags = {"UserController"})
    @ApiImplicitParam(name = "mobile",value = "手机号",type = "path",required = true)
    @GetMapping("/createSmsCode")
   //生成验证码
    public Result createSmsCode(String mobile){
        //验证手机号码
        boolean is = PhoneFormatCheckUtils.isChinaPhoneLegal(mobile);
        if(is){
        userService.createSmsCode(mobile);
        return new Result(true,StatusCode.OK,"短信验证码生成成功！");
        }else{
        return new Result(false,StatusCode.ERROR,"手机号码格式不正确！");
        }
    }

    //用户登录处理方法
    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse response){
        //判断用户名密码是否为空
        if(StringUtils.isEmpty(username)||StringUtils.isEmpty(password)){
            return new Result(false,StatusCode.ERROR,"用户名或密码为空，登陆失败！");
        }

        //根据登录用户名获取用户信息
        User user = userService.findByUserName(username);

        //判断user对象是否为空
        if(user != null){
            //比对用户输入密码和系统存储密码
            boolean checkpw = BCrypt.checkpw(password, user.getPassword());
            if(checkpw){
                //登录成功
                //创建map集合封装自定义数据
                HashMap<String, Object> map = new HashMap<>();
                map.put("role","USER");
                map.put("success","SUCCESS");
                map.put("username",username);
                //map转换json
                String json = JSON.toJSONString(map);
                //生成令牌
                String jwt = JwtUtil.createJWT(UUID.randomUUID().toString(), json, null);
                user.setPassword("");

                //创建cookie对象
                Cookie cookie = new Cookie("Authorization",jwt);
                //设置cookie路径到跟路径
                cookie.setPath("/");
                //把cookie设置到响应对象
                response.addCookie(cookie);

                return new Result(true,StatusCode.OK,"登录成功！",jwt);
            }else{
                //登录失败
                return new Result(false,StatusCode.LOGINERROR,"登录失败！");
            }
        }
        return new Result(false,StatusCode.LOGINERROR,"用户名或密码为空，登陆失败！");
    }

    //根据登录用户名获取信息
    @GetMapping("/findByUsername/{username}")
    public  Result<User> findByUsername(@PathVariable("username") String username){
        //调用服务方法，根据用户名获取登录信息
        User user = userService.findByUserName(username);
        if(user != null){
            return new Result<>(true,StatusCode.OK,"获取登录用户信息成功!",user);
        }else{
            return new Result<>(true,StatusCode.LOGINERROR,"获取登录用户信息失败!");
        }
    }

    /***
     * 增加用户积分
     * @param points:要添加的积分
     */
    @GetMapping(value = "/points/add")
   public Result addPoints(@RequestParam(value = "points")Integer points){
        //获取用户名
        Map<String, String> userMap = tokenDecode.getUserINfo();
        String username = userMap.get("user_name");

        //添加积分
        try {
            userService.addUserPoints(username,points);
            return new Result(true,StatusCode.OK,"添加积分成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,"添加积分失败！");
        }

    }
}
