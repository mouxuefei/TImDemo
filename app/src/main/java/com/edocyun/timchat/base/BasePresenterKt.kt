package com.edocyun.timchat.base

import io.reactivex.disposables.CompositeDisposable

open class BasePresenterKt<V : ITopView> : IPresenter<V> {
    override var mView: V? = null
    override val mDisposablePool: CompositeDisposable by lazy { CompositeDisposable() }

    override fun detachView() {
        super.detachView()
        if (!mDisposablePool.isDisposed) {
            mDisposablePool.clear()
        }
    }
}