package com.offcn.seckill.task;

import com.offcn.seckill.dao.SeckillGoodsMapper;
import com.offcn.seckill.pojo.SeckillGoods;
import com.offcn.seckill.pojo.SeckillOrder;
import com.offcn.seckill.pojo.SeckillStatus;
import com.offcn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 多线程下单
     */
    @Async
    public void createOrder() {
        //从队伍中获取排队信息
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderQueue").rightPop();

        if (seckillStatus != null) {
            //时间区间
            String time = seckillStatus.getTime();
            //用户登录名
            String username = seckillStatus.getUsername();
            //用户抢购商品
            Long id = seckillStatus.getGoodsId();


            try {
                System.out.println("准备执行......");
                Thread.sleep(2000);
                System.out.println("开始执行.......");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
            SeckillOrder seckillOrder1 = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
            System.out.println(seckillOrder1.getStatus() + "," + seckillOrder1.getCreateTime());

            //减少秒杀商品库存,原库存减一
            seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);

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

            //抢单成功，更新抢单状态,排队->等待支付
            seckillStatus.setStatus(2);
            seckillStatus.setOrderId(seckillOrder.getId());
            seckillStatus.setMoney(seckillOrder.getMoney().floatValue());
            redisTemplate.boundHashOps("UserQueueStatus").put(username, seckillStatus);
        }

    }
}
