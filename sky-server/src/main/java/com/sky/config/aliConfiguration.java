package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类,用于创建AliOssUtil对象,并将其交给IOC容器创建管理
 */
@Configuration
@Slf4j
public class aliConfiguration {
    @Bean
    @ConditionalOnMissingBean
    //这里aliOssProperties声明了@ConfigurationProperties同样会交给IOC容器管理
    //这里可以直接创建参数进行接收,这里的参数来源是去IOC容器中寻找
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传工具类对象");

        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),aliOssProperties.
                getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
