package com.edocyun.timchat.mvp.entity

enum class MsgSendStatus {
    DEFAULT,  //发送中
    SENDING,  //发送失败
    FAILED,  //已发送
    SENT
}