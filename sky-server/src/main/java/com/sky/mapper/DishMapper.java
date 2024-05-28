package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void add(Dish dish);

    /**
     * 根据id批量获取菜品信息
     * @param idList
     * @return
     */
    List<Dish> getByIds(List<Long> idList);

    /**
     * 根据id批量删除菜品
     * @param idList
     */
    void deleteByIds(List<Long> idList);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.dish where id=#{id}")
    Dish getById(Long id);

    /**
     * 修改菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void upData(Dish dish);
}
