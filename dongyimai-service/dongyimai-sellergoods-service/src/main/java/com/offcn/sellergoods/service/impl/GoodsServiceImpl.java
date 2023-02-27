package com.offcn.sellergoods.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offcn.entity.PageResult;
import com.offcn.sellergoods.dao.*;
import com.offcn.sellergoods.group.GoodsEntity;
import com.offcn.sellergoods.pojo.*;
import com.offcn.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:ujiuye
 * @Description:Goods业务层接口实现类
 * @Date 2021/2/1 14:19
 *****/
@Service
@Transactional
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    //注入商品扩展信息数据操作接口
    @Resource
    private GoodsDescMapper goodsDescMapper;

    //注入品牌数据操作接口
    @Resource
    private BrandMapper brandMapper;

    //注入分类数据操作接口
    @Resource
    private ItemCatMapper itemCatMapper;

    @Resource
    private ItemMapper itemMapper;

    /**
     * Goods条件+分页查询
     *
     * @param goods 查询条件
     * @param page  页码
     * @param size  页大小
     * @return 分页结果
     */
    @Override
    public PageResult<Goods> findPage(Goods goods, int page, int size) {
        Page<Goods> mypage = new Page<>(page, size);
        QueryWrapper<Goods> queryWrapper = this.createQueryWrapper(goods);
        IPage<Goods> iPage = this.page(mypage, queryWrapper);
        return new PageResult<Goods>(iPage.getTotal(), iPage.getRecords());
    }

    /**
     * Goods分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<Goods> findPage(int page, int size) {
        Page<Goods> mypage = new Page<>(page, size);
        IPage<Goods> iPage = this.page(mypage, new QueryWrapper<Goods>());

        return new PageResult<Goods>(iPage.getTotal(), iPage.getRecords());
    }

    /**
     * Goods条件查询
     *
     * @param goods
     * @return
     */
    @Override
    public List<Goods> findList(Goods goods) {
        //构建查询条件
        QueryWrapper<Goods> queryWrapper = this.createQueryWrapper(goods);
        //根据构建的条件查询数据
        return this.list(queryWrapper);
    }


    /**
     * Goods构建查询对象
     *
     * @param goods
     * @return
     */
    public QueryWrapper<Goods> createQueryWrapper(Goods goods) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        if (goods != null) {
            // 主键
            if (!StringUtils.isEmpty(goods.getId())) {
                queryWrapper.eq("id", goods.getId());
            }
            // 商家ID
            if (!StringUtils.isEmpty(goods.getSellerId())) {
                queryWrapper.eq("seller_id", goods.getSellerId());
            }
            // SPU名
            if (!StringUtils.isEmpty(goods.getGoodsName())) {
                queryWrapper.eq("goods_name", goods.getGoodsName());
            }
            // 默认SKU
            if (!StringUtils.isEmpty(goods.getDefaultItemId())) {
                queryWrapper.eq("default_item_id", goods.getDefaultItemId());
            }
            // 状态
            if (!StringUtils.isEmpty(goods.getAuditStatus())) {
                queryWrapper.eq("audit_status", goods.getAuditStatus());
            }
            // 是否上架
            if (!StringUtils.isEmpty(goods.getIsMarketable())) {
                queryWrapper.eq("is_marketable", goods.getIsMarketable());
            }
            // 品牌
            if (!StringUtils.isEmpty(goods.getBrandId())) {
                queryWrapper.eq("brand_id", goods.getBrandId());
            }
            // 副标题
            if (!StringUtils.isEmpty(goods.getCaption())) {
                queryWrapper.eq("caption", goods.getCaption());
            }
            // 一级类目
            if (!StringUtils.isEmpty(goods.getCategory1Id())) {
                queryWrapper.eq("category1_id", goods.getCategory1Id());
            }
            // 二级类目
            if (!StringUtils.isEmpty(goods.getCategory2Id())) {
                queryWrapper.eq("category2_id", goods.getCategory2Id());
            }
            // 三级类目
            if (!StringUtils.isEmpty(goods.getCategory3Id())) {
                queryWrapper.eq("category3_id", goods.getCategory3Id());
            }
            // 小图
            if (!StringUtils.isEmpty(goods.getSmallPic())) {
                queryWrapper.eq("small_pic", goods.getSmallPic());
            }
            // 商城价
            if (!StringUtils.isEmpty(goods.getPrice())) {
                queryWrapper.eq("price", goods.getPrice());
            }
            // 分类模板ID
            if (!StringUtils.isEmpty(goods.getTypeTemplateId())) {
                queryWrapper.eq("type_template_id", goods.getTypeTemplateId());
            }
            // 是否启用规格
            if (!StringUtils.isEmpty(goods.getIsEnableSpec())) {
                queryWrapper.eq("is_enable_spec", goods.getIsEnableSpec());
            }
            // 是否删除
            if (!StringUtils.isEmpty(goods.getIsDelete())) {
                queryWrapper.eq("is_delete", goods.getIsDelete());
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
        //根据id获取商品基本信息
        Goods goods = this.getById(id);
        //判断商品是否处于上架状态
        if("1".equals(goods.getIsMarketable())){
           throw new RuntimeException("商品为上架状态，不能删除");
        }
        goods.setIsDelete("1");
        goods.setAuditStatus("0");
        this.updateById(goods);
    }

    /**
     * 修改Goods
     *
     * @param goodsEntity
     */
    @Override
    public void update(GoodsEntity goodsEntity) {
        //重新设置审核状态为待审核
       goodsEntity.getGoods().setAuditStatus("0");
       //修改商品基本信息
        this.updateById(goodsEntity.getGoods());
        //修改商品扩展信息
        goodsDescMapper.updateById(goodsEntity.getGoodsDesc());
        //修改sku数据集合
        //创建sku的删除条件封装对象
        QueryWrapper<Item> qw = new QueryWrapper<>();
        qw.eq("goods_id",goodsEntity.getGoods().getId());
        itemMapper.delete(qw);
        //保存sku
       this.saveItemList(goodsEntity);
    }

    /**
     * 增加Goods
     *
     * @param goodsEntity
     */
    @Override
    public void add(GoodsEntity goodsEntity) {
        //首先设置商品状态为0  待审核
        goodsEntity.getGoods().setAuditStatus("0");
        //保存商品基本信息
        this.save(goodsEntity.getGoods());
        //关联扩展信息到商品编号
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        //保存商品的扩展信息
        goodsDescMapper.insert(goodsEntity.getGoodsDesc());

        //模拟异常
//        int i = 1 / 0;

        /*try {
            int i = 1 / 0;
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        this.saveItemList(goodsEntity);
    }

    private void saveItemList(GoodsEntity goodsEntity) {
        //判断是否启用了规格
        if("1".equals(goodsEntity.getGoods().getIsEnableSpec())){
            //启用规格
            //判断sku数据集合是否为空
            if(!CollectionUtils.isEmpty(goodsEntity.getItemList())){
                //遍历sku数据集合
                for (Item item : goodsEntity.getItemList()) {
                 //获取商品名称
                    String title = goodsEntity.getGoods().getGoodsName();
                   //获取规格属性值
                    String specJsonStr = item.getSpec();
                    //解析成对象
                    Map<String,String> map = JSON.parseObject(specJsonStr, Map.class);
                    //遍历map
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        title += " "+entry.getValue();
                    }
                    //把标题设置到sku标题属性
                    item.setTitle(title);
                    this.setItemValue(goodsEntity, item);
                    itemMapper.insert(item);
                }
            }
        }else{
            //不启用规格  SKU信息为默认值
            Item item = new Item();
            item.setTitle(goodsEntity.getGoods().getGoodsName());     //商品名称
            item.setPrice(goodsEntity.getGoods().getPrice());      //默认使用SPU的价格
            item.setNum(9999);
            item.setStatus("1");            //是否启用
            item.setIsDefault("1");         //是否默认
            item.setSpec("{}");             //没有选择规格，则放置空JSON结构
            this.setItemValue(goodsEntity, item);//ctrl+alt+m
            itemMapper.insert(item);
        }
    }

    private void setItemValue(GoodsEntity goodsEntity, Item item) {
        //把商品基本信息包含的三级分类设置到sku类目
        item.setCategoryId(goodsEntity.getGoods().getCategory3Id());
        //设置创建时间
        item.setCreateTime(new Date());
        //设置更新时间
        item.setUpdateTime(new Date());
        //设置关联商品信息表goods_id
        item.setGoodsId(goodsEntity.getGoods().getId());
        //设置卖家编号
        item.setSellerId(goodsEntity.getGoods().getSellerId());

        //设置品牌名称
        //从商品基本信息获取对应品牌的编号
        Long brandId = goodsEntity.getGoods().getBrandId();
        //根据品牌编号获取对应品牌名称
        Brand brand = brandMapper.selectById(brandId);
        //判断品牌是否为空
        if(brand != null){
            //获取品牌名称，设置到sku属性
            item.setBrand(brand.getName());
        }
        //设置分类名称
        Long category3Id = goodsEntity.getGoods().getCategory3Id();
        //查询分类对象
        ItemCat itemCat = itemCatMapper.selectById(category3Id);
        if(itemCat != null){
            //设置分类名称到sku对象
            item.setCategory(itemCat.getName());
        }
        //设置商品配图
        //从商品扩展信息对象，读取配图
        String itemImages = goodsEntity.getGoodsDesc().getItemImages();
        //把配图json字符串解析成java对象
        List<Map> mapList = JSON.parseArray(itemImages, Map.class);
        if(!CollectionUtils.isEmpty(mapList)){
            //提取第一张配图，设置到sku配图属性
            item.setImage((String) (mapList.get(0).get("url")));
        }
    }

    /**
     * 根据ID查询Goods
     *
     * @param id
     * @return
     */
    @Override
    public GoodsEntity findById(Long id) {
        //创建商品组合对象
        GoodsEntity goodsEntity = new GoodsEntity();
        //查询商品基本信息
        Goods goods = this.getById(id);
        goodsEntity.setGoods(goods);
        //查询商品扩展信息
        GoodsDesc goodsDesc = goodsDescMapper.selectById(id);
        goodsEntity.setGoodsDesc(goodsDesc);

        //查询sku数据集合
        //创建sku查询条件封装对象
        QueryWrapper<Item> qw = new QueryWrapper<>();
        qw.eq("goods_id",id);
        //查询sku数据
        List<Item> itemList = itemMapper.selectList(qw);
        goodsEntity.setItemList(itemList);
        return goodsEntity;
    }

    /**
     * 查询Goods全部数据
     *
     * @return
     */
    @Override
    public List<Goods> findAll() {
        return this.list(new QueryWrapper<Goods>());
    }

    /***
     * 商品审核
     * @return
     */
    @Override
    public void audit(Long id) {
        //根据商品编号获取基本信息
        Goods goods = getById(id);
        //修改商品审核状态
        goods.setAuditStatus("1");
        //可以同时设置商品上下架状态为上架 1
        goods.setIsMarketable("1");
        //更新保存回数据库
        this.updateById(goods);
    }

    /***
     * 商品下架
     * @return
     */
    @Override
    public void pull(Long id) {
        //根据商品id获取基本信息
        Goods goods = getById(id);
        //设置商品上下架状态为下架 0
        goods.setIsMarketable("0");
        //更新保存回数据库
        this.updateById(goods);

    }

    /***
     * 商品上架
     * @return
     */
    @Override
    public void put(Long id) {
        //根据商品编号获取基本信息
        Goods goods = getById(id);
        //首先判断商品审核状态是否为1
        if(!"1".equals(goods.getAuditStatus())){
            throw new RuntimeException("商品审核未通过！");
        }
        //可以同时设置商品上下架状态为上架 1
        goods.setIsMarketable("1");
        //更新保存回数据库
        this.updateById(goods);
    }

    /***
     * 批量上架
     * @return
     */
    @Override
    public void putMany(Long[] ids) {
        //把要修改数据设置到实体类
        Goods goods = new Goods();
        goods.setIsMarketable("1");
        //创建查询条件
        QueryWrapper<Goods> qw = new QueryWrapper<>();
        //指定id范围
        qw.in("id", Arrays.asList(ids));
        //设置修改条件2：商品状态为下架
        qw.eq("is_marketable","0");
        //商品状态必须为1
        qw.eq("audit_status","1");
        this.baseMapper.update(goods,qw);
    }
    /***
     * 批量下架
     * @return
     */
    @Override
    public void pullMany(Long[] ids) {
        //把要修改数据设置到实体类
        Goods goods = new Goods();
        goods.setIsMarketable("0");
        //创建查询条件
        QueryWrapper<Goods> qw = new QueryWrapper<>();
        //指定id范围
        qw.in("id", Arrays.asList(ids));
        //设置修改条件2：商品状态为上架
        qw.eq("is_marketable","1");
        //商品状态必须为1
        qw.eq("audit_status","1");
        this.baseMapper.update(goods,qw);
    }
}
