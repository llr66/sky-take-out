package com.sky.service;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     *启用、禁用分类
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);
}
