package com.edocyun.timchat.vp.api

import android.os.Build
import com.edocyun.timchat.TUIConfig
import com.edocyun.timchat.network.constant.ApiConfig
import com.edocyun.timchat.network.retrofit.RetrofitFactory
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
        var appName: String?
        var appVersion: String?
        try {
            appVersion = TUIConfig.getAppContext().packageManager.getPackageInfo(TUIConfig.getAppContext().packageName, 0).versionName
            appName = TUIConfig.getAppContext().applicationInfo
                .loadLabel(TUIConfig.getAppContext().packageManager).toString()
        } catch (e: Exception) {
            appVersion = "unknown"
            appName = "unknown"
        }
        builder.addHeader("Authorization", "XXXXXXXXXXXXXXXXXXXXX")
        builder.addHeader("Version", appVersion?:"")
        builder.addHeader("OS-Version", Build.VERSION.RELEASE)
        builder.addHeader("Device-Type", "Android")
        builder.addHeader("Device-Name", appName?:"云朵医生")
        builder.addHeader("Device-Model", Build.MODEL)
        builder.addHeader("Device-Code", "xxxxx")
        builder.addHeader("Device-Brand", Build.BRAND)
        return builder
    }

    override fun getApiService(): Class<ChatApi> {
        return ChatApi::class.java
    }

}