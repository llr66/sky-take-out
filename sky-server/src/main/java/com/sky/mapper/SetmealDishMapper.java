package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id批量获取套餐菜品连接数据
     * @param idList
     * @return
     */
    List<SetmealDish> getByDishIds(List<Long> idList);

    /**
     * 批量加入菜品套餐关系
     * @param setmealDishes
     */
    void add(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐菜品关系行数据
     * @param idList
     */
    void deleteBySetmealId(List<Long> idList);

    /**
     * 根据套餐id查询套餐菜品数据
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> getBySetmealId(Long id);
}
