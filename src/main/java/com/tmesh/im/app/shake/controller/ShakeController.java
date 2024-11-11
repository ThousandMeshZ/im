package com.tmesh.im.app.shake.controller;

import com.tmesh.im.app.shake.service.ShakeService;
import com.tmesh.im.app.shake.vo.ShakeVo01;
import com.tmesh.im.common.version.ApiVersion;
import com.tmesh.im.common.version.VersionEnum;
import com.tmesh.im.common.web.controller.BaseController;
import com.tmesh.im.common.web.domain.AjaxResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 摇一摇
 */
@RestController
@Slf4j
@RequestMapping("/shake")
public class ShakeController extends BaseController {

    @Resource
    private ShakeService shakeService;

    /**
     * 发送摇一摇
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping(value = "/doShake")
    public AjaxResult sendShake(@Validated @RequestBody ShakeVo01 shakeVo) {
        return AjaxResult.success(this.shakeService.doShake(shakeVo));
    }

}
