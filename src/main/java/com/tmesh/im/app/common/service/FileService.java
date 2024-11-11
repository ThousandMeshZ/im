package com.tmesh.im.app.common.service;

import com.tmesh.im.common.upload.vo.UploadAudioVo;
import com.tmesh.im.common.upload.vo.UploadFileVo;
import com.tmesh.im.common.upload.vo.UploadVideoVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 文件服务
 */
public interface FileService {

    /**
     * 文件上传
     */
    UploadFileVo uploadFile(MultipartFile file);

    /**
     * 文件视频
     */
    UploadVideoVo uploadVideo(MultipartFile file);

    /**
     * 文件音频
     */
    UploadAudioVo uploadAudio(MultipartFile file);

}
