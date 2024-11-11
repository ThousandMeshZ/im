package com.tmesh.im.common.upload.domain;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import lombok.Data; /**
 * @author : TMesh
 * @version : 1.0.0
 * @description :
 */
@Data
public class FastDfsStorePath {
    private String group;
    private String path;
    private String fullPath;
    private String fileUrl;

    public FastDfsStorePath(StorePath storePath) {
        this.group = storePath.getGroup();
        this.path = storePath.getPath();
        this.fullPath = storePath.getFullPath();
    }
}
