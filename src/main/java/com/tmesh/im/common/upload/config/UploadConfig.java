package com.tmesh.im.common.upload.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 上传类型枚举
 */
@Component
@Data
public class UploadConfig {

    /**
     * 服务端域名
     */
    @Value("${platform.upload.serverUrl}")
    private String serverUrl;
    /**
     * accessKey
     */
    @Value("${platform.upload.accessKey}")
    private String accessKey;
    /**
     * secretKey
     */
    @Value("${platform.upload.secretKey}")
    private String secretKey;
    /**
     * bucket
     */
    @Value("${platform.upload.bucket}")
    private String bucket;
    /**
     * region
     */
    @Value("${platform.upload.region}")
    private String region;
    /**
     * 封面
     */
    @Value("${platform.upload.post}")
    private String post;
    /**
     * 图片地址
     */
    @Value("${platform.upload.photoUrl}")
    private String photoUrl;
}

