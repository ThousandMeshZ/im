package com.tmesh.im.common.config;

import com.tmesh.im.common.core.EnumUtils;
import com.tmesh.im.common.enums.YesOrNoEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 读取项目相关配置
 */
@Component
@Configuration
@ConfigurationProperties(prefix = "platform")
public class PlatformConfig {

    /**
     * 上传路径
     */
    @Value("${platform.rootPath}")
    public static String ROOT_PATH;

    /**
     * 文件预览
     */
    public static String PREVIEW = "/preview/**";

    /**
     * 图标
     */
    public static String FAVICON = "/favicon.ico";

    /**
     * token超时时间（分钟）
     */
    @Value("${platform.timeout}")
    public static Integer TIMEOUT;

    /**
     * 是否开启短信
     */
    @Value("${platform.sms.open:N}")
    public static YesOrNoEnum SMS;

    /* @Value("${platform.timeout}")
    public void setTokenTimeout(Integer timeout) {
        PlatformConfig.TIMEOUT = timeout;
    } */

    /* @Value("${platform.sms:N}")
    public void setSms(String sms) {
        PlatformConfig.SMS = EnumUtils.toEnum(YesOrNoEnum.class, sms, YesOrNoEnum.NO);
    } */

    /* @Value("${platform.rootPath}")
    public void setRootPath(String rootPath) {
        PlatformConfig.ROOT_PATH = rootPath;
    } */

}
