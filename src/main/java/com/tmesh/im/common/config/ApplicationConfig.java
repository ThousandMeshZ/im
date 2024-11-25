package com.tmesh.im.common.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.tmesh.im.common.enums.YesOrNoEnum;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 程序注解配置
 */

@Configuration
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的Mapper类的包的路径
@MapperScan({"com.tmesh.im.app.**.dao"})
// 扫描spring工具类
@ComponentScan(basePackages = {"cn.hutool.extra.spring"})
public class ApplicationConfig {

    /**
     * 时区配置
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.timeZone(TimeZone.getDefault());
    }

    /**
     * 序列化枚举值为数据库存储值
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    }

    @Bean
    public static MappingJackson2HttpMessageConverter objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        // 忽略未知的枚举字段
        objectMapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        // 忽略多余的字段不参与序列化
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // null 属性字段转 ""
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
                jsonGenerator.writeString("");
            }
        });

        SimpleModule simpleModule = new SimpleModule();
        // 格式化 Long
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        // 格式化时间
        simpleModule.addSerializer(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
                jsonGenerator.writeString(DateUtil.format(date, DatePattern.NORM_DATETIME_FORMAT));
            }
        });
        // 格式化金额
        simpleModule.addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public void serialize(BigDecimal decimal, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
                jsonGenerator.writeString(decimal.setScale(2, RoundingMode.HALF_DOWN).toString());
            }
        });

        // 注册 module
        objectMapper.registerModule(simpleModule);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Value("${platform.cors}")
    private String cors;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        if (YesOrNoEnum.YES.getCode().equals(cors)) {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            // 设置访问源地址，（*）表示匹配所有。
            config.addAllowedOriginPattern("*");
            // 设置访问源请求头
            config.addAllowedHeader("*");
            // 设置访问源请求方法
            config.addAllowedMethod("*");
            // 有效期 1800秒，设置预检请求（OPTIONS请求）的缓存时间，1800秒意味着客户端可以缓存这个设置1800秒
            config.setMaxAge(3600L);
            // 添加映射路径，拦截一切请求
            source.registerCorsConfiguration("/**", config);
        }
        // 返回新的CorsFilter
        return new CorsFilter(source);
    }
}
