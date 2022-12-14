package com.edocyun.timchat.util

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * @FileName: RxTimerUtil.java
 * @author: villa_mou
 * @date: 10-17:09
 * @version V1.0 <描述当前版本功能>
 * @desc
 */
object RxTimerUtil {
    private var mDisPosable: Disposable? = null
    /**
     * milliseconds毫秒后执行next操作
     */
    fun postDelay(milliseconds: Long, task: ((Long) -> Unit)? = null) {
        mDisPosable = Observable.timer(milliseconds, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { number ->
                task?.invoke(number)
                //取消订阅
                cancel()
            }
    }

    /**
     * 每隔milliseconds毫秒后执行next操作
     */
    fun interval(milliseconds: Long, task: ((Long) -> Unit)? = null) {
        mDisPosable = Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { number ->
                task?.invoke(number)
            }
    }

    /**
     * 取消订阅
     */
     fun cancel() {
        mDisPosable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }
}