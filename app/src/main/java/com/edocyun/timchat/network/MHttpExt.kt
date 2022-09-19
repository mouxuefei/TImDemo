package com.edocyun.timchat.network


import com.edocyun.timchat.network.entity.BaseEntity
import com.edocyun.timchat.base.ITopPresenter
import com.edocyun.timchat.base.ITopView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * 统一处理方法,弹窗,内存泄漏,线程调度
 */
fun <T> Observable<T>.bindDialogAndDisposable(
    view: ITopView? = null,
    iTopPresenter: ITopPresenter? = null,
    msg: String? = null
): Observable<T> = this.async().bindDialog(view, msg).bindDisposable(iTopPresenter)

/**
 * 线程切换,回调回主线程
 */
fun <T> Observable<T>.async(withDelay: Long = 0): Observable<T> =
    this.subscribeOn(Schedulers.io())
        .delay(withDelay, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())

/**
 * 线程切换,回调回子线程
 */
fun <T> Observable<T>.asyncIo(withDelay: Long = 0): Observable<T> =
    this.subscribeOn(Schedulers.io())
        .delay(withDelay, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.io())

/**
 * 绑定弹窗
 */
fun <T> Observable<T>.bindDialog(view: ITopView? = null, msg: String? = null): Observable<T> =
    this.doOnSubscribe {
        view?.showLoading(msg ?: "加载中...")
    }.doOnComplete {
        view?.dismissLoading()
    }.doOnError { view?.dismissLoading() }

/**
 * 绑定内存泄漏
 */
fun <T> Observable<T>.bindDisposable(iTopPresenter: ITopPresenter? = null): Observable<T> =
    this.doOnSubscribe {
        iTopPresenter?.mDisposablePool?.add(it)
    }

/**
 * 处理结果
 */
fun <T> Observable<T>.onResult(
    success: ((T) -> Unit)? = null,
    subErr: ((BaseEntity<*>) -> Unit)? = null
): Disposable =
    this.subscribe({
        val baseEntity = it as BaseEntity<*>
        if (baseEntity.success == true) {
            success?.invoke(it)
        } else {
            subErr?.invoke(baseEntity)
        }
    }, {
        subErr?.invoke(BaseEntity<Any>(null, false, null, "请求失败", null))
    })