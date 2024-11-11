package com.tmesh.im.app.chat.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 腾讯 nlp 配置
 */
@Component
@Data
public class TencentConfig {

    @Value("${platform.tencent.appId}")
    private String appId;

    @Value("${platform.tencent.appKey}")
    private String appKey;

    @Value("${platform.tencent.appSecret}")
    private String appSecret;

}