package com.tmesh.im.common.config;

import cn.hutool.core.io.file.FileNameUtil;
import com.tmesh.im.common.version.DeviceInterceptor;
import com.tmesh.im.common.version.VersionHandlerMapping;
import com.tmesh.im.common.version.VersionInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : Mvc 配置
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Resource
    private VersionInterceptor versionInterceptor;

    @Resource
    private DeviceInterceptor deviceInterceptor;

    @Override
    public RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new VersionHandlerMapping();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(ApplicationConfig.objectMapper());
    }

    @Value("${platform.rootPath}")
    private String rootPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // favicon.ico
        registry.addResourceHandler(PlatformConfig.FAVICON).addResourceLocations("classpath:/static/");
        // file
        registry.addResourceHandler(PlatformConfig.PREVIEW).addResourceLocations("file:" + rootPath + FileNameUtil.UNIX_SEPARATOR);
    }

    /**
     * 自定义拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(versionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(PlatformConfig.FAVICON, PlatformConfig.PREVIEW);
        registry.addInterceptor(deviceInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(PlatformConfig.FAVICON, PlatformConfig.PREVIEW);
    }

}