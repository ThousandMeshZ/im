package com.tmesh.im.app.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.tmesh.im.app.chat.dao.ChatVersionDao;
import com.tmesh.im.app.chat.domain.ChatVersion;
import com.tmesh.im.app.chat.enums.VersionTypeEnum;
import com.tmesh.im.app.chat.service.ChatVersionService;
import com.tmesh.im.app.chat.vo.VersionVo;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.version.VersionUtils;
import com.tmesh.im.common.web.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 版本 服务层实现类
 */
@Service("chatVersionService")
public class ChatVersionServiceImpl extends BaseServiceImpl<ChatVersion> implements ChatVersionService {

    @Resource
    private ChatVersionDao chatVersionDao;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(this.chatVersionDao);
    }

    @Override
    public List<ChatVersion> queryList(ChatVersion t) {
        return this.chatVersionDao.queryList(t);
    }

    @Override
    public String getAgreement() {
        ChatVersion obj = this.findById(VersionTypeEnum.AGREEMENT.getCode());
        return obj.getUrl();
    }

    @Value("${platform.version}")
    private String version;

    @Override
    public VersionVo getVersion(String version, String device) {
        VersionTypeEnum versionType = initDevice(device);
        ChatVersion chatVersion = this.findById(versionType.getCode());
        YesOrNoEnum upgrade = VersionUtils.compareTo(version, chatVersion.getVersion()) < 0 ? YesOrNoEnum.YES : YesOrNoEnum.NO;
        YesOrNoEnum forceUpgrade = VersionUtils.compareTo(version, this.version) < 0 ? YesOrNoEnum.YES : YesOrNoEnum.NO;
        return BeanUtil.toBean(chatVersion, VersionVo.class)
                .setUpgrade(upgrade)
                .setForceUpgrade(forceUpgrade);
    }

    /**
     * 计算版本
     */
    private VersionTypeEnum initDevice(String device) {
        if (VersionTypeEnum.ANDROID.getName().equalsIgnoreCase(device)) {
            return VersionTypeEnum.ANDROID;
        }
        return VersionTypeEnum.IOS;
    }

}
