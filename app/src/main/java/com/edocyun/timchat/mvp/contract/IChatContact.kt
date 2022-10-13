package com.edocyun.timchat.mvp.contract

import com.edocyun.timchat.base.IPresenter
import com.edocyun.timchat.base.IView
import com.edocyun.timchat.mvp.entity.Message
import com.luck.picture.lib.entity.LocalMedia

/**
 * @FileName: IMainContact.java
 * @author: villa_mou
 * @date: 06-16:34
 * @version V1.0 <描述当前版本功能>
 * @desc
 */
interface IChatContact {
    interface View : IView<Presenter> {

        fun updateMessage(message: Message)

        fun fetchDataSuccess(isLoadMore:Boolean,data: MutableList<Message>)

        fun fetchDataError()
    }

    interface Presenter : IPresenter<View> {
        fun fetchData(isLoadMore:Boolean)

        fun sendTextMsg(text: String)

        fun sendImageMessage(media: LocalMedia)

        fun sendVideoMessage(media: LocalMedia)

        fun sendFileMessage( path: String)

        fun sendAudioMessage(path: String, time: Int)
    }
}