package com.edocyun.timchat.mvp.presenter

import android.media.MediaMetadataRetriever
import android.net.Uri
import com.edocyun.timchat.network.entity.BaseEntity
import com.edocyun.timchat.network.entity.BaseListEntity
import com.edocyun.timchat.network.fetch
import com.edocyun.timchat.base.BasePresenterKt
import com.edocyun.timchat.constants.Constants
import com.edocyun.timchat.util.DateTimeUtil
import com.edocyun.timchat.util.FileUtil
import com.edocyun.timchat.util.LogUtil
import com.edocyun.timchat.mvp.api.*
import com.edocyun.timchat.mvp.contract.IChatContact
import com.edocyun.timchat.mvp.entity.*
import com.edocyun.timchat.mvp.mock.getMockMessageList
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*


/**
 * @FileName: MainPresenter.java
 * @author: villa_mou
 * @date: 06-16:35
 * @version V1.0 <描述当前版本功能>
 * @desc
 */

class ChatPresenter : BasePresenterKt<IChatContact.View>(), IChatContact.Presenter {
    private var currentCacheTime = 0L
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
            if (!isLoadMore) {
                bindLoading(mView, "正在请求...")
            }
            bindDisPool(this@ChatPresenter)
            onSuccess {
                val msgList = getMockMessageList()
                mView?.fetchDataSuccess(isLoadMore, msgList)
            }
            onError { it ->
                mView?.fetchDataError()
                mView?.showToast(it.message ?: "请求失败")
                //TODO:
                getHistoryList(isLoadMore)
            }
        }
    }

    private fun getHistoryList(isLoadMore: Boolean) {
        val msgList = getMockMessageList()
        val newMsgList = msgList.map { msg ->
            msg.isShowTime = checkMsgShowDate(msg.sentTime)
            msg
        }
        mView?.fetchDataSuccess(isLoadMore, newMsgList as MutableList<Message>)
    }

    override fun sendTextMsg(text: String) {
        val message = getBaseSendMessage(MsgType.TEXT)
        val mTextMsgBody = TextMsgBody()
        mTextMsgBody.message = text
        message.body = mTextMsgBody
        mView?.updateMessage(message)
    }

    override fun sendImageMessage(media: LocalMedia) {
        val message = getBaseSendMessage(MsgType.IMAGE)
        val mImageMsgBody = ImageMsgBody()
        mImageMsgBody.thumbUrl = media.compressPath
        message.body = mImageMsgBody
        mView?.updateMessage(message)
    }

    override fun sendVideoMessage(media: LocalMedia) {
        val message = getBaseSendMessage(MsgType.VIDEO)
        val path = media.path
        val videoPath = FileUtil.getPathFromUri(Uri.parse(path))
        val mediaMetadataRetriever = MediaMetadataRetriever()
        try {
            mediaMetadataRetriever.setDataSource(videoPath);
            val sDuration =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) //时长(毫秒)
            val bitmap =
                mediaMetadataRetriever.getFrameAtTime(
                    0,
                    MediaMetadataRetriever.OPTION_NEXT_SYNC
                ) //缩略图
            val imgWidth = bitmap?.width
            val imgHeight = bitmap?.height
            if (bitmap == null) {
                LogUtil.e("buildVideoMessage() bitmap is null")
                return
            }
            val duration = java.lang.Long.valueOf(sDuration)
            val imgPath: String = FileUtil.saveBitmap( bitmap)
            val mImageMsgBody = VideoMsgBody()
            mImageMsgBody.extra = imgPath
            message.body = mImageMsgBody
            mView?.updateMessage(message)
        } catch (e: Exception) {
            LogUtil.d("视频缩略图路径获取失败：$e")
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever.release()
        }
    }

    override fun sendFileMessage(path: String) {
        val message = getBaseSendMessage(MsgType.FILE)
        val mFileMsgBody = FileMsgBody()
        mFileMsgBody.localPath = path
        mFileMsgBody.displayName =
            mView?.getCtx()?.let { FileUtil.getFileName(it, Uri.parse(path)) }
        mFileMsgBody.size = FileUtil.getFileLength(path)
        message.body = mFileMsgBody
        mView?.updateMessage(message)
    }

    override fun sendAudioMessage(path: String, time: Int) {
        val message: Message = getBaseSendMessage(
            MsgType.AUDIO
        )
        val mFileMsgBody = AudioMsgBody()
        mFileMsgBody.localPath = path
        mFileMsgBody.duration = time.toLong()
        message.body = mFileMsgBody
        mView?.updateMessage(message)
    }

    private fun getBaseSendMessage(msgType: MsgType): Message {
        val message = Message()
        message.uuid = UUID.randomUUID().toString() + ""
        message.userId = Constants.myId
        message.sentTime = System.currentTimeMillis() / 1000
        message.sentStatus = MsgSendStatus.SENDING
        message.msgType = msgType
        message.isShowTime = checkMsgShowDate(message.sentTime)
        return message
    }

    private fun checkMsgShowDate(thisTimestamp: Long): Boolean {
        val overTimeResult = DateTimeUtil.checkOver5Minutes(
            currentCacheTime,
            thisTimestamp
        )
        var finalResult = overTimeResult
        if (overTimeResult) {
            currentCacheTime = thisTimestamp
        }
        return finalResult
    }
}

