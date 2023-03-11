package com.offcn.seckill.task;

import com.alibaba.fastjson.JSON;
import com.offcn.seckill.dao.SeckillGoodsMapper;
import com.offcn.seckill.entity.SeckillStatus;
import com.offcn.seckill.pojo.SeckillGoods;
import com.offcn.seckill.pojo.SeckillOrder;
import com.offcn.utils.IdWorker;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MultiThreadingCreateOrder {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    /**
     * 模拟多线程下单
     */
    @Async("myThread001")//本方法异步执行，启用一个独立线程池，独立运行
    public  void createOrder() {
        //使用setnx指令，和set指令有一个区别  setnx能够返回设置成功还是失败
        // Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", System.currentTimeMillis(), 500, TimeUnit.MILLISECONDS);
        // if(lock) {
            //如果返回true表示我们获取到锁

/*
        try {
            System.out.println("准备执行下单逻辑......");
            //系统等待20秒，模拟下单逻辑
            Thread.sleep(20000);
            System.out.println("下单执行完成.........");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } */

            //从队伍中获取排队信息
            SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderQueue").rightPop();

            if (seckillStatus != null) {

                Object o = redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId()).rightPop();
                if(o == null){
                    //从货架未获取到商品
                //    清理下单次数，排队信息
                    clearQueue(seckillStatus);
                //    结束下单
                    return;
                }
                //时间区间
                String time = seckillStatus.getTime();
                //用户登录名
                String username = seckillStatus.getUsername();
                //用户抢购商品
                Long id = seckillStatus.getGoodsId();


                //从redis读取
                SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(id);

                if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
                    throw new RuntimeException("秒杀商品已经售空");
                }

                SeckillOrder seckillOrder = new SeckillOrder();

                seckillOrder.setId(idWorker.nextId());

                //设置秒杀商品编号
                seckillOrder.setSeckillId(seckillGoods.getId());

                //设置金额
                seckillOrder.setMoney(seckillGoods.getCostPrice());

                //设置购买用户名
                seckillOrder.setUserId(username);

                //创建时间
                seckillOrder.setCreateTime(new Date());

                //秒杀订单状态
                seckillOrder.setStatus("0");

                //把秒杀订单暂时存储到redis缓存
                redisTemplate.boundHashOps("SeckillOrder").put(username, seckillOrder);

                //修改秒杀订单状态为2
                //抢单成功，更新抢单状态,排队->等待支付
                seckillStatus.setStatus(2);
                seckillStatus.setOrderId(seckillOrder.getId());
                seckillStatus.setMoney(seckillOrder.getMoney().floatValue());
                //更新保存秒杀订单状态信息
                redisTemplate.boundHashOps("UserQueueStatus").put(username, seckillStatus);
                //减少秒杀商品库存,原库存减一
                // seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);

                Long count = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillStatus.getGoodsId(), -1);
                seckillGoods.setStockCount(count.intValue());
                //判断秒杀商品库存是否等于0

                if (seckillGoods.getStockCount() == 0) {
                    //商品被抢光，redis缓存对象更新保存回数据库
                    seckillGoodsMapper.updateById(seckillGoods);
                    //从redis删除该商品
                    redisTemplate.boundHashOps("SeckillGoods_" + time).delete(seckillGoods.getId());
                } else {
                    //商品剩余库存依然存在，继续更新商品库存到redis
                    redisTemplate.boundHashOps("SeckillGoods_" + time).put(seckillGoods.getId(), seckillGoods);

                }
               sendTimeMessage(seckillStatus);

            }
        }
        //定义一个清理排队信息，下单次数
    private void clearQueue(SeckillStatus seckillStatus){
        //清理下单次数
        redisTemplate.boundHashOps("UserQueueCount").delete(seckillStatus.getUsername());
        //清理下单排队信息
        redisTemplate.boundHashOps("UserQueueStatus").delete(seckillStatus.getUsername());
    }

    //单独定义方法，发送延时消息
    private void sendTimeMessage(SeckillStatus seckillStatus){
        //把秒杀状态对象，转换成json
        String jsonStr = JSON.toJSONString(seckillStatus);
        rabbitTemplate.convertAndSend(env.getProperty("mq.pay.queue.seckillordertimerdelay"), (Object) jsonStr, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("10000");
                return message;
            }
        });
    }

        //释放锁
        // redisTemplate.delete("lock");

    }
// }
