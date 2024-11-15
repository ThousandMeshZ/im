package com.tmesh.im.app.chat.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 读取高德地图相关配置
 */
@Component
@Data
public class AmapConfig {

    @Value("${platform.amap.key}")
    private String key;

}