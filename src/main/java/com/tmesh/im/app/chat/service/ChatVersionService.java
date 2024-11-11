package com.tmesh.im.app.chat.service;


import com.tmesh.im.app.chat.domain.ChatVersion;
import com.tmesh.im.app.chat.vo.VersionVo;
import com.tmesh.im.common.web.service.BaseService;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 版本 服务层
 */
public interface ChatVersionService extends BaseService<ChatVersion> {

    /**
     * 用户协议
     */
    String getAgreement();

    /**
     * 获取版本
     */
    VersionVo getVersion(String version, String device);

}
