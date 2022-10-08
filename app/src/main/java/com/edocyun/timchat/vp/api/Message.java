package com.edocyun.timchat.vp.api;


public class Message {
    private String uuid;
    private String msgId;
    private MsgType msgType;
    private MsgBody body;
    private MsgSendStatus sentStatus;
    private long sentTime;
    private String userId;

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    private boolean showTime;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public MsgBody getBody() {
        return body;
    }

    public void setBody(MsgBody body) {
        this.body = body;
    }


    public MsgSendStatus getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(MsgSendStatus sentStatus) {
        this.sentStatus = sentStatus;
    }


    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
