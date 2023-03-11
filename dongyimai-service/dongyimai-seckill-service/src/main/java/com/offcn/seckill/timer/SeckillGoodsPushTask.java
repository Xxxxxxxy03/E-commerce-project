package com.offcn.seckill.timer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.offcn.seckill.dao.SeckillGoodsMapper;
import com.offcn.seckill.pojo.SeckillGoods;
import com.offcn.seckill.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillGoodsPushTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    //    每30秒执行
//    * 表示任意值 在秒位  0-59秒
//    , 表示多个值   分割
//    - 表示一个范围
//     / 是一个递增    0/5   从0秒开始每次递增5秒
//    ? 只会出现在日期和周的位置 表示占位符
    @Scheduled(cron = " 0/30 * * * * ?")
    public void loadGoodsPushRedis() {
        //测试把数据库商品写入redis缓存
        //创建一个日期时间格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("定时任务执行中：" + dateFormat.format(new Date()));

        //获得时间段集合
        List<Date> dateMenus = DateUtil.getDateMenus();
        //遍历时间段集合
        for (Date startTime : dateMenus) {

            //提取开始时间，转换为年月日格式字符串
            String extName = DateUtil.date2Str(startTime);
            //    创建查询对象
            QueryWrapper<SeckillGoods> qw = new QueryWrapper<>();
            //设置查询条件  商品必须审核通过 status = 1
            qw.eq("status", "1");
            //库存大于0
            qw.gt("stock_count", 0);
            //开始时间<=活动开始时间
            qw.ge("start_time", DateUtil.date2StrFull(startTime));
            //活动结束时间小于开始时间+2小时
            System.out.println(DateUtil.addDateHour(startTime, 2));
            qw.lt("end_time", DateUtil.date2StrFull(DateUtil.addDateHour(startTime, 2)));

            //从redis读取当天的秒杀商品
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + extName).keys();

            //判断keys不为空，设置排除条件
            if (keys != null && keys.size() > 0) {
                qw.notIn("id", keys);
            }
            //查询数据库
            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectList(qw);

            //遍历集合存储到redis
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                redisTemplate.boundHashOps("SeckillGoods_" + extName).put(seckillGoods.getId(), seckillGoods);

                //为每个商品准备一个货架  redis的list结构
                Long[] ids = pushIds(seckillGoods.getStockCount(), seckillGoods.getId());
                redisTemplate.boundListOps("SeckillGoodsCountList_"+seckillGoods.getId()).leftPushAll(ids);

                //同时记录每个秒杀商品剩余库存  使用put  写入对象value就会当成字符串处理，希望存储的是数值
                redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillGoods.getId(),seckillGoods.getStockCount());

            }
        }
    }

    private Long[] pushIds(int len,Long id){
        Long[] longs = new Long[len];
        //循环遍历数组
        for (int i = 0; i < longs.length; i++) {
            //设置每个数组元素值为商品编号
            longs[i] = id;
        }
        return longs;
    }
}
