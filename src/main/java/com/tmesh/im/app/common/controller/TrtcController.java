package com.tmesh.im.app.common.controller;

import com.tmesh.im.app.common.service.TrtcService;
import com.tmesh.im.common.version.ApiVersion;
import com.tmesh.im.common.version.VersionEnum;
import com.tmesh.im.common.web.domain.AjaxResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 实时语音/视频
 */
@RestController
@RequestMapping("/trtc")
@Slf4j
public class TrtcController {

    @Resource
    private TrtcService trtcService;

    /**
     * 获取签名
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/getSign")
    public AjaxResult getSign() {
        return AjaxResult.success(this.trtcService.getSign());
    }

}
