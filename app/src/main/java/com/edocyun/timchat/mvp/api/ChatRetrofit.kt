package com.edocyun.timchat.mvp.api

import android.os.Build
import com.edocyun.timchat.MainApplication
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
        val (appName: String?, appVersion: String?) = getAppInfo()
        //TODO:
        builder.addHeader("Authorization", "XXXXXXXXXXXXXXXXXXXXX")
        builder.addHeader("Version", appVersion ?: "")
        builder.addHeader("OS-Version", Build.VERSION.RELEASE)
        builder.addHeader("Device-Type", "Android")
        builder.addHeader("Device-Name", appName ?: "云朵医生")
        builder.addHeader("Device-Model", Build.MODEL)
        //TODO:
        builder.addHeader("Device-Code", "xxxxx")
        builder.addHeader("Device-Brand", Build.BRAND)
        return builder
    }

    private fun getAppInfo(): Pair<String?, String?> {
        var appName: String = ""
        var appVersion: String = ""
        try {
            MainApplication.getApp()?.let {
                appVersion = it.packageManager?.getPackageInfo(
                    it.packageName,
                    0
                )?.versionName.toString()
                appName = it.applicationInfo
                    .loadLabel(it.packageManager).toString()
            }
        } catch (e: Exception) {
            appVersion = "unknown"
            appName = "unknown"
        }
        return Pair(appName, appVersion)
    }

    override fun getApiService(): Class<ChatApi> {
        return ChatApi::class.java
    }

}