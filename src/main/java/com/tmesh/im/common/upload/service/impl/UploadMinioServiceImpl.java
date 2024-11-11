package com.tmesh.im.common.upload.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import com.tmesh.im.common.upload.config.UploadConfig;
import com.tmesh.im.common.upload.enums.UploadTypeEnum;
import com.tmesh.im.common.upload.service.UploadService;
import com.tmesh.im.common.upload.vo.UploadFileVo;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
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
 * @description : minio 上传
 */
@Slf4j
@Service("uploadMinioService")
@Configuration
@ConditionalOnProperty(prefix = "upload", name = "uploadType", havingValue = "minio")
public class UploadMinioServiceImpl extends UploadBaseService implements UploadService {

    @Resource
    private UploadConfig uploadConfig;

    /**
     * 获取上传凭证
     */
    private MinioClient initClient() {
        return MinioClient.builder()
                .endpoint(this.uploadConfig.getRegion())
                .credentials(this.uploadConfig.getAccessKey(), this.uploadConfig.getSecretKey())
                .build();
    }

    @Override
    public String getServerUrl() {
        return this.uploadConfig.getServerUrl();
    }

    @Override
    public Dict getToken(String fileType) {
        String serverUrl = this.uploadConfig.getServerUrl();
        String post = this.uploadConfig.getPost();
        return Dict.create()
                .set("uploadType", UploadTypeEnum.LOCAL)
                .set("serverUrl", serverUrl)
                .set("fileName", IdUtil.objectId() + "." + fileType)
                .set("post", post);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file) {
        return this.uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file, String folder) {
        String serverUrl = this.uploadConfig.getServerUrl();
        MinioClient client = this.initClient();
        String fileName = UploadBaseService.getFileName(file);
        String fileType = UploadBaseService.getFileType(file);
        String fileKey = UploadBaseService.getFileKey(file);
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(this.uploadConfig.getBucket())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(fileType)
                    .build();
            client.putObject(args);
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
        MinioClient client = this.initClient();
        String fileName = UploadBaseService.getFileName(file);
        String fileType = UploadBaseService.getFileType(file);
        String fileKey = UploadBaseService.getFileKey(file);
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(this.uploadConfig.getBucket())
                    .object(fileName)
                    .stream(FileUtil.getInputStream(file), FileUtil.size(file), -1)
                    .contentType(fileType)
                    .build();
            client.putObject(args);
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

}
