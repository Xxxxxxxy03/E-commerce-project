package com.offcn.sellergoods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offcn.entity.PageResult;
import com.offcn.sellergoods.dao.SpecificationMapper;
import com.offcn.sellergoods.dao.SpecificationOptionMapper;
import com.offcn.sellergoods.group.SpecEntity;
import com.offcn.sellergoods.pojo.Specification;
import com.offcn.sellergoods.pojo.SpecificationOption;
import com.offcn.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/****
 * @Author:ujiuye
 * @Description:Specification业务层接口实现类
 * @Date 2021/2/1 14:19
 *****/
@Service
public class SpecificationServiceImpl extends ServiceImpl<SpecificationMapper,Specification> implements SpecificationService {

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    /**
     * Specification条件+分页查询
     * @param specification 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageResult<Specification> findPage(Specification specification, int page, int size){
         Page<Specification> mypage = new Page<>(page, size);
        QueryWrapper<Specification> queryWrapper = this.createQueryWrapper(specification);
        IPage<Specification> iPage = this.page(mypage, queryWrapper);
        return new PageResult<Specification>(iPage.getTotal(),iPage.getRecords());
    }

    /**
     * Specification分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<Specification> findPage(int page, int size){
        Page<Specification> mypage = new Page<>(page, size);
        IPage<Specification> iPage = this.page(mypage, new QueryWrapper<Specification>());

        return new PageResult<Specification>(iPage.getTotal(),iPage.getRecords());
    }

    /**
     * Specification条件查询
     * @param specification
     * @return
     */
    @Override
    public List<Specification> findList(Specification specification){
        //构建查询条件
        QueryWrapper<Specification> queryWrapper = this.createQueryWrapper(specification);
        //根据构建的条件查询数据
        return this.list(queryWrapper);
    }


    /**
     * Specification构建查询对象
     * @param specification
     * @return
     */
    public QueryWrapper<Specification> createQueryWrapper(Specification specification){
        QueryWrapper<Specification> queryWrapper = new QueryWrapper<>();
        if(specification!=null){
            // 主键
            if(!StringUtils.isEmpty(specification.getId())){
                 queryWrapper.eq("id",specification.getId());
            }
            // 名称
            if(!StringUtils.isEmpty(specification.getSpecName())){
                 queryWrapper.eq("spec_name",specification.getSpecName());
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
        //删除规格名称对象
        this.removeById(id);
        //关联删除规格选项
        QueryWrapper<SpecificationOption> qw = new QueryWrapper<>();
        qw.eq("spec_id",id);
        //执行删除
        specificationOptionMapper.delete(qw);
    }

    /**
     * 获取Specification下拉列表
     * @return
     */
    @Override
    public List<Map> selectOptions() {
        return this.baseMapper.selectOptions();
    }

    /**
     * 修改Specification
     * @param specEntity
     */
    @Override
    public void update(SpecEntity specEntity){
        this.updateById(specEntity.getSpecification());
        //首先把对应的规格选项数据全部删除
        QueryWrapper<SpecificationOption> qw = new QueryWrapper<>();
        qw.eq("spec_id",specEntity.getSpecification().getId());
        specificationOptionMapper.delete(qw);
        //再次保存规格选项
        for (SpecificationOption specificationOption : specEntity.getSpecificationOptionList()) {
            specificationOption.setSpecId(specEntity.getSpecification().getId());
            specificationOptionMapper.insert(specificationOption);
        }
    }

    /**
     * 增加Specification
     * @param specEntity
     */
    @Override
    public void add(SpecEntity specEntity){
        //保存规格数据
        this.save(specEntity.getSpecification());
        //获取规格选项的数据
        for (SpecificationOption specificationOption : specEntity.getSpecificationOptionList()) {
            //关联规格选项到规格
            specificationOption.setSpecId(specEntity.getSpecification().getId());
            //保存规格选项数据到数据库
            specificationOptionMapper.insert(specificationOption);
        }
    }

    /**
     * 根据ID查询SpecEntity
     * @param id
     * @return
     */
    @Override
    public SpecEntity findById(Long id){
        //创建一个要返回的组合实体对象
        SpecEntity specEntity = new SpecEntity();
        Specification specification = this.getById(id);
        //设置到组合对象
        specEntity.setSpecification(specification);
        //查规格选项
        QueryWrapper<SpecificationOption> qw = new QueryWrapper<>();
        qw.eq("spec_id",specification.getId());
        List<SpecificationOption> specificationOptions = specificationOptionMapper.selectList(qw);

        //设置到组合对象
        specEntity.setSpecificationOptionList(specificationOptions);
        return specEntity;
    }

    /**
     * 查询Specification全部数据
     * @return
     */
    @Override
    public List<Specification> findAll() {
        return this.list(new QueryWrapper<Specification>());
    }
}
