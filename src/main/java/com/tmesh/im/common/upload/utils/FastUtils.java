package com.tmesh.im.common.upload.utils;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : FastUtils
 */
@Slf4j
@Component
public class FastUtils {

    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    private ThumbImageConfig thumbImageConfig;

    @Resource
    private FdfsWebServer fdfsWebServer;

/*     public FastUtils(ThumbImageConfig thumbImageConfig, FastFileStorageClient fastFileStorageClient, FdfsWebServer fdfsWebServer) {
        FastUtils.thumbImageConfig = thumbImageConfig;
        FastUtils.fastFileStorageClient = fastFileStorageClient;
        FastUtils.fdfsWebServer = fdfsWebServer;
    } */

    public StorePath uploadFile(MultipartFile multipartFile) {
        StorePath storePath;
        try {
            storePath = this.fastFileStorageClient.uploadFile(multipartFile.getInputStream(), multipartFile.getSize(), FilenameUtils.getExtension(multipartFile.getOriginalFilename()), (Set) null);
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
            throw new RuntimeException("文件上传失败");
        }
        return storePath;
    }

    public StorePath uploadFile(File file) {
        StorePath storePath;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            storePath = this.fastFileStorageClient.uploadFile(inputStream, file.length(), FilenameUtils.getExtension(file.getName()), null);
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
            throw new RuntimeException("文件上传失败");
        }
        return storePath;
    }

}
