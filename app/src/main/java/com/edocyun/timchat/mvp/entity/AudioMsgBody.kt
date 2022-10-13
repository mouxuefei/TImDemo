package com.edocyun.timchat.mvp.entity

class AudioMsgBody : FileMsgBody() {
    //语音消息长度,单位：秒。
    var duration: Long = 0
}