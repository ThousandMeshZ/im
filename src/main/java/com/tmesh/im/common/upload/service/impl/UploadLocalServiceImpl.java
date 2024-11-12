package com.tmesh.im.common.upload.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Dict;
import com.tmesh.im.common.upload.config.UploadConfig;
import com.tmesh.im.common.upload.enums.UploadTypeEnum;
import com.tmesh.im.common.upload.service.UploadService;
import com.tmesh.im.common.upload.vo.UploadFileVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 本地上传
 */
@Slf4j
@Service("uploadLocalService")
@Configuration
@ConditionalOnProperty(prefix = "platform.upload", name = "uploadType", havingValue = "local")
public class UploadLocalServiceImpl extends UploadBaseService implements UploadService {

    @Resource
    private UploadConfig uploadConfig;

    @Override
    public String getServerUrl() {
        return this.uploadConfig.getServerUrl();
    }

    @Override
    public Dict getToken(String fileType) {
        return Dict.create()
                .set("uploadType", UploadTypeEnum.LOCAL);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file) {
        return this.uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file, String folder) {
        String serverUrl = this.uploadConfig.getServerUrl();
        String uploadPath = this.uploadConfig.getRegion();
        String fileName = UploadBaseService.getFileName(file);
        String fileType = UploadBaseService.getFileType(file);
        // 文件路径
        String fileKey = UploadBaseService.getFileKey(uploadPath, fileType);
        try {
            // 文件拷贝
            file.transferTo(new File(uploadPath + FileNameUtil.UNIX_SEPARATOR + fileKey));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        }
        // 组装对象
        UploadFileVo fileVo = format(fileName, serverUrl, fileKey, fileType)
                .setFullPath(serverUrl + fileKey);
        return fileVo;
    }

    @Override
    public UploadFileVo uploadFile(File file) {
        return this.uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(File file, String folder) {
        String serverUrl = this.uploadConfig.getServerUrl();
        String uploadPath = this.uploadConfig.getRegion();
        String fileName = UploadBaseService.getFileName(file);
        String fileType = UploadBaseService.getFileType(file);
        // 文件路径
        String fileKey = UploadBaseService.getFileKey(uploadPath, fileType);
        // 文件拷贝
        FileUtil.copyFile(file, new File(uploadPath + FileNameUtil.UNIX_SEPARATOR + fileKey));
        // 组装对象
        UploadFileVo fileVo = format(fileName, serverUrl, fileKey, fileType)
                .setFullPath(serverUrl + fileKey);
        return fileVo;
    }
}
