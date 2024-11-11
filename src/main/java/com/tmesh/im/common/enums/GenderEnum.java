package com.tmesh.im.common.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 性别类型枚举
 */
@Getter
public enum GenderEnum {

    /**
     * 未知
     */
    UNKNOWN("0", "未知"),
    /**
     * 男
     */
    MALE("1", "男"),
    /**
     * 女
     */
    FEMALE("2", "女");

    /*
     * @EnumValue 在需要存储数据库的属性上添加注解，
     * @JsonValue 在需要前端展示的属性上添加注解；
     * @EnumValue用来标记数据库存的值
     * mybatis原生默认是以枚举的名称： Enum.name()作为默认值，即 User 类中定义的属性 private SexEnum sex;
     * 默认向数据库存的时候会将 GenderEnum.MAN.name()的值存入数据库(String 类型)，
     * 使用@EnumValue注解标识 GenderEnum 类中的 code 属性后，保存数据库时就会取值 code 保存进数据库。同样如果标识 value 保存时会取 value 的值(男/女)保存入库。
     * */
    @EnumValue
    @JsonValue
    private final String code;
    private final String info;

    GenderEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }
}
