package com.tmesh.im.app.common.controller;


import com.tmesh.im.app.common.service.FileService;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.version.ApiVersion;
import com.tmesh.im.common.version.VersionEnum;
import com.tmesh.im.common.web.domain.AjaxResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 文件处理
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 通用上传请求
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/upload")
    public AjaxResult upload(MultipartFile file) {
        if (file == null) {
            throw new BaseException("上传文件不能为空");
        }
        return AjaxResult.success(this.fileService.uploadFile(file));
    }

    /**
     * 生成视频封面图
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/uploadVideo")
    public AjaxResult createVideoCover(MultipartFile file) {
        if (file == null) {
            throw new BaseException("上传文件不能为空");
        }
        // 调用视频处理工具类
        return AjaxResult.success(this.fileService.uploadVideo(file));
    }

    /**
     * 生成音频文字
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/uploadAudio")
    public AjaxResult uploadAudio(MultipartFile file) {
        if (file == null) {
            throw new BaseException("上传文件不能为空");
        }
        // 调用视频处理工具类
        return AjaxResult.success(this.fileService.uploadAudio(file));
    }

}
