package com.edocyun.timchat.mvp.entity

class Message {
    var uuid: String? = null
    var msgId: String? = null
    var msgType: MsgType? = null
    var body: MsgBody? = null
    var sentStatus: MsgSendStatus? = null
    var sentTime: Long = 0
    var userId: String? = null
    var isShowTime = false
}