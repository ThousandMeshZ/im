package com.tmesh.im.app.collect.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import com.tmesh.im.app.collect.dao.ChatCollectDao;
import com.tmesh.im.app.collect.domain.ChatCollect;
import com.tmesh.im.app.collect.service.ChatCollectService;
import com.tmesh.im.app.collect.vo.CollectVo01;
import com.tmesh.im.app.collect.vo.CollectVo02;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.shiro.ShiroUtils;
import com.tmesh.im.common.web.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 收藏表 服务层实现类
 */
@Service("chatCollectService")
public class ChatCollectServiceImpl extends BaseServiceImpl<ChatCollect> implements ChatCollectService {

    @Resource
    private ChatCollectDao chatCollectDao;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(this.chatCollectDao);
    }

    @Override
    public List<ChatCollect> queryList(ChatCollect t) {
        return this.chatCollectDao.queryList(t);
    }

    @Override
    public void addCollect(CollectVo01 collectVo) {
        ChatCollect collect = new ChatCollect()
                .setUserId(ShiroUtils.getUserId())
                .setCollectType(collectVo.getCollectType())
                .setContent(collectVo.getContent())
                .setCreateTime(DateUtil.date());
        this.add(collect);
    }

    @Override
    public void deleteCollect(Long collectId) {
        ChatCollect collect = this.getById(collectId);
        if (collect == null) {
            return;
        }
        if (!ShiroUtils.getUserId().equals(collect.getUserId())) {
            throw new BaseException("删除失败，不能删除别人的收藏");
        }
        this.deleteById(collectId);
    }

    @Override
    public PageInfo collectList(ChatCollect collect) {
        collect.setUserId(ShiroUtils.getUserId());
        List<ChatCollect> collectList = queryList(collect);
        List<CollectVo02> dataList = new ArrayList<>();
        collectList.forEach(collectEle -> 
            dataList.add(BeanUtil.toBean(collectEle, CollectVo02.class).setCollectId(collectEle.getId()))
        );
        return this.getPageInfo(dataList, collectList);
    }
}
