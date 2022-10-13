package com.edocyun.timchat.mvp.entity

class VideoMsgBody : FileMsgBody() {
    //视频消息长度
    var duration: Long = 0
    //高度
    var height = 0
    //宽度
    var width = 0
}