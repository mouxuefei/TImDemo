package com.edocyun.timchat

import android.app.Application
import android.content.Context
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.BuildConfig
import com.orhanobut.logger.Logger
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException
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
        setRxJavaErrorHandler()
    }

    private fun initLogger() {
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    private fun setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(object : Consumer<Throwable?> {
            override fun accept(e: Throwable?) {
                if (e is UndeliverableException) {
                    return
                } else if (e is IOException) {
                    // fine, irrelevant network problem or API that throws on cancellation
                    return
                } else if (e is InterruptedException) {
                    // fine, some blocking code was interrupted by a dispose call
                    return
                } else if (e is NullPointerException || e is IllegalArgumentException) {
                    // that's likely a bug in the application
                    Thread.currentThread().uncaughtExceptionHandler.uncaughtException(
                        Thread.currentThread(),
                        e
                    )
                    return
                } else if (e is IllegalStateException) {
                    // that's a bug in RxJava or in a custom operator
                    Thread.currentThread().uncaughtExceptionHandler.uncaughtException(
                        Thread.currentThread(),
                        e
                    )
                    return
                }
                Logger.d("villa", "unknown exception=$e")
            }


        })
    }
}