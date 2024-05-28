package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;

    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Autowired
    DishFlavorMapper dishFlavorMapper;
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult Page(DishPageQueryDTO dishPageQueryDTO) {
        //使用分页简化工具开始分页
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        //调用mapper层返回分页的员工数据,并且使用分页插件对象Page进行接收,泛型就写接收的实体类
        Page<DishVO> page=dishMapper.pageQuery(dishPageQueryDTO);

        //从这个分页插件page类去获取PageResult属性值:页面数量
        Long total=page.getTotal();
        List<DishVO> records=page.getResult();

        //将数据封装为PageResult,并返回给controller层
        return new PageResult(total,records);
    }

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    public void addWithFlavor(DishDTO dishDTO) {
        //1,对于菜品表进行添加
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        log.info("往数据库中添加菜品数据:{}",dish);
        //记得添加注解让其AOP进行代理去添加日期
        dishMapper.add(dish);

        //因为我们需要获得我们添加菜品后菜品的id,因为这个id是将数据保存到数据库后自动生成的,所以我们需要在
        //菜品存入数据库中后再去获取,而要实现这一功能,需要在对于sql的xml标签上加上属性<insert id="add" useGeneratedKeys="true" keyProperty="id">
        //菜品数据储存后主键回显
        Long id = dish.getId();
        log.info("菜品主键回显:{}",id);

        //2,对菜品口味表进行添加
        //获取口味数据
        List<DishFlavor> dishFlavorList=dishDTO.getFlavors();
        //遍历集合为其dishId属性赋值
        for (DishFlavor dishFlavor : dishFlavorList) {
            dishFlavor.setDishId(id);
        }
        dishFlavorMapper.add(dishFlavorList);
    }

    /**
     * 批量删除菜品
     * @param idList
     */
    @Override
    public void deleteByIds(List<Long> idList) {

        List<Dish> dishs=dishMapper.getByIds(idList);
        //删除前进行判断菜品状态是否为起售中
        for (Dish dish : dishs) {
            if (dish.getStatus()== StatusConstant.ENABLE){
                throw new BaseException(MessageConstant.DISH_ON_SALE);
            }
        }
        //删除前进行判断是否被套餐关联
        List<SetmealDish> setmealDishes=setmealDishMapper.getByDishIds(idList);
        if (setmealDishes.size()>0){
         throw new BaseException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品
        dishMapper.deleteByIds(idList);
        //删除菜品相关的口味
        dishFlavorMapper.deletByDishIds(idList);
    }
}
