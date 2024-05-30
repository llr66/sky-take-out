package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐")
    public Result<String> add(@RequestBody SetmealDTO setmealDTO) {
        log.info("接收到了新增套餐请求:{}", setmealDTO);
        setmealService.add(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> Page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("接收到套餐分页请求:{}",setmealPageQueryDTO);
        PageResult pageResult=setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);

    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result<String> delete(String ids){
        log.info("接收到了批量删除请求:{}",ids);
        String[] split = ids.split(",");
        List<Long> idList=new ArrayList<>();
        for (String s : split) {
            idList.add( Long.parseLong(s));
        }
        log.info("转换后的id集合:{}",idList);
        setmealService.delete(idList);
        return Result.success();
    }
}
