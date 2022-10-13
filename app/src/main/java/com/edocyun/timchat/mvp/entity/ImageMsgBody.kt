package com.edocyun.timchat.mvp.entity

class ImageMsgBody : FileMsgBody {
    //缩略图文件的本地路径
    var thumbPath: String? = null

    //缩略图远程地址
    var thumbUrl: String? = null

    //是否压缩(false:原图，true：压缩过)
    var isCompress = false

    //高度
    var height = 0

    //宽度
    var width = 0

    constructor() {}

    /**
     * 生成ImageMessage对象。
     * @param thumbPath  缩略图地址。
     * @param localPath 大图地址。
     * @param compress 是否发送原图。
     * @return ImageMessage对象实例。
     */
    constructor(thumbPath: String?, localPath: String?, compress: Boolean) {
        this.thumbPath = thumbPath
        thumbUrl = localPath
        isCompress = compress
    }

    override fun toString(): String {
        return "ImageMsgBody{" +
                "thumbPath='" + thumbPath + '\'' +
                ", thumbUrl='" + thumbUrl + '\'' +
                ", compress=" + isCompress +
                ", height=" + height +
                ", width=" + width +
                '}'
    }

    companion object {
        fun obtain(thumUri: String, localUri: String, isFull: Boolean): ImageMsgBody {
            return ImageMsgBody(thumUri, localUri, isFull)
        }
    }
}