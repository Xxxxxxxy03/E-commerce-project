package com.offcn.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offcn.entity.PageResult;
import com.offcn.order.dao.OrderItemMapper;
import com.offcn.order.dao.OrderMapper;
import com.offcn.order.dao.PayLogMapper;
import com.offcn.order.group.Cart;
import com.offcn.order.pojo.Order;
import com.offcn.order.pojo.OrderItem;
import com.offcn.order.pojo.PayLog;
import com.offcn.order.service.OrderService;
import com.offcn.sellergoods.feign.ItemFeign;
import com.offcn.user.feign.UserFeign;
import com.offcn.utils.IdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/****
 * @Author:ujiuye
 * @Description:Order业务层接口实现类
 * @Date 2021/2/1 14:19
 *****/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    //注入redis操作类
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ItemFeign itemFeign;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * Order条件+分页查询
     *
     * @param order 查询条件
     * @param page  页码
     * @param size  页大小
     * @return 分页结果
     */
    @Override
    public PageResult<Order> findPage(Order order, int page, int size) {
        Page<Order> mypage = new Page<>(page, size);
        QueryWrapper<Order> queryWrapper = this.createQueryWrapper(order);
        IPage<Order> iPage = this.page(mypage, queryWrapper);
        return new PageResult<Order>(iPage.getTotal(), iPage.getRecords());
    }

    /**
     * Order分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<Order> findPage(int page, int size) {
        Page<Order> mypage = new Page<>(page, size);
        IPage<Order> iPage = this.page(mypage, new QueryWrapper<Order>());

        return new PageResult<Order>(iPage.getTotal(), iPage.getRecords());
    }

    /**
     * Order条件查询
     *
     * @param order
     * @return
     */
    @Override
    public List<Order> findList(Order order) {
        //构建查询条件
        QueryWrapper<Order> queryWrapper = this.createQueryWrapper(order);
        //根据构建的条件查询数据
        return this.list(queryWrapper);
    }


    /**
     * Order构建查询对象
     *
     * @param order
     * @return
     */
    public QueryWrapper<Order> createQueryWrapper(Order order) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        if (order != null) {
            // 订单id
            if (!StringUtils.isEmpty(order.getOrderId())) {
                queryWrapper.eq("order_id", order.getOrderId());
            }
            // 实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分
            if (!StringUtils.isEmpty(order.getPayment())) {
                queryWrapper.eq("payment", order.getPayment());
            }
            // 支付类型，1、在线支付，2、货到付款
            if (!StringUtils.isEmpty(order.getPaymentType())) {
                queryWrapper.eq("payment_type", order.getPaymentType());
            }
            // 邮费。精确到2位小数;单位:元。如:200.07，表示:200元7分
            if (!StringUtils.isEmpty(order.getPostFee())) {
                queryWrapper.eq("post_fee", order.getPostFee());
            }
            // 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
            if (!StringUtils.isEmpty(order.getStatus())) {
                queryWrapper.eq("status", order.getStatus());
            }
            // 订单创建时间
            if (!StringUtils.isEmpty(order.getCreateTime())) {
                queryWrapper.eq("create_time", order.getCreateTime());
            }
            // 订单更新时间
            if (!StringUtils.isEmpty(order.getUpdateTime())) {
                queryWrapper.eq("update_time", order.getUpdateTime());
            }
            // 付款时间
            if (!StringUtils.isEmpty(order.getPaymentTime())) {
                queryWrapper.eq("payment_time", order.getPaymentTime());
            }
            // 发货时间
            if (!StringUtils.isEmpty(order.getConsignTime())) {
                queryWrapper.eq("consign_time", order.getConsignTime());
            }
            // 交易完成时间
            if (!StringUtils.isEmpty(order.getEndTime())) {
                queryWrapper.eq("end_time", order.getEndTime());
            }
            // 交易关闭时间
            if (!StringUtils.isEmpty(order.getCloseTime())) {
                queryWrapper.eq("close_time", order.getCloseTime());
            }
            // 物流名称
            if (!StringUtils.isEmpty(order.getShippingName())) {
                queryWrapper.eq("shipping_name", order.getShippingName());
            }
            // 物流单号
            if (!StringUtils.isEmpty(order.getShippingCode())) {
                queryWrapper.eq("shipping_code", order.getShippingCode());
            }
            // 用户id
            if (!StringUtils.isEmpty(order.getUserId())) {
                queryWrapper.eq("user_id", order.getUserId());
            }
            // 买家留言
            if (!StringUtils.isEmpty(order.getBuyerMessage())) {
                queryWrapper.eq("buyer_message", order.getBuyerMessage());
            }
            // 买家昵称
            if (!StringUtils.isEmpty(order.getBuyerNick())) {
                queryWrapper.eq("buyer_nick", order.getBuyerNick());
            }
            // 买家是否已经评价
            if (!StringUtils.isEmpty(order.getBuyerRate())) {
                queryWrapper.eq("buyer_rate", order.getBuyerRate());
            }
            // 收货人地区名称(省，市，县)街道
            if (!StringUtils.isEmpty(order.getReceiverAreaName())) {
                queryWrapper.eq("receiver_area_name", order.getReceiverAreaName());
            }
            // 收货人手机
            if (!StringUtils.isEmpty(order.getReceiverMobile())) {
                queryWrapper.eq("receiver_mobile", order.getReceiverMobile());
            }
            // 收货人邮编
            if (!StringUtils.isEmpty(order.getReceiverZipCode())) {
                queryWrapper.eq("receiver_zip_code", order.getReceiverZipCode());
            }
            // 收货人
            if (!StringUtils.isEmpty(order.getReceiver())) {
                queryWrapper.eq("receiver", order.getReceiver());
            }
            // 过期时间，定期清理
            if (!StringUtils.isEmpty(order.getExpire())) {
                queryWrapper.eq("expire", order.getExpire());
            }
            // 发票类型(普通发票，电子发票，增值税发票)
            if (!StringUtils.isEmpty(order.getInvoiceType())) {
                queryWrapper.eq("invoice_type", order.getInvoiceType());
            }
            // 订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
            if (!StringUtils.isEmpty(order.getSourceType())) {
                queryWrapper.eq("source_type", order.getSourceType());
            }
            // 商家ID
            if (!StringUtils.isEmpty(order.getSellerId())) {
                queryWrapper.eq("seller_id", order.getSellerId());
            }
        }
        return queryWrapper;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        this.removeById(id);
    }

    /**
     * 修改Order
     *
     * @param order
     */
    @Override
    public void update(Order order) {
        this.updateById(order);
    }

    /**
     * 增加Order
     *
     * @param order
     */
    @Override
    public void add(Order order) {
        //   从redis读取当前登录用户对应的购物车数据
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        //定义一个总的金额
        BigDecimal total_money = new BigDecimal(0.00);

        //创建集合存储，全部订单号
        ArrayList list = new ArrayList<>();
        //    判断从redis获取购物车数据是否为空
        if (cartList != null) {
            //    遍历购物车数据
            for (Cart cart : cartList) {
                //    创建订单对象
                Order tbOrder = new Order();

                //    使用id生成器生成订单编号
                long orderId = idWorker.nextId();

                list.add(orderId + "");
                //把order对象存储的一些数据设置到tbOrder
               /*  tbOrder.setReceiver(order.getReceiver());
                tbOrder.setReceiverMobile(order.getReceiverMobile());
                tbOrder.setReceiverAreaName(order.getReceiverAreaName()); */
                //    采用对象苏醒复制工具进行属性批量复制
                //    复制的原则、属性名必须相同，属性类型必须一致
                BeanUtils.copyProperties(order, tbOrder);
                tbOrder.setOrderId(orderId);

                //    继续设置其他订单属性
                //    设置状态
                tbOrder.setStatus("1");
                //    订单创建日期
                tbOrder.setCreateTime(new Date());
                tbOrder.setUpdateTime(new Date());

                //    设置商家编号,从购物车获取
                tbOrder.setSellerId(cart.getSellerId());

                //    定义一个合计金额
                BigDecimal money = new BigDecimal(0.00);

                //    获取当前购物车的购物明细集合
                for (OrderItem orderItem : cart.getOrderItemList()) {
                    //    获取每条购物明细合计金额
                    money = money.add(orderItem.getTotalFee());

                    //    生成订单详情编号
                    long orderItemId = idWorker.nextId();
                    orderItem.setId(orderItemId);
                    //    设置关联订单号
                    orderItem.setOrderId(orderId);
                    //    设置商家编号
                    orderItem.setSellerId(cart.getSellerId());

                    orderItemMapper.insert(orderItem);
                    //    设置实付金额到订单
                    tbOrder.setPayment(money);


                }
                    //累加各个订单金额到总金额
                    total_money = total_money.add(money);
                    //保存订单到数据库
                    this.save(tbOrder);
                //    下单成功，保存支付日志
                //支持货到付款，判断是否属于线上支付，记录支付日志
                if ("1".equals(order.getPaymentType())) {
                    //线上支付，才需要记录支付日志
                    PayLog payLog = new PayLog();
                    //使用id生成器生成一个支付的订单号
                    long out_trade_no = idWorker.nextId();

                    payLog.setOutTradeNo(out_trade_no + "");
                    //创建时间
                    payLog.setCreateTime(new Date());
                    //    支付金额
                    BigDecimal total_money_fen = total_money.multiply(new BigDecimal(100));
                    System.out.println(total_money_fen.toBigInteger().longValue());
                    payLog.setTotalFee(total_money_fen.toBigInteger().longValue());
                    //    设置关联的用户id
                    payLog.setUserId(order.getUserId());
                    //    设置支付状态
                    payLog.setTradeState("0");
                    //支付类型
                    payLog.setPayType("1");
                    //    关联当前全部订单号
                    //    把订单号集合转换字符串 去除前后[]

                    String orderIdStr = list.toString().replace("[", "").replace("]", "").replace(" ", "");
                    payLog.setOrderList(orderIdStr);

                    //    保存支付日志到数据库
                    int i = payLogMapper.insert(payLog);
                    System.out.println(i);

                    redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
                } else {
                    System.out.println("支付日志记录失败，不属于线上支付");
                }

                //    调用feign接口更新库存
                itemFeign.decrCount(order.getUserId());

                //    增加积分，每次下单增加10分
                userFeign.addPoints(10);

            }
            //    清空购物车数据
            redisTemplate.boundHashOps("cartList").delete(order.getUserId());
        }

    }

    /**
     * 根据ID查询Order
     *
     * @param id
     * @return
     */
    @Override
    public Order findById(Long id) {
        return this.getById(id);
    }

    /**
     * 查询Order全部数据
     *
     * @return
     */
    @Override
    public List<Order> findAll() {
        return this.list(new QueryWrapper<Order>());
    }


    /**
     * 修改订单状态
     *
     * @param out_trade_no 支付订单号
     * @param trade_no     支付宝返回的交易流水号
     */
    @Override
    public void updateStatus(String out_trade_no, String trade_no) {
        //修改支付日志状态
        //根据支付订单号去数据库读取对应支付日志
        PayLog payLog = payLogMapper.selectById(out_trade_no);
        //设置支付完成时间
        payLog.setPayTime(new Date());
        //设置交易状态 1 支付完成
        payLog.setTradeState("1");
        //交易号
        payLog.setTransactionId(trade_no);
        payLogMapper.updateById(payLog);
        //    修改订单状态
        //    获取订单号列表
        String orderList = payLog.getOrderList();
        //获取订单号数组
        String[] orderIds = orderList.split(",");
        if (orderIds != null) {
            for (String orderId : orderIds) {
                Order order = orderMapper.selectById(Long.parseLong(orderId));
                if (order != null) {
                    //修改订单状态 2 已付款
                    order.setStatus("2");
                    //更新保存回数据库
                    orderMapper.updateById(order);
                }
            }
        }
        //    清除redis缓存数据
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());

    }
}
