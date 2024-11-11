package com.tmesh.im.app.common.controller;

import com.tmesh.im.app.chat.service.ChatVersionService;
import com.tmesh.im.common.aspectj.IgnoreAuth;
import com.tmesh.im.common.constant.HeadConstant;
import com.tmesh.im.common.version.ApiVersion;
import com.tmesh.im.common.version.VersionEnum;
import com.tmesh.im.common.web.domain.AjaxResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 通用请求处理
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Resource
    private ChatVersionService versionService;

    /**
     * 校验版本号
     */
    @IgnoreAuth
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/getVersion")
    public AjaxResult getVersion(HttpServletRequest request) {
        // 请求的版本
        String version = request.getHeader(HeadConstant.VERSION);
        // 请求的设备
        String device = request.getHeader(HeadConstant.DEVICE);
        return AjaxResult.success(this.versionService.getVersion(version, device));
    }

    /**
     * 用户协议
     */
    @IgnoreAuth
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/getAgreement")
    public AjaxResult getAgreement() {
        return AjaxResult.success(this.versionService.getAgreement());
    }

}
