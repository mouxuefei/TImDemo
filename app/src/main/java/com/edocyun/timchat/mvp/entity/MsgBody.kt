package com.edocyun.timchat.mvp.entity

import java.io.Serializable

open class MsgBody : Serializable {
    var localMsgType: MsgType? = null
}