package com.offcn.sellergoods.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offcn.entity.PageResult;
import com.offcn.sellergoods.dao.SpecificationOptionMapper;
import com.offcn.sellergoods.dao.TypeTemplateMapper;
import com.offcn.sellergoods.pojo.SpecificationOption;
import com.offcn.sellergoods.pojo.TypeTemplate;
import com.offcn.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/****
 * @Author:ujiuye
 * @Description:TypeTemplate业务层接口实现类
 * @Date 2021/2/1 14:19
 *****/
@Service
public class TypeTemplateServiceImpl extends ServiceImpl<TypeTemplateMapper,TypeTemplate> implements TypeTemplateService {

    //注入规格选项数据接口
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    /**
     * TypeTemplate条件+分页查询
     * @param typeTemplate 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageResult<TypeTemplate> findPage(TypeTemplate typeTemplate, int page, int size){
         Page<TypeTemplate> mypage = new Page<>(page, size);
        QueryWrapper<TypeTemplate> queryWrapper = this.createQueryWrapper(typeTemplate);
        IPage<TypeTemplate> iPage = this.page(mypage, queryWrapper);
        return new PageResult<TypeTemplate>(iPage.getTotal(),iPage.getRecords());
    }

    /**
     * TypeTemplate分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<TypeTemplate> findPage(int page, int size){
        Page<TypeTemplate> mypage = new Page<>(page, size);
        IPage<TypeTemplate> iPage = this.page(mypage, new QueryWrapper<TypeTemplate>());

        return new PageResult<TypeTemplate>(iPage.getTotal(),iPage.getRecords());
    }

    /**
     * TypeTemplate条件查询
     * @param typeTemplate
     * @return
     */
    @Override
    public List<TypeTemplate> findList(TypeTemplate typeTemplate){
        //构建查询条件
        QueryWrapper<TypeTemplate> queryWrapper = this.createQueryWrapper(typeTemplate);
        //根据构建的条件查询数据
        return this.list(queryWrapper);
    }


    /**
     * TypeTemplate构建查询对象
     * @param typeTemplate
     * @return
     */
    public QueryWrapper<TypeTemplate> createQueryWrapper(TypeTemplate typeTemplate){
        QueryWrapper<TypeTemplate> queryWrapper = new QueryWrapper<>();
        if(typeTemplate!=null){
            // 
            if(!StringUtils.isEmpty(typeTemplate.getId())){
                 queryWrapper.eq("id",typeTemplate.getId());
            }
            // 模板名称
            if(!StringUtils.isEmpty(typeTemplate.getName())){
                queryWrapper.like("name",typeTemplate.getName());
            }
            // 关联规格
            if(!StringUtils.isEmpty(typeTemplate.getSpecIds())){
                 queryWrapper.eq("spec_ids",typeTemplate.getSpecIds());
            }
            // 关联品牌
            if(!StringUtils.isEmpty(typeTemplate.getBrandIds())){
                 queryWrapper.eq("brand_ids",typeTemplate.getBrandIds());
            }
            // 自定义属性
            if(!StringUtils.isEmpty(typeTemplate.getCustomAttributeItems())){
                 queryWrapper.eq("custom_attribute_items",typeTemplate.getCustomAttributeItems());
            }
        }
        return queryWrapper;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Long id){
        this.removeById(id);
    }

    /**
     * 修改TypeTemplate
     * @param typeTemplate
     */
    @Override
    public void update(TypeTemplate typeTemplate){
        this.updateById(typeTemplate);
    }

    /**
     * 增加TypeTemplate
     * @param typeTemplate
     */
    @Override
    public void add(TypeTemplate typeTemplate){
        this.save(typeTemplate);
    }

    /**
     * 根据ID查询TypeTemplate
     * @param id
     * @return
     */
    @Override
    public TypeTemplate findById(Long id){
        return  this.getById(id);
    }

    /**
     * 查询TypeTemplate全部数据
     * @return
     */
    @Override
    public List<TypeTemplate> findAll() {
        return this.list(new QueryWrapper<TypeTemplate>());
    }


    /***
     * 根据TypeTemplate id获取对应规格列表数据
     * @return
     */
    @Override
    public List<Map> findSpecList(Long typeId) {
      //根据模板id，获取模板信息数据
        TypeTemplate typeTemplate = this.findById(typeId);

        //把模板信息对象，包含的规格属性值获取到
        String specIdsJsonStr = typeTemplate.getSpecIds();
        //把规格json字符串转换成一个list集合
        List<Map> mapList = JSON.parseArray(specIdsJsonStr, Map.class);
        //判断规格数据集合是否为空
        if(!CollectionUtils.isEmpty(mapList)){
            //遍历集合
            for (Map map : mapList) {
                //map  id text
                //获取规格id
                //Map集合中取得数值类型的值默认是整型
                Long id =new Long((Integer)map.get("id"));
                //创建查询规格选项的查询条件封装对象
                QueryWrapper<SpecificationOption> qw = new QueryWrapper<>();
                //设置查询条件：指定规格编号
                qw.eq("spec_id",id);
                //发出查询
                List<SpecificationOption> specificationOptionList = specificationOptionMapper.selectList(qw);
                //把查询到的规格选项集合存放到map
                map.put("options",specificationOptionList);
            }
        }
        return mapList;
    }
}
