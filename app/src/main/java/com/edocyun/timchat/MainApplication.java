package com.edocyun.timchat;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import androidx.annotation.Nullable;

/**
 * @version V1.0 <描述当前版本功能>
 * @FileName: MainApplication.java
 * @author: villa_mou
 * @date: 10-10:14
 * @desc
 */
public class MainApplication extends Application {
    private static MainApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initLogger();
    }

    private void initLogger() {
        PrettyFormatStrategy prettyFormatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(0)
                .methodOffset(7)
                .tag("villa")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(prettyFormatStrategy){
            @Override
            public boolean isLoggable(int priority, @Nullable @org.jetbrains.annotations.Nullable String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    public static MainApplication getApp() {
        return instance;
    }

}
