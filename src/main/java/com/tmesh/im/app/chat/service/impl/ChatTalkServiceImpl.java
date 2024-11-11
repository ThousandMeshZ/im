package com.tmesh.im.app.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.tmesh.im.app.chat.config.TencentConfig;
import com.tmesh.im.app.chat.enums.ApplySourceEnum;
import com.tmesh.im.app.chat.enums.FriendTypeEnum;
import com.tmesh.im.app.chat.service.ChatTalkService;
import com.tmesh.im.app.chat.service.ChatUserService;
import com.tmesh.im.app.chat.service.ChatWeatherService;
import com.tmesh.im.app.chat.utils.TencentUtils;
import com.tmesh.im.app.chat.vo.FriendVo06;
import com.tmesh.im.app.chat.vo.FriendVo07;
import com.tmesh.im.app.push.vo.PushParamVo;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.shiro.ShiroUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 系统聊天 服务层实现类
 */
@Service("chatTalkService")
public class ChatTalkServiceImpl implements ChatTalkService {

    private static final Logger log = LoggerFactory.getLogger(ChatTalkServiceImpl.class);
    @Autowired
    private TencentConfig tencentConfig;

    @Resource
    private ChatWeatherService weatherService;

    @Resource
    private ChatUserService chatUserService;

    /**
     * 好友列表
     */
    private static List<FriendVo06> friendList() {
        // 天气机器人
        Long weatherId = 10002L;
        FriendTypeEnum weatherType = FriendTypeEnum.WEATHER;
        FriendVo06 weather = new FriendVo06()
                .setUserId(weatherId)
                .setChatNo(NumberUtil.toStr(weatherId))
                .setNickName(weatherType.getInfo())
                .setPortrait("http://q3z3-im.oss-cn-beijing.aliyuncs.com/0295f6edc9de43748c0d2f25b5057893.png")
                .setUserType(weatherType);
        // 翻译机器人
        Long translationId = 10003L;
        FriendTypeEnum translationType = FriendTypeEnum.TRANSLATION;
        FriendVo06 translation = new FriendVo06()
                .setUserId(translationId)
                .setChatNo(NumberUtil.toStr(translationId))
                .setNickName(translationType.getInfo())
                .setPortrait("http://q3z3-im.oss-cn-beijing.aliyuncs.com/18ac0b6aa3d147e6b1a65c2eb838707e.png")
                .setUserType(translationType);
        return CollUtil.newArrayList(weather, translation);
    }

    @Override
    public List<FriendVo06> queryFriendList() {
        Long userId = ShiroUtils.getUserId();
        List<FriendVo06> userList = friendList();
        userList.add(BeanUtil.toBean(this.chatUserService.findById(userId), FriendVo06.class).setUserType(FriendTypeEnum.SELF));
        return userList;
    }

    @Override
    public FriendVo07 queryFriendInfo(Long userId) {
        Map<Long, FriendVo06> dataList = friendList().stream().collect(
                Collectors.toMap(FriendVo06::getUserId, friendVo06 -> friendVo06, (k1, k2) -> k1)
        );
        FriendVo06 friendVo = dataList.get(userId);
        if (friendVo == null) {
            return null;
        }
        return BeanUtil.toBean(friendVo, FriendVo07.class)
                .setIsFriend(YesOrNoEnum.YES)
                .setSource(ApplySourceEnum.SYS);
    }

    @Override
    public PushParamVo talk(Long userId, String content) {
        Map<Long, FriendVo06> dataList = friendList().stream().collect(
                Collectors.toMap(FriendVo06::getUserId, friendVo06 -> friendVo06, (k1, k2) -> k1)
        );
        FriendVo06 friendVo = dataList.get(userId);
        if (friendVo == null) {
            return null;
        }
        PushParamVo paramVo = new PushParamVo()
                .setUserId(friendVo.getUserId())
                .setPortrait(friendVo.getPortrait())
                .setNickName(friendVo.getNickName())
                .setContent(content)
                .setUserType(friendVo.getUserType());
        switch (friendVo.getUserType()) {
            case WEATHER:
                content = this.weather(content);
                break;
            case TRANSLATION:
                content = TencentUtils.translation(this.tencentConfig, content);
                break;
        }
        return paramVo.setContent(content);
    }

    /**
     * 天气预报
     */
    private String weather(String content) {
        List<JSONObject> dataList = this.weatherService.queryByCityName(content);
        if (CollectionUtils.isEmpty(dataList)) {
            return "暂未找到结果，格式：北京市";
        }
        StringBuilder builder = new StringBuilder();
        dataList.forEach(e -> {
            builder.append("城市：");
            builder.append(e.getStr("province"));
            builder.append(e.getStr("city"));
            builder.append("\n");
            builder.append("天气：");
            builder.append(e.getStr("weather"));
            builder.append("\n");
            builder.append("温度：");
            builder.append(e.getStr("temperature"));
            builder.append("℃");
            builder.append("\n");
            builder.append("风力：");
            builder.append(e.getStr("windpower"));
            builder.append("级");
            builder.append("\n");
            builder.append("湿度：");
            builder.append(e.getStr("temperature"));
            builder.append("RH");
            builder.append("\n");
            builder.append("\n");
        });
        return StrUtil.removeSuffix(builder.toString(), "\n\n");
    }

    public static void main(String[] args) {
        log.info("{}", IdUtil.simpleUUID());
    }
}
