package com.tmesh.im.common.upload.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.tmesh.im.common.upload.config.UploadConfig;
import com.tmesh.im.common.upload.enums.UploadTypeEnum;
import com.tmesh.im.common.upload.service.UploadService;
import com.tmesh.im.common.upload.utils.FastUtils;
import com.tmesh.im.common.upload.vo.UploadFileVo;
import jakarta.annotation.Resource;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : fast 上传
 */
@Slf4j
@Service("uploadFastService")
@Configuration
@NoArgsConstructor
@ConditionalOnProperty(prefix = "upload", name = "uploadType", havingValue = "fast")
public class UploadFastServiceImpl extends UploadBaseService implements UploadService {

    @Resource
    private UploadConfig uploadConfig;

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
        StorePath storePath;
        try {
            storePath = FastUtils.uploadFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        }
        String fileKey = storePath.getFullPath();
        String fileName = getFileName(file);
        String fileType = getFileType(file);
        String serverUrl = this.uploadConfig.getServerUrl();
        return format(fileName, serverUrl, fileKey, fileType);
    }

    @Override
    public UploadFileVo uploadFile(File file) {
        return this.uploadFile(file, null);
    }

    @Override
    public UploadFileVo uploadFile(File file, String folder) {
        StorePath storePath;
        try {
            storePath = FastUtils.uploadFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        }
        String fileKey = storePath.getFullPath();
        String fileType = getFileType(file);
        String fileName = getFileName(file);
        String serverUrl = this.uploadConfig.getServerUrl();
        return format(fileName, serverUrl, fileKey, fileType);
    }
}
