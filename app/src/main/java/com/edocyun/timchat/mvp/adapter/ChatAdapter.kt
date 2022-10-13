package com.edocyun.timchat.mvp.adapter

import android.content.Context
import android.text.TextUtils
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
import java.io.File

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
     *
     * @param helper
     * @param item
     */
    private fun setStatus(helper: BaseViewHolder, item: Message) {
        val msgContent = item.body
        if (msgContent is TextMsgBody
            || msgContent is AudioMsgBody || msgContent is VideoMsgBody || msgContent is FileMsgBody
        ) {
            //只需要设置自己发送的状态
            val sentStatus = item.sentStatus
            val isSend = item.userId == Constants.myId
            if (isSend) {
                when {
                    sentStatus === MsgSendStatus.SENDING -> {
                        helper.setVisible(R.id.chat_item_progress, true)
                            .setVisible(R.id.chat_item_fail, false)
                    }
                    sentStatus === MsgSendStatus.FAILED -> {
                        helper.setVisible(R.id.chat_item_progress, false)
                            .setVisible(R.id.chat_item_fail, true)
                    }
                    sentStatus === MsgSendStatus.SENT -> {
                        helper.setVisible(R.id.chat_item_progress, false)
                            .setVisible(R.id.chat_item_fail, false)
                    }
                }
            }
        } else if (msgContent is ImageMsgBody) {
            val isSend = item.userId == Constants.myId
            if (isSend) {
                val sentStatus = item.sentStatus
                when {
                    sentStatus === MsgSendStatus.SENDING -> {
                        helper.setVisible(R.id.chat_item_progress, false)
                            .setVisible(R.id.chat_item_fail, false)
                    }
                    sentStatus === MsgSendStatus.FAILED -> {
                        helper.setVisible(R.id.chat_item_progress, false)
                            .setVisible(R.id.chat_item_fail, true)
                    }
                    sentStatus === MsgSendStatus.SENT -> {
                        helper.setVisible(R.id.chat_item_progress, false)
                            .setVisible(R.id.chat_item_fail, false)
                    }
                }
            } else {
            }
        }
    }

    /**
     * 内容
     */
    private fun setContent(helper: BaseViewHolder, item: Message) {
        if (item.msgType == MsgType.TEXT) {
            val msgBody = item.body as TextMsgBody?
            helper.setText(R.id.chat_item_content_text, msgBody?.message)
        } else if (item.msgType == MsgType.IMAGE) {
            val msgBody = item.body as ImageMsgBody?
            if (TextUtils.isEmpty(msgBody?.thumbPath)) {
                GlideUtils.loadChatImage(
                    context,
                    msgBody?.thumbUrl,
                    helper.getView<View>(R.id.bivPic) as ImageView
                )
            } else {
                val file = File(msgBody?.thumbPath)
                if (file.exists()) {
                    GlideUtils.loadChatImage(
                        context,
                        msgBody?.thumbPath,
                        helper.getView<View>(R.id.bivPic) as ImageView
                    )
                } else {
                    GlideUtils.loadChatImage(
                        context,
                        msgBody?.thumbUrl,
                        helper.getView<View>(R.id.bivPic) as ImageView
                    )
                }
            }
        } else if (item.msgType == MsgType.VIDEO) {
            val msgBody = item.body as VideoMsgBody?
            val file = File(msgBody?.extra)
            if (file.exists()) {
                GlideUtils.loadChatImage(
                    context,
                    msgBody?.extra,
                    helper.getView<View>(R.id.bivPic) as ImageView
                )
            } else {
                GlideUtils.loadChatImage(
                    context,
                    msgBody?.extra,
                    helper.getView<View>(R.id.bivPic) as ImageView
                )
            }
        } else if (item.msgType == MsgType.FILE) {
            val msgBody = item.body as FileMsgBody?
            helper.setText(R.id.msg_tv_file_name, msgBody?.displayName)
            helper.setText(R.id.msg_tv_file_size, msgBody?.size.toString() + "B")
        } else if (item.msgType == MsgType.AUDIO) {
            val msgBody = item.body as AudioMsgBody?
            helper.setText(R.id.tvDuration, msgBody?.duration.toString() + "\"")
        }
    }

}