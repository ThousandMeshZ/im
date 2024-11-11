/**
 * Licensed to the Apache Software Foundation （ASF） under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * （the "License"）； you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * https://www.q3z3.com
 * QQ : 939313737
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tmesh.im.app.shake.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.tmesh.im.app.chat.domain.ChatUser;
import com.tmesh.im.app.chat.service.ChatUserService;
import com.tmesh.im.app.shake.service.NearService;
import com.tmesh.im.app.shake.vo.NearVo01;
import com.tmesh.im.app.shake.vo.NearVo02;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.redis.GeoHashUtils;
import com.tmesh.im.common.redis.GeoVo;
import com.tmesh.im.common.shiro.ShiroUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 附近的人 服务层实现类
 */
@Service("nearService")
public class NearServiceImpl implements NearService {

    @Autowired
    private GeoHashUtils geoHashUtils;

    @Resource
    private ChatUserService chatUserService;

    @Override
    public List<NearVo02> doNear(NearVo01 nearVo) {
        sendNear(nearVo);
        return getNear();
    }

    @Override
    public void closeNear() {
        String userId = NumberUtil.toStr(ShiroUtils.getUserId());
        this.geoHashUtils.remove(AppConstants.REDIS_NEAR, userId);
    }

    private void sendNear(NearVo01 nearVo) {
        // 当前用户ID
        String userId = NumberUtil.toStr(ShiroUtils.getUserId());
        // 保存坐标
        this.geoHashUtils.add(AppConstants.REDIS_NEAR, nearVo.getLongitude(), nearVo.getLatitude(), userId);
    }

    private List<NearVo02> getNear() {
        // 当前用户
        String userId = NumberUtil.toStr(ShiroUtils.getUserId());
        // 1000公里内的9999个用户
        List<GeoResult<GeoVo>> geoResults = this.geoHashUtils.radius(AppConstants.REDIS_NEAR, userId, 100, 99);
        // 过滤
        List<String> userList = new ArrayList<>();
        List<NearVo02> dateList = geoResults.stream().collect(
                ArrayList::new, (x,y) -> {
                    String name = JSONUtil.parseObj(y.getContent()).getStr("name");
                    if (!userId.equals(name)) {
                        userList.add(name);
                        NearVo02 nearVo = new NearVo02()
                                .setUserId(NumberUtil.parseLong(name))
                                .setDistance(y.getDistance().getValue())
                                .setDistanceUnit(y.getDistance().getUnit());
                        x.add(nearVo);
                    }
                }
            , ArrayList::addAll);
        if (CollectionUtils.isEmpty(userList)) {
            return dateList;
        }
        Map<Long, ChatUser> mapList = this.chatUserService.getByIds(userList).stream()
                .collect(HashMap::new, (x, y) -> {
                    x.put(y.getUserId(), y);
                }, HashMap::putAll);
        // 转换
        dateList.forEach(nearVo -> {
            ChatUser chatUser = ChatUser.initUser(mapList.get(nearVo.getUserId()));
            nearVo.setPortrait(chatUser.getPortrait())
                    .setIntro(chatUser.getIntro())
                    .setNickName(chatUser.getNickName())
                    .setGender(chatUser.getGender());
        });
        return dateList;
    }
}
