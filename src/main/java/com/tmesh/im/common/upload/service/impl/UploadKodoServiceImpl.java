package com.tmesh.im.common.upload.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.tmesh.im.common.upload.config.UploadConfig;
import com.tmesh.im.common.upload.enums.UploadTypeEnum;
import com.tmesh.im.common.upload.service.UploadService;
import com.tmesh.im.common.upload.vo.UploadFileVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 七牛云上传
 */
@Slf4j
@Service("uploadKodoService")
@Configuration
@ConditionalOnProperty(prefix = "platform.upload", name = "uploadType", havingValue = "kodo")
public class UploadKodoServiceImpl extends UploadBaseService implements UploadService {

    @Resource
    private UploadConfig uploadConfig;

    /**
     * 获取Auth
     */
    private Auth getAuth() {
        return Auth.create(this.uploadConfig.getAccessKey(), this.uploadConfig.getSecretKey());
    }

    /**
     * 获取Token
     */
    private String getUploadToken(String fileType) {
        return getAuth().uploadToken(this.uploadConfig.getBucket(), fileType);
    }

    @Override
    public String getServerUrl() {
        return this.uploadConfig.getServerUrl();
    }

    @Override
    public Dict getToken(String fileType) {
        String serverUrl = this.uploadConfig.getServerUrl();
        String region = this.uploadConfig.getRegion();
        String post = this.uploadConfig.getPost();
        String fileName = IdUtil.objectId();
        String token;
        if (StringUtils.isEmpty(fileType)) {
            token = this.getUploadToken(fileType);
        } else {
            fileName += "." + fileType;
            token = this.getUploadToken(fileName);
        }
        return Dict.create()
                .set("uploadType", UploadTypeEnum.KODO)
                .set("serverUrl", serverUrl)
                .set("fileName", fileName)
                .set("region", region)
                .set("uploadToken", token)
                .set("post", post);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file) {
        return this.uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile file, String folder) {
        String fileName = UploadBaseService.getFileName(file);
        String fileKey = UploadBaseService.getFileKey(file, folder);
        String fileType = UploadBaseService.getFileType(file);
        String token = this.getUploadToken(fileKey);
        Response response = null;
        try {
            UploadManager uploadManager = new UploadManager(new com.qiniu.storage.Configuration());
            response = uploadManager.put(file.getInputStream(), fileKey, token, null, fileType);
            return format(fileName, this.uploadConfig.getServerUrl(), fileKey, fileType);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
    
    @Override
    public UploadFileVo uploadFile(File file) {
        return this.uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(File file, String folder) {
        String fileName = UploadBaseService.getFileName(file);
        String fileKey = UploadBaseService.getFileKey(file, folder);
        String fileType = UploadBaseService.getFileType(file);
        String token = this.getUploadToken(fileKey);
        InputStream inputStream = FileUtil.getInputStream(file);
        Response response = null;
        try {
            UploadManager uploadManager = new UploadManager(new com.qiniu.storage.Configuration());
            response = uploadManager.put(inputStream, fileKey, token, null, fileType);
            return format(fileName, this.uploadConfig.getServerUrl(), fileKey, fileType);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
