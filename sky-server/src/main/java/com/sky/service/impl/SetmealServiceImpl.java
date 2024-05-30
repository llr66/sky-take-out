package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void add(SetmealDTO setmealDTO) {

        //并且给这两个方法设置一个事务管理因为他们得同时生效

        //新增套餐表数据,并进行主键回显获取主键id
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.add(setmeal);
        Long setmealId=setmeal.getId();

        //批量添加菜品套餐关系表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        log.info("菜品套餐关系表添加数据:{}",setmealDishes);
        setmealDishMapper.add(setmealDishes);
    }

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {

        //使用分页工具进行分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<Setmeal> setmeals=setmealMapper.pageQuery(setmealPageQueryDTO);

        Long total=setmeals.getTotal();
        List<Setmeal> setmealLists=setmeals.getResult();

        PageResult pageResult=new PageResult(total,setmealLists);

        return pageResult;

    }
}
