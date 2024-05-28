package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 批量添加菜品口味表
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量添加菜品口味信息
     * @param dishFlavorList
     */
    void add(List<DishFlavor> dishFlavorList);

    /**
     * 根据菜品id批量删除菜品口味信息
     * @param idList
     */
    void deletByDishIds(List<Long> idList);

    /**
     * 根据菜品id去插叙口味数据
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.dish_flavor where dish_id=#{id}")
    List<DishFlavor> getByDishId(Long id);
}
