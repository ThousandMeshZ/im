package com.tmesh.im.common.web.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tmesh.im.common.core.EnumUtils;
import com.tmesh.im.common.enums.GenderEnum;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.web.domain.AjaxResult;
import com.tmesh.im.common.web.domain.JsonDateDeserializer;
import com.tmesh.im.common.web.page.PageDomain;
import com.tmesh.im.common.web.page.TableDataInfo;
import com.tmesh.im.common.web.page.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.Date;
import java.beans.PropertyEditorSupport;
import java.util.List;

/**
  *
  * @Author TMesh
  * @Description web 控制层通用数据处理
  */
 
@Slf4j
public class BaseController {

    /**
    * 注册的属性编辑器 
    
    * 由 @InitBinder 注解修饰的方法用于初始化 WebDataBinder，从而实现请求参数的类型转换适配，
    * 例如日期字符串转换为日期 Date 类型，同时可以通过继承 PropertyEditorSupport 类来实现自定义 Editor，从而增加可以转换适配的类型种类。
    
    * @InitBinder 注解修饰的方法用于初始化 WebDataBinder 对象，
    * 能够实现：从 request 获取到 handler 方法中由 @RequestParam 注解或 @PathVariable 注解修饰的参数后，
    * 假如获取到的参数类型与 handler 方法上的参数类型不匹配，此时可以使用初始化好的 WebDataBinder 对获取到的参数进行类型处理。
    * 通常，如果在 @ControllerAdvice 注解修饰的类中使 @InitBinder 注解，
    * 此时 @InitBinder 注解修饰的方法所做的事情全局生效（前提是 @ControllerAdvice 注解没有设置 basePackages 字段）；
    * 如果在 @Controller 注解修饰的类中使用 @InitBinder 注解，此时 @InitBinder 注解修饰的方法所做的事情仅对当前 Controller 生效。
   
    * 由 @InitBinder 注解修饰的方法返回值类型必须为 void，入参必须为 WebDataBinder 对象实例。
    * 如果在 @Controller 注解修饰的类中使用 @InitBinder 注解则配置仅对当前类生效，如果在 @ControllerAdvice 注解修饰的类中使用 @InitBinder 注解则配置全局生效。
    
    * 将前台传递过来的日期格式的字符串，自动转化为 Date 类型
    * 其他类型转换
    */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Data 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(JsonDateDeserializer.parseDate(text));
            }
        });

        // YesOrNoEnum 类型转换
        binder.registerCustomEditor(YesOrNoEnum.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(EnumUtils.toEnum(YesOrNoEnum.class, text));
            }
        });

        // GenderTypeEnum 类型转换
        binder.registerCustomEditor(GenderEnum.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(EnumUtils.toEnum(GenderEnum.class, text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.getPageDomain();
        startPage(PageDomain.escapeOrderBySql(pageDomain.getOrderBy()));
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage(String orderBy) {
        PageDomain pageDomain = TableSupport.getPageDomain();
        PageHelper.startPage(pageDomain.getPageNum(), pageDomain.getPageSize(), StrUtil.toUnderlineCase(orderBy));
    }

    /**
     * 设置排序分页数据
     */
    protected void orderBy(String orderBy) {
        PageHelper.orderBy(StrUtil.toUnderlineCase(orderBy));
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list) {
        return new TableDataInfo(list, new PageInfo<>(list).getTotal());
    }

    protected TableDataInfo getDataTable(List<?> list, PageDomain pageDomain) {
        return getDataTable(CollUtil.sub(list, pageDomain.getPageStart(), pageDomain.getPageEnd()));
    }

    /**
     * 响应请求分页数据
     */
    protected TableDataInfo getDataTable(PageInfo<?> pageInfo) {
        return new TableDataInfo(pageInfo.getList(), pageInfo.getTotal());
    }

    /**
     * 响应返回结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.fail();
    }
}
