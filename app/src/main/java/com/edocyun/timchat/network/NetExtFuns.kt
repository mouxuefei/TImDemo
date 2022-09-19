/*
 * Copyright (c) 18-1-8 下午1:38. XQ Yang
 */

package com.edocyun.timchat.network

import com.edocyun.timchat.network.entity.BaseEntity
import com.edocyun.timchat.base.ITopPresenter
import com.edocyun.timchat.base.ITopView
import com.orhanobut.logger.Logger
import io.reactivex.Observable


open class BaseSubscribeWrap<R> {
    internal var iPresenter: ITopPresenter? = null
    internal lateinit var api: Observable<R>
    internal var iView: ITopView? = null
    internal var success: ((result: R) -> Unit) = {}
    internal var error: ((e: BaseEntity<*>) -> Unit) = {}
    internal var loadingMsg: String? = null
    internal var doOnIo: Boolean = false

    fun bindDisPool(disPool: ITopPresenter) {
        this.iPresenter = disPool
    }

    /**
     * 在子线程回调
     */
    fun doOnIo() {
        this.doOnIo = true
    }

    /**
     *
     */
    fun bindLoading(loadingView: ITopView?, loadingMsg: String? = null) {
        this.iView = loadingView
        this.loadingMsg = loadingMsg
    }

    fun api(service: () -> Observable<R>) {
        this.api = service.invoke()
    }

    fun onSuccess(success: ((result: R) -> Unit)) {
        this.success = success
    }

    fun onError(error: ((e: BaseEntity<*>) -> Unit)) {
        this.error = error
    }
}

fun <RESULT> fetch(init: BaseSubscribeWrap<RESULT>.() -> Unit) {
    val wrap = BaseSubscribeWrap<RESULT>()
    wrap.init()
    if (wrap.iPresenter == null) {
        Logger.e("disPool is null, Memory leaks are possible!!!")
    }
    if (wrap.doOnIo) {
        wrap.api = wrap.api.asyncIo()
    } else {
        wrap.api = wrap.api.async()
    }
    wrap.iView?.let {
        wrap.api = wrap.api.bindDialog(it, wrap.loadingMsg)
    }

    wrap.iPresenter?.let {
        wrap.api = wrap.api.bindDisposable(it)
    }
    wrap.api.onResult({
        wrap.success.invoke(it)
    }, {
        wrap.error.invoke(it)
    })
}
/**
 *  这个是传啥，返回啥，没有包裹一个公共属性
 */
//@SuppressLint("CheckResult")
//fun <RESULT> httpUni(init: BaseSubscribeUniWrap<RESULT, ProjectCoreHttpService>.() -> Unit) {
//    val wrap = BaseSubscribeUniWrap<RESULT, ProjectCoreHttpService>()
//    wrap.init()
//    val subUniversual = object : AbstractSubscribeUniversal<RESULT, ProjectCoreHttpService>(wrap.disPool, wrap.parameter) {
//        override fun getObservable(service: ProjectCoreHttpService, factory: RequestMap): Observable<RESULT> {
//            return wrap.apiSelectUniversal.invoke(service, factory)
//        }
//    }
//    if (wrap.doOnIo) {
//        ProjectCoreHttpManager.dealOnIO(subUniversual)
//    } else {
//        ProjectCoreHttpManager.deal(subUniversual)
//    }.subscribe(object : BaseObserver<RESULT>(wrap.loadingView, msg = wrap.loadingMsg) {
//        override fun onStart() {
//            wrap.needLockView?.get()?.isEnabled = false
//            super.onStart()
//            wrap.stateListener?.postValue(RequestState.LOADING)
//        }
//
//        override fun onNext(t: RESULT) {
//            wrap.success.invoke(t)
//        }
//
//        override fun onComplete() {
//            super.onComplete()
////            wrap.stateListener?.postValue(RequestState.LOADED)
//            wrap.needLockView?.get()?.isEnabled = true
//            wrap.needLockView?.clear()
//            wrap.needLockView = null
//        }
//
//        override fun onError(e: Throwable) {
//            super.onError(e)
//            wrap.error.invoke(e)
//            wrap.stateListener?.postValue(RequestState.error(e.message))
//            wrap.needLockView?.get()?.isEnabled = true
//            wrap.needLockView?.clear()
//            wrap.needLockView = null
//        }
//    })
//}


//var mDialog: WeakReference<MLoadingView>? = null
//
//fun dismiss() {
//    mDialog?.get()?.dismissProgressDialog()
//    mDialog?.clear()
//    mDialog = null
//}
//
//@Deprecated("用扩展dsl")
//fun <T : IStateBean> Observable<T>.dSubscribe(context: Context, msg: String = "", autoDismiss: Boolean = true, onError: ((Throwable) -> Unit)? = null, onSuccess: (T) -> Unit) {
//    this.subscribe(object : Observer<T> {
//        override fun onComplete() {
//            if (autoDismiss) {
//                dismiss()
//            }
//        }
//
//        override fun onSubscribe(d: Disposable) {
//            if (!NetworkUtils.isConnected) {
//                ToastUtils.showShort("当前无网络")
//                onComplete()
//            } else {
//                if (mDialog == null || mDialog?.get() == null) {
//                    mDialog = WeakReference(MLoadingView(context, R.style.dialog_transparent_style))
//                }
//                mDialog?.get()!!.show(if (msg.isNotEmpty()) "${msg}中..." else "", true, autoDismiss)
//            }
//        }
//
//        override fun onNext(t: T) {
//            if (t.isOk()) {
//                onSuccess.invoke(t)
//            } else {
//                if (!TextUtils.isEmpty(t.message)) {
//                    onError(ResultException(t.state, t.message))
//                } else {
//                    onError(ResultException(t.state, msg + "失败,请检查网络或稍后重试"))
//                }
//            }
//            if (autoDismiss) {
//                dismiss()
//            }
//        }
//
//        override fun onError(e: Throwable) {
//            if (e is ResultException) {
//                ToastUtils.showShort(e.message)
//            }
//            onError?.invoke(e)
//            dismiss()
//        }
//    })
//}
