package com.edocyun.timchat.vp.mock

import com.edocyun.timchat.constants.Constants
import com.edocyun.timchat.vp.api.*
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
                mReceiveMsgList.add(mMessgaeText)
            }
            1 -> {
                //构建图片消息
                val mMessgaeImage = getBaseReceiveMessage(MsgType.IMAGE)
                val mImageMsgBody = ImageMsgBody()
                mImageMsgBody.thumbUrl =
                    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2F4k%2Fs%2F01%2F210924132020A05-0-lp.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1664517206&t=e7cc1c3246afac219b23950f73f66ba4"
                mMessgaeImage?.body = mImageMsgBody
                mReceiveMsgList.add(mMessgaeImage)
            }
            2 -> {
                //构建文件消息
                val mMessgaeFile = getBaseReceiveMessage(MsgType.FILE)
                val mFileMsgBody = FileMsgBody()
                mFileMsgBody.displayName = "收到的文件"
                mFileMsgBody.size = 12
                mMessgaeFile.body = mFileMsgBody
                mReceiveMsgList.add(mMessgaeFile)
            }
        }
    }

    return mReceiveMsgList

}

private fun getBaseReceiveMessage(msgType: MsgType): Message {
    val mMessgae = Message()
    mMessgae.uuid = UUID.randomUUID().toString() + ""
    mMessgae.userId = Constants.otherId
    mMessgae.sentTime = System.currentTimeMillis()
    mMessgae.sentStatus = MsgSendStatus.SENDING
    mMessgae.msgType = msgType
    return mMessgae
}
