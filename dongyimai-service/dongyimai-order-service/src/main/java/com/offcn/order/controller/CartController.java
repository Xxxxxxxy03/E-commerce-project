package com.offcn.order.controller;

import com.offcn.entity.Result;
import com.offcn.entity.StatusCode;
import com.offcn.order.group.Cart;
import com.offcn.order.service.CartService;
import com.offcn.utils.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/cart")
public class CartController {

    //注入购物车服务
    @Autowired
    private CartService cartService;

    @Autowired
    private TokenDecode tokenDecode;


    /**
     * 购物车列表
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        Map<String, String> map = tokenDecode.getUserINfo();
        String username = map.get("user_name");
        //调用服务，从redis中提取购物车数据
        return cartService.findCartListFromRedis(username);
    }


    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request) {

        //从请求头获取令牌数据
      /*   String token = request.getHeader("Authorization");
        //截取掉Bearer
        if (token.startsWith("Bearer") || token.startsWith("bearer")) {
            token = token.substring(7);
        }
        System.out.println("从网关读取到令牌：" + token); */
        //调用解析令牌工具
        Map<String, String> map = tokenDecode.getUserINfo();

        //临时定义一个测试用户名
        String username = map.get("user_name");
        try {
            //获取现有购物车数据集合
            List<Cart> cartList = findCartList();
            //调用添加商品到购物车方法
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            //把最新购物车集合存储到redis
            cartService.saveCartListToRedis(username, cartList);

            //返回结果
            return new Result(true, StatusCode.OK, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, StatusCode.ERROR, "添加失败");
        }

    }
}