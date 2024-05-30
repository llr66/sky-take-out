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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult Page(DishPageQueryDTO dishPageQueryDTO) {
        //使用分页简化工具开始分页
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        //调用mapper层返回分页的员工数据,并且使用分页插件对象Page进行接收,泛型就写接收的实体类
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        //从这个分页插件page类去获取PageResult属性值:页面数量
        Long total = page.getTotal();
        List<DishVO> records = page.getResult();

        //将数据封装为PageResult,并返回给controller层
        return new PageResult(total, records);
    }

    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addWithFlavor(DishDTO dishDTO) {
        //1,对于菜品表进行添加
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        log.info("往数据库中添加菜品数据:{}", dish);
        //记得添加注解让其AOP进行代理去添加日期
        dishMapper.add(dish);

        //因为我们需要获得我们添加菜品后菜品的id,因为这个id是将数据保存到数据库后自动生成的,所以我们需要在
        //菜品存入数据库中后再去获取,而要实现这一功能,需要在对于sql的xml标签上加上属性<insert id="add" useGeneratedKeys="true" keyProperty="id">
        //菜品数据储存后主键回显
        Long id = dish.getId();
        log.info("菜品主键回显:{}", id);

        //2,对菜品口味表进行添加
        //获取口味数据
        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        //遍历集合为其dishId属性赋值
        for (DishFlavor dishFlavor : dishFlavorList) {
            dishFlavor.setDishId(id);
        }
        dishFlavorMapper.add(dishFlavorList);
    }

    /**
     * 批量删除菜品
     *
     * @param idList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Long> idList) {

        List<Dish> dishs = dishMapper.getByIds(idList);
        //删除前进行判断菜品状态是否为起售中
        for (Dish dish : dishs) {
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new BaseException(MessageConstant.DISH_ON_SALE);
            }
        }
        //删除前进行判断是否被套餐关联
        List<SetmealDish> setmealDishes = setmealDishMapper.getByDishIds(idList);
        if (setmealDishes.size() > 0) {
            throw new BaseException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品
        dishMapper.deleteByIds(idList);
        //删除菜品相关的口味
        dishFlavorMapper.deletByDishIds(idList);
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */

    @Override
    public DishVO getById(Long id) {
        //查询菜品表的菜品数据
        Dish dish = dishMapper.getById(id);
        //查询菜品口味表中,菜品关联的口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        //利用工具类进行数据拷贝
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;

    }


    /**
     * 修改菜品
     *
     * @param dishDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updata(DishDTO dishDTO) {
        Dish dish = new Dish();
        //拷贝传入数据,记得加上在Mapper方法标签将更改时间的操作交给AOP处理
        BeanUtils.copyProperties(dishDTO, dish);
        //对Dish表行数据进行修改
        dishMapper.upData(dish);
        //对口味Dish_flavor表中的数据进行修改
        List<DishFlavor> flavors = dishDTO.getFlavors();

        Long dishId = dishDTO.getId();
        //删除原有口味数据
        dishFlavorMapper.delete(dishId);
        //添加修改后的口味数据,(记得给口味数据id赋值)

        // 检查口味列表是否为空，如果为空则直接返回
        if (flavors == null || flavors.isEmpty()) {
            return;
        }

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        dishFlavorMapper.add(flavors);
    }

    /**
     * 菜品起售停售
     *
     * @param id
     * @param status
     */
    @Override
    public void upDataStatus(Long id, Integer status) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status).build();
        dishMapper.upData(dish);
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        List<Dish> dishes = dishMapper.getByCategoryId(categoryId);
        return dishes;
    }
}
