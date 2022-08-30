package com.edocyun.timchat.vp.contract

import com.edocyun.timchat.base.mvp.IPresenter
import com.edocyun.timchat.base.mvp.IView
import com.edocyun.timchat.vp.api.MessageEntity

/**
 * @FileName: IMainContact.java
 * @author: villa_mou
 * @date: 06-16:34
 * @version V1.0 <描述当前版本功能>
 * @desc
 */
interface IChatContact {
    interface View : IView<Presenter> {
        fun getMessageResponse(data: ArrayList<MessageEntity>?)
    }

    interface Presenter : IPresenter<View> {
        fun fetchData(isLoadMore:Boolean)
    }
}