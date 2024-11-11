package com.tmesh.im.app.chat.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tmesh.im.app.chat.config.AmapConfig;
import com.tmesh.im.app.chat.service.ChatWeatherService;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 天气预报-服务层实现类
 */
@Service("chatWeatherService")
public class ChatWeatherServiceImpl implements ChatWeatherService {

    // 文档地址 https://lbs.amap.com/api/webservice/guide/api/weatherinfo

    /**
     * 接口地址
     */
    private final static String URL = "https://restapi.amap.com/v3/weather/weatherInfo?city=CITY&&key=KEY&extensions=EXT";
    private final static String EXT_BASE = "base";

    @Autowired
    private AmapConfig amapConfig;

    @Autowired
    private RedisUtils redisUtils;

    private JSONArray doQuery(String city, String extensions) {
        String key = StrUtil.format(AppConstants.REDIS_MP_WEATHER, city, extensions);
        if (this.redisUtils.hasKey(key)) {
            return JSONUtil.parseArray(this.redisUtils.get(key));
        }
        String url = this.URL.replace("CITY", city).replace("KEY", amapConfig.getKey()).replace("EXT", extensions);
        String result = HttpUtil.get(url);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if (1 != jsonObject.getInt("status")) {
            throw new BaseException("天气接口异常，请稍后再试");
        }
        JSONArray jsonArray = jsonObject.getJSONArray(EXT_BASE.equals(extensions) ? "lives" : "forecasts");
        if ("[[]]".equals(jsonArray.toString())) {
            return new JSONArray();
        }
        this.redisUtils.set(key, JSONUtil.toJsonStr(jsonArray), AppConstants.REDIS_MP_WEATHER_TIME, TimeUnit.MINUTES);
        return jsonArray;
    }

    @Override
    public List<JSONObject> queryByCityName(String cityName) {
        JSONArray jsonArray = doQuery(cityName, this.EXT_BASE);
        return jsonArray.toList(JSONObject.class);
    }

}
