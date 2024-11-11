package com.tmesh.im.app.common.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.tmesh.im.app.chat.config.TencentConfig;
import com.tmesh.im.app.chat.utils.TencentUtils;
import com.tmesh.im.app.common.service.FileService;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.upload.service.UploadService;
import com.tmesh.im.common.upload.vo.UploadAudioVo;
import com.tmesh.im.common.upload.vo.UploadFileVo;
import com.tmesh.im.common.upload.vo.UploadVideoVo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 文件服务实现类
 */
@Service("fileService")
public class FileServiceImpl implements FileService {

    @Resource(name = "uploadOssService")
    private UploadService uploadService;

    @Autowired
    private TencentConfig tencentConfig;


    @Override
    public UploadFileVo uploadFile(MultipartFile file) {
        String fileType = FileNameUtil.extName(file.getOriginalFilename());
        if ("webp".equalsIgnoreCase(fileType)) {
            throw new BaseException(StrUtil.format("暂不支持{}格式上传", fileType));
        }
        // 上传
        return this.uploadService.uploadFile(file);
    }

    @Override
    public UploadVideoVo uploadVideo(MultipartFile videoFile) {
        // 上传视频文件
        UploadFileVo videoFileVo = this.uploadService.uploadFile(videoFile);
        // screenShot
        return BeanUtil.toBean(videoFileVo, UploadVideoVo.class)
                .setScreenShot(videoFileVo.getFullPath() + AppConstants.VIDEO_PARAM);
    }


    @Override
    public UploadAudioVo uploadAudio(MultipartFile audioFile) {
        // 上传音频文件
        UploadFileVo audioFileVo = this.uploadService.uploadFile(audioFile);
        String data;
        try {
            data = Base64.encode(audioFile.getInputStream());
        } catch (IOException e) {
            throw new BaseException("语音识别接口调用异常，请稍后再试");
        }
        return BeanUtil.toBean(audioFileVo, UploadAudioVo.class).setSourceText(TencentUtils.audio2Text(this.tencentConfig, data));
    }

}
