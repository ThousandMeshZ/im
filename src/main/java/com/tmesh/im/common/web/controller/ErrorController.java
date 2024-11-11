package com.tmesh.im.common.web.controller;

import com.tmesh.im.common.aspectj.IgnoreAuth;
import com.tmesh.im.common.core.EnumUtils;
import com.tmesh.im.common.enums.ResultCodeEnum;
import com.tmesh.im.common.web.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 错误请求处理
 */

@RestController
@RequestMapping("/error")
@Slf4j
public class ErrorController {

    @IgnoreAuth
    @RequestMapping("/{code}")
    public AjaxResult error(@PathVariable String code) {
        AjaxResult ajaxResult;
        ResultCodeEnum resultCode = EnumUtils.toEnum(ResultCodeEnum.class, code, ResultCodeEnum.FAIL);
        switch (resultCode) {
            case SUCCESS -> ajaxResult = AjaxResult.success();
            default -> ajaxResult = AjaxResult.result(resultCode);
        }
        return ajaxResult;
    }
}
