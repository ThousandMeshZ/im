package com.tmesh.im.app.common.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.json.JSONUtil;
import com.tmesh.im.app.common.config.TrtcConfig;
import com.tmesh.im.app.common.service.TrtcService;
import com.tmesh.im.app.common.vo.TrtcVo;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.redis.RedisUtils;
import com.tmesh.im.common.shiro.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 实时语音/视频 服务实现类
 */
@Service("trtcService")
public class TrtcServiceImpl implements TrtcService {

    @Autowired
    private TrtcConfig trtcConfig;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public TrtcVo getSign() {
        String key = AppConstants.REDIS_TRTC_SIGN + ShiroUtils.getUserId();
        if (this.redisUtils.hasKey(key)) {
            return JSONUtil.toBean(this.redisUtils.get(key), new TypeReference<TrtcVo>() {}, false);
        }
        String userId = AppConstants.REDIS_TRTC_USER + ShiroUtils.getUserId();
        long currTime = DateUtil.currentSeconds();
        Dict doc = Dict.create()
                .set("TLS.ver", "2.0")
                .set("TLS.identifier", userId)
                .set("TLS.sdkappid", this.trtcConfig.getAppId())
                .set("TLS.expire", this.trtcConfig.getExpire())
                .set("TLS.time", currTime)
                .set("TLS.sig", this.hmacsha256(userId, currTime));
        Deflater compressor = new Deflater();
        compressor.setInput(JSONUtil.toJsonStr(doc).getBytes(StandardCharsets.UTF_8));
        compressor.finish();
        byte[] bytes = new byte[2048];
        int length = compressor.deflate(bytes);
        compressor.end();
        TrtcVo trtcVo = new TrtcVo()
                .setUserId(userId)
                .setAppId(this.trtcConfig.getAppId())
                .setExpire(this.trtcConfig.getExpire())
                .setSign(this.base64EncodeUrl(ArrayUtil.resize(bytes, length)));
        this.redisUtils.set(key, JSONUtil.toJsonStr(trtcVo), 5, TimeUnit.DAYS);
        return trtcVo;
    }

    private String hmacsha256(String userId, long currTime) {
        String contentToBeSigned = "TLS.identifier:" + userId + "\n"
                + "TLS.sdkappid:" + this.trtcConfig.getAppId() + "\n"
                + "TLS.time:" + currTime + "\n"
                + "TLS.expire:" + this.trtcConfig.getExpire() + "\n";
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, StrUtil.bytes(this.trtcConfig.getSecret(), StandardCharsets.UTF_8));
        byte[] signed = mac.digest(contentToBeSigned);
        return Base64.encode(signed);
    }

    private String base64EncodeUrl(byte[] input) {
        byte[] base64 = Base64.encode(input).getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < base64.length; ++i)
            switch (base64[i]) {
                case '+':
                    base64[i] = '*';
                    break;
                case '/':
                    base64[i] = '-';
                    break;
                case '=':
                    base64[i] = '_';
                    break;
                default:
                    break;
            }
        return new String(base64);
    }
}
