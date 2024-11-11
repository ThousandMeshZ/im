package com.tmesh.im.app.chat.service;

import cn.hutool.json.JSONObject;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 天气预报-服务层
 */
public interface ChatWeatherService {

    /**
     * 预报天气
     */
    List<JSONObject> queryByCityName(String cityName);

}
