package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> Page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("接收到了分页请求:{}", dishPageQueryDTO);
        log.info("传入Mapper方法的dishPageQueryDTO:{}",dishPageQueryDTO);
        PageResult pageResult = dishService.Page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result<String> addWithFlavor(@RequestBody DishDTO dishDTO)
    {
        log.info("接收到了菜品添加请求:{}",dishDTO);
        dishService.addWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result<String> deleteByIds(String ids){
        log.info("接收到了删除请求id:{}",ids);
        //将数据转换为Integer型集合
        String[] split = ids.split(",");
        List<Long> idList=new ArrayList<>();
        for (String s : split) {
            idList.add(Long.valueOf(s)
            );
        }
        log.info("转化后的id集合:{}",idList);
        dishService.deleteByIds(idList);

        return Result.success();
    }

    /**
     *根据id查询菜品
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("接收到查询菜品id:{}请求",id);
        DishVO dishVO=dishService.getById(id);
        log.info("查询到的DishVO数据:{}",dishVO);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result<String> upDate(@RequestBody DishDTO dishDTO){
        log.info("收到修改菜品的请求:{}",dishDTO);
        dishService.updata(dishDTO);
        return Result.success();
    }

    /**
     * 菜品起售停售
     * @param id
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> upDataStatus(Long id,@PathVariable Integer status ){
        log.info("接收到菜品状态更新请求:id{},status{}",id,status);
        dishService.upDataStatus(id,status);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        log.info("接收到菜品查询请求,分类id:{}",categoryId);
        List<Dish> dishes=dishService.getByCategoryId(categoryId);
        return Result.success(dishes);
    }
}
