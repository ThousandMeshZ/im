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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import com.tmesh.im.app.chat.domain.ChatUser;
import com.tmesh.im.app.chat.service.ChatUserService;
import com.tmesh.im.app.shake.service.ShakeService;
import com.tmesh.im.app.shake.vo.ShakeVo01;
import com.tmesh.im.app.shake.vo.ShakeVo02;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.redis.GeoHashUtils;
import com.tmesh.im.common.redis.RedisUtils;
import com.tmesh.im.common.shiro.ShiroUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.stereotype.Service;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 摇一摇 服务层实现类
 */
@Service("shakeService")
public class ShakeServiceImpl implements ShakeService {

    @Autowired
    private GeoHashUtils geoHashUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private ChatUserService chatUserService;

    private final static String ERR_MSG = "暂无匹配到的结果";

    @Override
    public ShakeVo02 doShake(ShakeVo01 shakeVo) {
        this.sendShake(shakeVo);
        return this.getShake();
    }

    private void sendShake(ShakeVo01 shakeVo) {
        // 当前用户ID
        String userId = NumberUtil.toStr(ShiroUtils.getUserId());
        // 保存集合
        this.redisUtils.lRightPush(AppConstants.REDIS_SHAKE, userId);
        // 保存经纬度
        this.geoHashUtils.add(AppConstants.REDIS_GEO, shakeVo.getLongitude(), shakeVo.getLatitude(), userId);
    }

    private ShakeVo02 getShake() {
        if (!this.redisUtils.hasKey(AppConstants.REDIS_SHAKE)) {
            throw new BaseException(ERR_MSG);
        }
        String userId = this.redisUtils.lLeftPop(AppConstants.REDIS_SHAKE);
        String current = NumberUtil.toStr(ShiroUtils.getUserId());
        if (current.equals(userId)) {
            throw new BaseException(ShakeServiceImpl.ERR_MSG);
        }
        this.redisUtils.lRightPush(AppConstants.REDIS_SHAKE, userId);
        ChatUser chatUser = ChatUser.initUser(this.chatUserService.getById(NumberUtil.parseLong(userId)));
        Distance distance = this.geoHashUtils.dist(AppConstants.REDIS_GEO, userId, current);
        return BeanUtil.toBean(chatUser, ShakeVo02.class)
                .setDistance(distance.getValue())
                .setDistanceUnit(distance.getUnit());
    }

}
