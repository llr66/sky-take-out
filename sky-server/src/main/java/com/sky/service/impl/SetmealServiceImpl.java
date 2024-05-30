package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    /**
     * 批量删除套餐
     * @param idList
     */
    @Override
    public void delete(List<Long> idList) {
        //删除前判断套餐的状态
        List<Setmeal> setmeals=setmealMapper.getByIds(idList);
        log.info("请求删除的套餐:{}",setmeals);
        for (Setmeal setmeal : setmeals) {
            if(setmeal.getStatus()==1){
                throw new BaseException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //删除套餐表行数据
        setmealMapper.deleteByIds(idList);
        //删除套餐菜品关系中间表行数据
        setmealDishMapper.deleteBySetmealIds(idList);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        //获取套餐表数据
        Setmeal setmeal=setmealMapper.getById(id);
        log.info("查询到的套餐数据:{}",setmeal);
        //获取套餐菜品关系表数据
        List<SetmealDish> setmealDishes=setmealDishMapper.getBySetmealId(id);
        log.info("查询到的套餐菜品关系数据:{}",setmealDishes);

        SetmealVO setmealVO=new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    public void updata(SetmealDTO setmealDTO) {
        //修改套餐表数据(记得给Mapper方法打上注解交给AOP代理)
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.updata(setmeal);
        //修改套餐菜品数据

        List<SetmealDish> setmealDishes=setmealDTO.getSetmealDishes();
        Long setmealId=setmealDTO.getId();
        //给加上setmealID属性
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }

        log.info("删除原先套餐菜品关系id:{}",setmealId);
        //删除原来的套餐菜品关系数据
        setmealDishMapper.deleteBySetmealId(setmealId);
        //更新添加套餐菜品关系数据
        setmealDishMapper.add(setmealDishes);
    }

    /**
     * 套餐起售,停售
     * @param status
     * @param id
     */
    @Override
    public void updataStatus(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status).build();

        setmealMapper.updata(setmeal);
    }
}
