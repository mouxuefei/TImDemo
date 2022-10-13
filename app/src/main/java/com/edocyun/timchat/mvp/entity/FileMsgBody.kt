package com.edocyun.timchat.mvp.entity


open class FileMsgBody : MsgBody() {
    //文件后缀名
    var ext: String? = null

    //文件名称,可以和文件名不同，仅用于界面展示
    var displayName: String? = null

    //文件长度(字节)
    var size: Long = 0

    //本地文件保存路径
    var localPath: String? = null

    //文件下载地址
    var remoteUrl: String? = null

    //文件内容的MD5
    var md5: String? = null

    //扩展信息，可以放置任意的数据内容，也可以去掉此属性
    var extra: String? = null
}