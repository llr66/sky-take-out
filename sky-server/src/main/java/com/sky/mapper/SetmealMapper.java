package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增套餐
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    @Options(keyProperty = "id",useGeneratedKeys = true)
    @Insert("insert into sky_take_out.setmeal(category_id, name, price, description, image, create_time, update_time, create_user, update_user) VALUES" +
            "(#{categoryId},#{name},#{price},#{description},#{image},#{createTime},#{updateTime},#{createUser},#{updateUser}) ")
    void add(Setmeal setmeal);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id集合查找套餐
     * @param idList
     * @return
     */
    List<Setmeal> getByIds(List<Long> idList);

    /**
     * 删除套餐
     * @param idList
     */
    void deleteByIds(List<Long> idList);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.setmeal where id =#{id}")
    Setmeal getById(Long id);
}
