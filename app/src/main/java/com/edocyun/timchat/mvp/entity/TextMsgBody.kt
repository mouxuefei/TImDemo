package com.edocyun.timchat.mvp.entity

class TextMsgBody : MsgBody {
    var message: String? = null
    var extra: String? = null
    constructor() {}

    constructor(message: String?) {
        this.message = message
    }

    override fun toString(): String {
        return "TextMsgBody{" +
                "message='" + message + '\'' +
                ", extra='" + extra + '\'' +
                '}'
    }
}