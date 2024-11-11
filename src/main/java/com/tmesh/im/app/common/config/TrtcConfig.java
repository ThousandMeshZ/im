package com.tmesh.im.app.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 读取 trtc 相关配置
 */
@Component
@Data
public class TrtcConfig {

    @Value("${platform.trtc.appId}")
    private String appId;
    @Value("${platform.trtc.expire}")
    private String expire;
    @Value("${platform.trtc.secret}")
    private String secret;

}