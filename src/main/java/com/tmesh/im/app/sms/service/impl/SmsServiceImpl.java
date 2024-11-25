package com.tmesh.im.app.sms.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import com.tmesh.im.app.sms.enums.SmsTemplateEnum;
import com.tmesh.im.app.sms.enums.SmsTypeEnum;
import com.tmesh.im.app.sms.service.SmsService;
import com.tmesh.im.app.sms.vo.SmsVo;
import com.tmesh.im.common.config.PlatformConfig;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.redis.RedisUtils;
import com.tmesh.im.common.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 短信 服务层实现类
 */
@Service("smsService")
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RedisUtils redisUtils;

    @Value("${platform.sms.host}")
    private String host;
    @Value("${platform.sms.path}")
    private String path;
    @Value("${platform.sms.appcode}")
    private String appcode;
    @Value("${platform.sms.method}")
    private String method;
    @Value("${platform.sms.skin}")
    private String skin;
    
    @Override
    public Dict sendSms(SmsVo smsVo) {
        // 验证手机号
        if (!Validator.isMobile(smsVo.getPhone())) {
            throw new BaseException("请输入正确的手机号");
        }
        SmsTypeEnum smsType = smsVo.getType();
        String key = smsType.getPrefix().concat(smsVo.getPhone());
        // 生成验证码
        String code = String.valueOf(RandomUtil.randomInt(1000, 9999));
        // 发送短信
        if (YesOrNoEnum.YES.equals(PlatformConfig.SMS)) {
            Dict dict = Dict.create().set("code", code);
            this.doSendSms(smsVo.getPhone(), SmsTemplateEnum.VERIFY_CODE, dict);
        }
        log.info("短信验证码：{}", code);
        // 存入缓存
        this.redisUtils.set(key, code, smsType.getTimeout(), TimeUnit.MINUTES);
        return Dict.create().set("expiration", smsType.getTimeout());
    }
    
    @Override
    public void verifySms(String phone, String code, SmsTypeEnum smsType) {
        // 验证手机号
        if (!Validator.isMobile(phone)) {
            throw new BaseException("请输入正确的手机号");
        }
        String key = smsType.getPrefix().concat(phone);
        if (!this.redisUtils.hasKey(key)) {
            throw new BaseException("验证码已过期，请重新获取");
        }
        String value = this.redisUtils.get(key);
        if (value.equalsIgnoreCase(code)) {
            this.redisUtils.delete(key);
        } else {
            throw new BaseException("验证码buzq，请重新获取");
        }
    }

    /**
     * 执行发送短信
     *
     * @param phone
     * @param templateCode
     * @param dict
     */
    @Override
    public void doSendSms(String phone, SmsTemplateEnum templateCode, Dict dict) {
        //TODO 短信待集成
        sendCode(phone, templateCode, dict);
    }

    public Boolean sendCode(String phone, SmsTemplateEnum templateCode, Dict dict) {
        try {
            Map<String, String> headers = new HashMap<String, String>();
            //最后在 header 中的格式(中间是英文空格)
            headers.put("Authorization", "APPCODE " + appcode);
            Map<String, String> querys = new HashMap<String, String>();
            querys.put("mobile", phone);
            querys.put("content", "【TMeshMall】您的验证码是" + dict.getStr("code") + "。如非本人操作，请忽略本短信。");
            // (中间是英文空格)
            HttpResponse response = HttpUtils.doGet(host, path, headers, querys);
            JSONObject json = read(response.getEntity().getContent());
            System.out.print("获取返回的json: " + json);
            if (response.getStatusLine().getStatusCode() == 200) {
                String error_code = json.getStr("error_code");
                if ("0".equals(error_code)) {
                    log.error("正常请求计费(其他均不计费)");
                } else if ("201709".equals(error_code)) {
                    log.error("发送内容和模板不匹配,发送的内容和已审核通过模板不匹配,发送内容需和已审核通过的模板除了变量部分，其余部分必须一致");
                } else if ("201708".equals(error_code)) {
                    log.error("模板参数没有全部生效,短信内容不能包含特殊字符#,请检查参数重试");
                } else if ("201705".equals(error_code)) {
                    log.error("参数错误");
                } else if ("201706".equals(error_code)) {
                    log.error("短信长度超过限制");
                } else if ("201701".equals(error_code)) {
                    log.error("错误的手机号码");
                } else if ("201711".equals(error_code)) {
                    log.error("变量内容超过限定字符");
                } else if ("201710".equals(error_code)) {
                    log.error("有效号码不足");
                } else {
                    log.error("未知错误");
                }
            } else {
                log.error("未知错误");
            }
        } catch (MalformedURLException e) {
            log.error("URL格式错误");
            e.printStackTrace();
        } catch (UnknownHostException e) {
            log.error("URL地址错误");
            e.printStackTrace();
        } catch (Exception e) {
            // 打开注释查看详细报错异常信息
            e.printStackTrace();
        }
        return true;
    }

    /*
     * 读取返回结果
     */
    private static JSONObject read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return new JSONObject(sb.toString());
    }
}
