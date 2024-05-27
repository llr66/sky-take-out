package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/*
    通用接口
 */
@RestController
@Slf4j
@Api(tags = "通用接口")
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(@RequestBody MultipartFile file){
        log.info("接收到上传文件:{}",file);
        //将文件对象转换为字节数组,并用uuid作为随机名称填入参数(防止文件重名)
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //创建随机的UUid
            String fileNme = UUID.randomUUID().toString() + extension;

            //调用工具类方法上传文件并返回文件的url
            String url = aliOssUtil.upload(file.getBytes(), fileNme);
            log.info("响应的url:{}",url);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败:{}",e);
        }
        return null;
    }
}
