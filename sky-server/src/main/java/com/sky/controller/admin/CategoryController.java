package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result<String> updata(@RequestBody CategoryDTO categoryDTO){
        log.info("需要修改分类{}",categoryDTO);
        categoryService.updata(categoryDTO);
        return Result.success();
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result<String> delete(Long id){
        log.info("需要删除分类的id:{}",id);
        categoryService.delete(id);

        return Result.error("test");
    }

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result<String> add(@RequestBody CategoryDTO categoryDTO){
        log.info("需要新增的分类:{}",categoryDTO);
        categoryService.add(categoryDTO);
        return Result.success();
    }

    /**
     *根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List> getByType(Integer type){
        log.info("查询{}类型的分类",type);
        List<Category> categoryList=categoryService.getByType(type);
        return Result.success(categoryList);
    }
}
