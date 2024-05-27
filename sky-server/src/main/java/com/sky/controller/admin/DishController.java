package com.sky.controller.admin;

import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
    菜品相关接口
 */
@RestController
@Slf4j
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishController {
@Autowired
    DishService dishService;

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
public Result<PageResult> Page(DishPageQueryDTO dishPageQueryDTO){
    log.info("接收到了分页请求:{}",dishPageQueryDTO);
    PageResult pageResult=dishService.Page(dishPageQueryDTO);
    return Result.success(pageResult);
}
}
