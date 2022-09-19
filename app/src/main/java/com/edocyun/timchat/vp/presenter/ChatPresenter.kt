package com.edocyun.timchat.vp.presenter

import com.edocyun.timchat.network.entity.BaseEntity
import com.edocyun.timchat.network.entity.BaseListEntity
import com.edocyun.timchat.network.fetch
import com.edocyun.timchat.base.BasePresenterKt
import com.edocyun.timchat.vp.api.ChatMessageDTO
import com.edocyun.timchat.vp.api.ChatRetrofit
import com.edocyun.timchat.vp.api.MessageEntity
import com.edocyun.timchat.vp.contract.IChatContact
import com.orhanobut.logger.Logger


/**
 * @FileName: MainPresenter.java
 * @author: villa_mou
 * @date: 06-16:35
 * @version V1.0 <描述当前版本功能>
 * @desc
 */

class ChatPresenter : BasePresenterKt<IChatContact.View>(), IChatContact.Presenter {
    private val pageSize = 20
    private var pageIndex = 1
    override fun fetchData(isLoadMore: Boolean) {
        if (isLoadMore) {
            pageIndex += 1
        }
        val dto = ChatMessageDTO(pageSize, pageIndex)
        val api = ChatRetrofit.apiService.messages(dto)
        fetch<BaseEntity<BaseListEntity<MessageEntity>>> {
            api { api }
            bindLoading(mView, "正在请求...")
            bindDisPool(this@ChatPresenter)
            onSuccess {
                mView?.getMessageResponse(it.data?.records)
            }
            onError {
                Logger.e("onError-------")
                mView?.showToast(it.message ?: "请求失败")
            }
        }
    }
}

