package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    DishMapper dishMapper;

    @Autowired
    SetmealMapper setmealMapper;


    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {

        //使用简化分页开发(本地线程空间会将其带到mapper层进行动态sql拼接)
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());

        //调用mapper层获取简化分页对象
        Page<Category> page=categoryMapper.pageQuery(categoryPageQueryDTO);

        //从这个分页插件page类去获取PageResult属性值:页面数量
        Long total=page.getTotal();
        List<Category> records=page.getResult();

        //将数据封装为PageResult,并返回给controller层
        return new PageResult(total,records);
    }

    /**
     * 启用、禁用分类
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .build();
        categoryMapper.updata(category);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void updata(CategoryDTO categoryDTO) {
        Category category=new Category();
        //将实体类进行拷贝转换
        BeanUtils.copyProperties(categoryDTO,category);
        //更新修改时间
        category.setUpdateTime(LocalDateTime.now());
        //设置操作用户
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.updata(category);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void delete(Long id) {

        //查询当前分类是否关联了菜品，如果关联了就抛出业务异常
        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = setmealMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        categoryMapper.delete(id);
    }

    /**
     * 新增分类
     * @param categoryDTO
     */
    @Override
    public void add(CategoryDTO categoryDTO) {

        //将分类拷贝转换
        Category category=new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        //对分类实体类对象进行属性补充
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());

        //默认一开始是禁用状态
        category.setStatus(0);

        categoryMapper.add(category);
    }

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    @Override
    public List<Category> getByType(Integer type) {
        List<Category> categoryList=categoryMapper.getByType(type);
        return categoryList;
    }
}
