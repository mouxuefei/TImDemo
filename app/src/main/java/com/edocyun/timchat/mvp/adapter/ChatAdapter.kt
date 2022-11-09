package com.edocyun.timchat.mvp.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.edocyun.timchat.R
import com.edocyun.timchat.constants.Constants
import com.edocyun.timchat.util.DateTimeUtil
import com.edocyun.timchat.util.GlideUtils
import com.edocyun.timchat.mvp.entity.*

class ChatAdapter(context: Context, data: MutableList<Message>) :
    BaseDelegateMultiAdapter<Message, BaseViewHolder>(data) {

    companion object {
        private const val TYPE_SEND_TEXT = 1
        private const val TYPE_RECEIVE_TEXT = 2
        private const val TYPE_SEND_IMAGE = 3
        private const val TYPE_RECEIVE_IMAGE = 4
        private const val TYPE_SEND_VIDEO = 5
        private const val TYPE_RECEIVE_VIDEO = 6
        private const val TYPE_SEND_FILE = 7
        private const val TYPE_RECEIVE_FILE = 8
        private const val TYPE_SEND_AUDIO = 9
        private const val TYPE_RECEIVE_AUDIO = 10
        private const val SEND_TEXT = R.layout.item_text_send
        private const val RECEIVE_TEXT = R.layout.item_text_receive
        private const val SEND_IMAGE = R.layout.item_image_send
        private const val RECEIVE_IMAGE = R.layout.item_image_receive
        private const val SEND_VIDEO = R.layout.item_video_send
        private const val RECEIVE_VIDEO = R.layout.item_video_receive
        private const val SEND_FILE = R.layout.item_file_send
        private const val RECEIVE_FILE = R.layout.item_file_receive
        private const val RECEIVE_AUDIO = R.layout.item_audio_receive
        private const val SEND_AUDIO = R.layout.item_audio_send
    }

    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<Message>() {
            override fun getItemType(data: List<Message>, position: Int): Int {
                val entity = data[position]
                val isSend = entity.userId == Constants.myId
                when {
                    MsgType.TEXT === entity.msgType -> {
                        return if (isSend) TYPE_SEND_TEXT else TYPE_RECEIVE_TEXT
                    }
                    MsgType.IMAGE === entity.msgType -> {
                        return if (isSend) TYPE_SEND_IMAGE else TYPE_RECEIVE_IMAGE
                    }
                    MsgType.VIDEO === entity.msgType -> {
                        return if (isSend) TYPE_SEND_VIDEO else TYPE_RECEIVE_VIDEO
                    }
                    MsgType.FILE === entity.msgType -> {
                        return if (isSend) TYPE_SEND_FILE else TYPE_RECEIVE_FILE
                    }
                    MsgType.AUDIO === entity.msgType -> {
                        return if (isSend) TYPE_SEND_AUDIO else TYPE_RECEIVE_AUDIO
                    }
                    else -> return 0
                }
            }
        })
        getMultiTypeDelegate()
            ?.addItemType(TYPE_SEND_TEXT, SEND_TEXT)
            ?.addItemType(TYPE_RECEIVE_TEXT, RECEIVE_TEXT)
            ?.addItemType(TYPE_SEND_IMAGE, SEND_IMAGE)
            ?.addItemType(TYPE_RECEIVE_IMAGE, RECEIVE_IMAGE)
            ?.addItemType(TYPE_SEND_VIDEO, SEND_VIDEO)
            ?.addItemType(TYPE_RECEIVE_VIDEO, RECEIVE_VIDEO)
            ?.addItemType(TYPE_SEND_FILE, SEND_FILE)
            ?.addItemType(TYPE_RECEIVE_FILE, RECEIVE_FILE)
            ?.addItemType(TYPE_SEND_AUDIO, SEND_AUDIO)
            ?.addItemType(TYPE_RECEIVE_AUDIO, RECEIVE_AUDIO)
    }

    override fun convert(holder: BaseViewHolder, item: Message) {
        setStatus(holder, item)
        setContent(holder, item)
        setTimeVisible(holder, item)
    }

    /**
     * 时间是否显示
     * @param helper
     * @param item
     */
    private fun setTimeVisible(helper: BaseViewHolder, item: Message) {
        helper.setVisible(R.id.item_tv_time, item.isShowTime)
        if (item.isShowTime) {
            helper.setText(R.id.item_tv_time, DateTimeUtil.getTimeFormatText(item.sentTime))
        }
    }

    /**
     * 发送状态，loading,error
     */
    private fun setStatus(helper: BaseViewHolder, item: Message) {
        val sentStatus = item.sentStatus
        //TODO: 判断用户id是否是当前用户id
        val isSend = item.userId == Constants.myId
        if (isSend) {
            var progressVisible = false
            var failVisible = false
            when {
                sentStatus === MsgSendStatus.SENDING -> progressVisible = true
                sentStatus === MsgSendStatus.FAILED -> failVisible = true
            }
            helper.setVisible(R.id.chat_item_progress, progressVisible)
                .setVisible(R.id.chat_item_fail, failVisible)
        }
    }

    /**
     * 内容
     */
    private fun setContent(helper: BaseViewHolder, item: Message) {
        when (item.msgType) {
            MsgType.TEXT -> {
                val msgBody = item.body as TextMsgBody?
                helper.setText(R.id.chat_item_content_text, msgBody?.message)
            }
            MsgType.IMAGE -> {
                val msgBody = item.body as ImageMsgBody?
                val url = if (msgBody?.thumbPath != null) msgBody.thumbPath else msgBody?.thumbUrl
                GlideUtils.loadChatImage(
                    context,
                    url,
                    helper.getView<View>(R.id.bivPic) as ImageView
                )
            }
            MsgType.VIDEO -> {
                val msgBody = item.body as VideoMsgBody?
                GlideUtils.loadChatImage(
                    context,
                    msgBody?.extra,
                    helper.getView<View>(R.id.bivPic) as ImageView
                )
            }
            MsgType.FILE -> {
                val msgBody = item.body as FileMsgBody?
                helper.setText(R.id.msg_tv_file_name, msgBody?.displayName)
                helper.setText(R.id.msg_tv_file_size, msgBody?.size.toString() + "B")
            }
            MsgType.AUDIO -> {
                val msgBody = item.body as AudioMsgBody?
                helper.setText(R.id.tvDuration, msgBody?.duration.toString() + "\"")
            }
            else -> {
            }
        }
    }

}