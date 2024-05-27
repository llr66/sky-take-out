package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 添加菜品口味表
 */
@Mapper
public interface DishFlavorMapper {

    void add(List<DishFlavor> dishFlavorList);
}
