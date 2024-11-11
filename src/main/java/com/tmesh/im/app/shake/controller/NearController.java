package com.tmesh.im.app.shake.controller;

import com.tmesh.im.app.shake.service.NearService;
import com.tmesh.im.app.shake.vo.NearVo01;
import com.tmesh.im.common.version.ApiVersion;
import com.tmesh.im.common.version.VersionEnum;
import com.tmesh.im.common.web.controller.BaseController;
import com.tmesh.im.common.web.domain.AjaxResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 附近的人
 */
@RestController
@Slf4j
@RequestMapping("/near")
public class NearController extends BaseController {

    @Resource
    private NearService nearService;

    /**
     * 发送附近的人
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping(value = "/doNear")
    public AjaxResult doNear(@Validated @RequestBody NearVo01 nearVo) {
        return AjaxResult.success(this.nearService.doNear(nearVo));
    }

    /**
     * 关闭附近的人
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping(value = "/closeNear")
    public AjaxResult closeNear() {
        this.nearService.closeNear();
        return AjaxResult.success();
    }

}
