package com.edocyun.timchat.mvp.mock

import com.edocyun.timchat.constants.Constants
import com.edocyun.timchat.mvp.entity.*
import java.util.*

/**
 * @FileName: mock.java
 * @author: villa_mou
 * @date: 09-10:10
 * @version V1.0 <描述当前版本功能>
 * @desc
 */

fun getMockMessageList(): MutableList<Message> {
    //下拉刷新模拟获取历史消息
    val mReceiveMsgList = mutableListOf<Message>()
    for (i in 1..20) {
        when (i % 3) {
            0 -> {
                //构建文本消息
                val mMessgaeText = getBaseReceiveMessage(MsgType.TEXT)
                val mTextMsgBody = TextMsgBody()
                mTextMsgBody.message = "收到的消息"
                mMessgaeText.body = mTextMsgBody
                mMessgaeText.sentStatus = MsgSendStatus.FAILED
                mReceiveMsgList.add(mMessgaeText)
            }
            1 -> {
                //构建图片消息
                val mMessgaeImage = getBaseReceiveMessage(MsgType.IMAGE)
                val mImageMsgBody = ImageMsgBody()
                mImageMsgBody.thumbUrl =
                    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Flmg.jj20.com%2Fup%2Fallimg%2F1114%2F033021091503%2F210330091503-6-1200.jpg&refer=http%3A%2F%2Flmg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1668740123&t=b19af77815638b230ab07e8c2b77bc3a"
                mMessgaeImage?.body = mImageMsgBody
                mMessgaeImage.sentStatus = MsgSendStatus.FAILED
                mReceiveMsgList.add(mMessgaeImage)
            }
            2 -> {
                //构建文件消息
                val mMessgaeFile = getBaseReceiveMessage(MsgType.FILE)
                val mFileMsgBody = FileMsgBody()
                mFileMsgBody.displayName = "收到的文件"
                mFileMsgBody.size = 12
                mMessgaeFile.body = mFileMsgBody
                mMessgaeFile.sentStatus = MsgSendStatus.FAILED
                mReceiveMsgList.add(mMessgaeFile)
            }
        }
    }

    return mReceiveMsgList

}

private fun getBaseReceiveMessage(msgType: MsgType): Message {
    val message = Message()
    message.uuid = UUID.randomUUID().toString() + ""
    message.userId = Constants.otherId
    message.sentTime = System.currentTimeMillis() / 1000
    message.sentStatus = MsgSendStatus.SENDING
    message.msgType = msgType
    return message
}
