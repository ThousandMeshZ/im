package com.tmesh.im.app.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.tmesh.im.app.auth.service.TokenService;
import com.tmesh.im.app.auth.vo.AuthVo01;
import com.tmesh.im.app.chat.dao.ChatUserDao;
import com.tmesh.im.app.chat.domain.ChatUser;
import com.tmesh.im.app.chat.service.ChatUserService;
import com.tmesh.im.app.chat.vo.MyVo09;
import com.tmesh.im.app.push.service.ChatPushService;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.constant.HeadConstant;
import com.tmesh.im.common.enums.GenderEnum;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.exception.LoginException;
import com.tmesh.im.common.redis.GeoHashUtils;
import com.tmesh.im.common.redis.RedisUtils;
import com.tmesh.im.common.shiro.Md5Utils;
import com.tmesh.im.common.shiro.ShiroUtils;
import com.tmesh.im.common.utils.ServletUtils;
import com.tmesh.im.common.web.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 用户表 服务层实现
 */
@Slf4j
@Service("chatUserService")
public class ChatUserServiceImpl extends BaseServiceImpl<ChatUser> implements ChatUserService {

    @Resource
    private ChatUserDao chatUserDao;

    @Resource
    private TokenService tokenService;

    @Resource
    private ChatPushService chatPushService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private GeoHashUtils geoHashUtils;
    
    @Autowired
    public void setBaseDao() {
        super.setBaseDao(this.chatUserDao);
    }
    
    @Override
    public List<ChatUser> queryList(ChatUser chatUser) {
        List<ChatUser> chatUserList = this.chatUserDao.queryList(chatUser);
        return chatUserList;
    }
    
    @Override
    public void register(AuthVo01 authVo) {
        String phone = authVo.getPhone();
        String password = authVo.getPassword();
        String nickName = authVo.getNickName();
        String msg = "此手机号码已注册过，请勿重复注册";
        // 验证手机号是否注册过
        if (this.queryCount(new ChatUser()
                .setPhone(phone)) > 0) {
            throw new BaseException(msg);
        }
        String salt = RandomUtil.randomString(4);
        String chatNo = IdUtil.simpleUUID();
        ChatUser chatUser = new ChatUser()
                .setNickName(nickName)
                .setChatNo(chatNo)
                .setGender(GenderEnum.UNKNOWN)
                .setPortrait(AppConstants.DEFAULT_PORTRAIT)
                .setSalt(salt)
                .setPhone(phone)
                .setPassword(Md5Utils.credentials(password, salt))
                .setCreateTime(DateUtil.date());
        try {
            this.add(chatUser);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            throw new BaseException(msg);
        }
    }
    
    @Override
    public ChatUser queryByPhone(String phone) {
        return this.queryOne(new ChatUser().setPhone(phone));
    }
    
    @Override
    public Integer resetPass(Long userId, String password) {
        String salt = RandomUtil.randomString(4);
        ChatUser chatUser = new ChatUser()
                .setUserId(userId)
                .setSalt(salt)
                .setPassword(Md5Utils.credentials(password, salt));
        return this.updateById(chatUser);
    }
    
    @Override
    public Integer editPass(String password, String pwd) {
        // 当前用户
        ChatUser currentChatUser = this.getById(ShiroUtils.getUserId());
        if (!Md5Utils.credentials(password, currentChatUser.getSalt()).equalsIgnoreCase(currentChatUser.getPassword())) {
            throw new BaseException("旧密码不正确");
        }
        String salt = RandomUtil.randomString(4);
        ChatUser chatUser = new ChatUser()
                .setUserId(currentChatUser.getUserId())
                .setSalt(salt)
                .setPassword(Md5Utils.credentials(pwd, salt));
        return this.updateById(chatUser);
    }
    
    @Override
    public void editChatNo(String chatNo) {
        Long userId = ShiroUtils.getUserId();
        String errMsg = "微聊号已被占用，请重新输入";
        // 校验
        ChatUser chatUser = this.queryOne(new ChatUser().setChatNo(chatNo));
        if (chatUser != null && !userId.equals(chatUser.getUserId())) {
            throw new BaseException(errMsg);
        }
        try {
            // 更新
            this.updateById(new ChatUser().setUserId(userId).setChatNo(chatNo));
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            throw new BaseException(errMsg);
        }
    }

    @Override
    public MyVo09 getInfo() {
        // 当前用户
        ChatUser chatUser = this.findById(ShiroUtils.getUserId());
        return BeanUtil.toBean(chatUser, MyVo09.class)
                .setPhone(DesensitizedUtil.mobilePhone(chatUser.getPhone()));
    }

    @Override
    public String getQrCode() {
        Long userId = ShiroUtils.getUserId();
        return AppConstants.QR_CODE_USER + userId;
    }

    @Transactional
    @Override
    public void deleted() {
        // 移除缓存
        this.removeCache();
        // 更新用户
        ChatUser cu = new ChatUser()
                .setUserId(ShiroUtils.getUserId())
                .setDeletedTime(DateUtil.date());
        this.updateById(cu);
    }

    @Transactional
    @Override
    public Dict doLogin(AuthenticationToken authenticationToken) {
        String msg = null;
        try {
            ShiroUtils.getSubject().login(authenticationToken);
        } catch (LoginException e) {
            e.printStackTrace();
            msg = e.getMessage();
        } catch (AuthenticationException e) {
            e.printStackTrace();
            msg = "手机号或密码不正确";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "未知异常";
            log.error(msg, e);
        }
        if (StringUtils.hasText(msg)) {
            throw new BaseException(msg);
        }
        Long userId = ShiroUtils.getUserId();
        this.tokenService.deleteToken(this.getById(userId).getToken());
        // 生成新 token
        String token = this.tokenService.generateToken();
        String version = ServletUtils.getRequest().getHeader(HeadConstant.VERSION);
        ChatUser chatUser = new ChatUser()
                .setUserId(userId)
                .setToken(token)
                .setVersion(version);
        // 更新 token
        this.updateById(chatUser);
        return Dict.create().set("token", token);
    }
    
    @Override
    public void logout() {
        try {
            // 移除缓存
            this.removeCache();
            // 执行退出
            ShiroUtils.getSubject().logout();
            log.info("退出成功。。。。");
        } catch (Exception ex) {
            log.error("退出异常", ex);
        }
    }
    
    @Override
    public void refresh() {
        Long userId = ShiroUtils.getUserId();
        // 拉取离线消息
        this.chatPushService.pullMsg(userId);
    }

    /**
     * 移除缓存
     */
    private void removeCache() {
        Long userId = ShiroUtils.getUserId();
        ChatUser chatUser = this.getById(userId);
        if (chatUser != null) {
            // 清理 token
            this.tokenService.deleteToken(chatUser.getToken());
        }
        String userIdStr = NumberUtil.toStr(userId);
        // 附近的人
        this.geoHashUtils.remove(AppConstants.REDIS_NEAR, userIdStr);
        // 摇一摇
        this.redisUtils.lRemove(AppConstants.REDIS_SHAKE, 0, userIdStr);

    }
}
