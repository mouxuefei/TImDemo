package com.edocyun.timchat

import android.content.Context
import android.text.TextUtils
import com.tencent.imsdk.v2.V2TIMUserFullInfo
import java.io.File

/**
 * 公共配置，如文件路径配置、用户信息
 */
object TUIConfig {
    @JvmStatic
    var appContext: Context? = null
        private set
    private var appDir = ""
    private var selfFaceUrl = ""
    private var selfNickName = ""
    private var selfAllowType = 0
    private var selfBirthDay = 0L
    private var selfSignature = ""
    private var gender = 0
    const val TUICORE_SETTINGS_SP_NAME = "TUICoreSettings"
    private const val RECORD_DIR_SUFFIX = "/record/"
    private const val RECORD_DOWNLOAD_DIR_SUFFIX = "/record/download/"
    private const val VIDEO_DOWNLOAD_DIR_SUFFIX = "/video/download/"
    private const val IMAGE_BASE_DIR_SUFFIX = "/image/"
    private const val IMAGE_DOWNLOAD_DIR_SUFFIX = "/image/download/"
    private const val MEDIA_DIR_SUFFIX = "/media/"
    private const val FILE_DOWNLOAD_DIR_SUFFIX = "/file/download/"
    private const val CRASH_LOG_DIR_SUFFIX = "/crash/"
    private var initialized = false
    fun init(context: Context?) {
        if (initialized) {
            return
        }
        appContext = context
        initPath()
        initialized = true
    }

    private var defaultAppDir: String
        get() {
            if (TextUtils.isEmpty(appDir)) {
                var context: Context? = null
                if (appContext != null) {
                    context = appContext
                }
                if (context != null) {
                    appDir = context.filesDir.absolutePath
                }
            }
            return appDir
        }
        set(appDir) {
            TUIConfig.appDir = appDir
        }
    private val recordDir: String
        get() = defaultAppDir + RECORD_DIR_SUFFIX
    private val recordDownloadDir: String
        get() = defaultAppDir + RECORD_DOWNLOAD_DIR_SUFFIX
    private val videoDownloadDir: String
        get() = defaultAppDir + VIDEO_DOWNLOAD_DIR_SUFFIX
    val imageBaseDir: String
        get() = defaultAppDir + IMAGE_BASE_DIR_SUFFIX
    @JvmStatic
    val imageDownloadDir: String
        get() = defaultAppDir + IMAGE_DOWNLOAD_DIR_SUFFIX
    @JvmStatic
    val mediaDir: String
        get() = defaultAppDir + MEDIA_DIR_SUFFIX
    val fileDownloadDir: String
        get() = defaultAppDir + FILE_DOWNLOAD_DIR_SUFFIX
    val crashLogDir: String
        get() = defaultAppDir + CRASH_LOG_DIR_SUFFIX

    /**
     * 设置登录用户信息
     */
    fun setSelfInfo(userFullInfo: V2TIMUserFullInfo) {
        selfFaceUrl = userFullInfo.faceUrl
        selfNickName = userFullInfo.nickName
        selfAllowType = userFullInfo.allowType
        selfBirthDay = userFullInfo.birthday
        selfSignature = userFullInfo.selfSignature
        gender = userFullInfo.gender
    }

    /**
     * 清除登录用户信息
     */
    fun clearSelfInfo() {
        selfFaceUrl = ""
        selfNickName = ""
        selfAllowType = 0
        selfBirthDay = 0L
        selfSignature = ""
    }

    /**
     * 初始化文件目录
     */
    private fun initPath() {
        var f = File(mediaDir)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(recordDir)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(recordDownloadDir)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(videoDownloadDir)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(imageDownloadDir)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(fileDownloadDir)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(crashLogDir)
        if (!f.exists()) {
            f.mkdirs()
        }
    }
}