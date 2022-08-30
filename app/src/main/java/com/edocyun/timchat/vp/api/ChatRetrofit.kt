package com.edocyun.timchat.vp.api

import com.edocyun.timchat.base.http.constant.ApiConfig
import com.edocyun.timchat.base.http.retrofit.RetrofitFactory
import okhttp3.Request

/**
 * FileName: com.beijing.zhagen.meiqi.http.retrofit.MainRetrofit.java
 * Author: mouxuefei
 * date: 2018/3/20
 * version: V1.0
 * desc:
 */

object ChatRetrofit : RetrofitFactory<ChatApi>() {
    override fun getBaseUrl() = ApiConfig.domain + ApiConfig.basePath + ApiConfig.apiVersion
    override fun getHeader(builder: Request.Builder): Request.Builder {
        builder.addHeader("token", "XXXXXXXXXXXXXXXXXXXXX")
        builder.addHeader("Content-Type", "application/json")
        /**
        const headers = {
        'Device-Brand': deviceInfo.brand,
        'Device-Code': deviceInfo.uniqueId,
        'Device-Model': deviceInfo.model,
        'Device-Name': deviceInfo.name,
        'Device-Type': Platform.select({ ios: 'iOS', default: 'Android' }),
        'OS-Version': deviceInfo.osVersion,
        Version: deviceInfo.appVersion,
        Authorization: reqToken,
        };
         */
        return builder
    }

    override fun getApiService(): Class<ChatApi> {
        return ChatApi::class.java
    }

}