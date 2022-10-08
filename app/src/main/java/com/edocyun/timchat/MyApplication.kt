package com.edocyun.timchat

import android.app.Application
import android.content.Context
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import kotlin.properties.Delegates


/**
 * @FileName: App.java
 * @author: villa_mou
 * @date: 08-10:05
 * @version V1.0 <描述当前版本功能>
 * @desc
 */
class MyApplication : Application() {
    companion object {
        var mContext: Context by Delegates.notNull()
            private set
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        TUIConfig.init(mContext)
        initLogger()
    }

    private fun initLogger() {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .methodOffset(7)
            .tag("villa")
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return true
            }
        })
    }

}