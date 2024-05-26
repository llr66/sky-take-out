package com.sky.controller.admin;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Api(tags = "分类管理相关接口")
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> Page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类管理进行分页查询:{}",categoryPageQueryDTO);
        PageResult pageResult=categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用、禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用分类")
    public Result<String> startOrStop(@PathVariable Integer status,Long id){
        log.info("接收到了id为{}的1分类的状态申请:{}",id,status);
        categoryService.startOrStop(status,id);
        return Result.success();
    }
}
