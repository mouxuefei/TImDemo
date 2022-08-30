package com.edocyun.timchat.vp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.os.Handler
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.edocyun.timchat.R
import com.edocyun.timchat.base.mvp.BaseMvpActivity
import com.edocyun.timchat.constants.Constants.*
import com.edocyun.timchat.helper.ChatUiHelper
import com.edocyun.timchat.util.FileUtils
import com.edocyun.timchat.util.LogUtil
import com.edocyun.timchat.util.PictureFileUtil
import com.edocyun.timchat.vp.adapter.ChatAdapter
import com.edocyun.timchat.vp.api.*
import com.edocyun.timchat.vp.contract.IChatContact
import com.edocyun.timchat.vp.presenter.ChatPresenter
import com.edocyun.timchat.widget.MediaManager
import com.edocyun.timchat.widget.RecordButton
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.include_add_layout.*
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ChatActivity : BaseMvpActivity<IChatContact.View, IChatContact.Presenter>(),
    IChatContact.View, SwipeRefreshLayout.OnRefreshListener {

    private var mAdapter: ChatAdapter? = null

    private var ivAudio: ImageView? = null

    override fun getContentView() = R.layout.activity_chat
    override fun initView() {
        initRv()
        initListener()
        initChatUi()
    }

    private fun initListener() {
        btn_send.setOnClickListener {
            sendTextMsg(et_content.text.toString())
            et_content.setText("")
        }
        rlPhoto.setOnClickListener {
            PictureFileUtil.openGalleryPic(this@ChatActivity, REQUEST_CODE_IMAGE)
        }
        rlVideo.setOnClickListener {
            PictureFileUtil.openGalleryAudio(this@ChatActivity, REQUEST_CODE_VEDIO)
        }
        rlFile.setOnClickListener {
            PictureFileUtil.openFile(this@ChatActivity, REQUEST_CODE_FILE)
        }
        rlLocation.setOnClickListener { }
    }

    private fun initRv() {
        mAdapter = ChatAdapter(this, ArrayList())
        val mLinearLayout = LinearLayoutManager(this)
        rv_chat_list.layoutManager = mLinearLayout
        rv_chat_list.adapter = mAdapter
        swipe_chat.setOnRefreshListener(this)
        mAdapter?.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
                val isSend = mAdapter?.getItem(position)?.senderId == mSenderId
                if (ivAudio != null) {
                    if (isSend) {
                        ivAudio?.setBackgroundResource(R.mipmap.audio_animation_list_right_3)
                    } else {
                        ivAudio?.setBackgroundResource(R.mipmap.audio_animation_list_left_3)
                    }
                    ivAudio = null
                    MediaManager.reset()
                } else {
                    ivAudio = view.findViewById<ImageView>(R.id.ivAudio)
                    MediaManager.reset()
                    if (isSend) {
                        ivAudio?.setBackgroundResource(R.drawable.audio_animation_right_list)
                    } else {
                        ivAudio?.setBackgroundResource(R.drawable.audio_animation_left_list)
                    }
                    val drawable = ivAudio?.background as AnimationDrawable
                    drawable.start()
                    mAdapter?.let {
                        MediaManager.playSound(
                            this@ChatActivity, (it.data[position].body as AudioMsgBody).localPath
                        ) {
                            if (isSend) {
                                ivAudio?.setBackgroundResource(R.mipmap.audio_animation_list_right_3)
                            } else {
                                ivAudio?.setBackgroundResource(R.mipmap.audio_animation_list_left_3)
                            }
                            MediaManager.release()
                        }
                    }

                }
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initChatUi() {
        //mBtnAudio
        val mUiHelper = ChatUiHelper.with(this)
        mUiHelper.bindContentLayout(llContent)
            .bindttToSendButton(btn_send)
            .bindEditText(et_content)
            .bindBottomLayout(bottom_layout)
            .bindEmojiLayout(rlEmotion as LinearLayout?)
            .bindAddLayout(llAdd as LinearLayout?)
            .bindToAddButton(ivAdd)
            .bindToEmojiButton(ivEmo)
            .bindAudioBtn(btnAudio)
            .bindAudioIv(iv_Audio)
//            .bindEmojiData()
        //底部布局弹出,聊天列表上滑
        rv_chat_list.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                rv_chat_list.post(Runnable {
                    mAdapter?.let {
                        if (it.itemCount > 0) {
                            rv_chat_list.smoothScrollToPosition(it.itemCount - 1)
                        }
                    }

                })
            }
        })
        //点击空白区域关闭键盘
        rv_chat_list.setOnTouchListener(OnTouchListener { _, _ ->
            mUiHelper.hideBottomLayout(false)
            mUiHelper.hideSoftInput()
            et_content.clearFocus()
            ivEmo.setImageResource(R.mipmap.ic_emoji)
            false
        })
        //
        (btnAudio as RecordButton).setOnFinishedRecordListener { audioPath, time ->
            LogUtil.d("录音结束回调")
            val file = File(audioPath)
            if (file.exists()) {
                sendAudioMessage(audioPath, time)
            }
        }
    }


    //文本消息
    private fun sendTextMsg(hello: String) {
        val mMessgae = getBaseSendMessage(MsgType.TEXT)
        val mTextMsgBody = TextMsgBody()
        mTextMsgBody.message = hello
        mMessgae.body = mTextMsgBody
        //开始发送
        mAdapter?.addData(mMessgae)
        //模拟两秒后发送成功
        updateMsg(mMessgae)
    }


    //图片消息
    private fun sendImageMessage(media: LocalMedia) {
        val mMessgae = getBaseSendMessage(MsgType.IMAGE)
        val mImageMsgBody = ImageMsgBody()
        mImageMsgBody.thumbUrl = media.compressPath
        mMessgae.body = mImageMsgBody
        //开始发送
        mAdapter?.addData(mMessgae)
        //模拟两秒后发送成功
        updateMsg(mMessgae)
    }


    //视频消息
    private fun sendVedioMessage(media: LocalMedia) {
        val mMessgae = getBaseSendMessage(MsgType.VIDEO)
        //生成缩略图路径
        val vedioPath = media.path
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(vedioPath)
        val bitmap = mediaMetadataRetriever.frameAtTime
        val imgname = System.currentTimeMillis().toString() + ".jpg"
        val urlpath = Environment.getExternalStorageDirectory().toString() + "/" + imgname
        val f = File(urlpath)
        try {
            if (f.exists()) {
                f.delete()
            }
            val out = FileOutputStream(f)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            LogUtil.d("视频缩略图路径获取失败：$e")
            e.printStackTrace()
        }
        val mImageMsgBody = VideoMsgBody()
        mImageMsgBody.extra = urlpath
        mMessgae.body = mImageMsgBody
        //开始发送
        mAdapter?.addData(mMessgae)
        //模拟两秒后发送成功
        updateMsg(mMessgae)
    }

    //文件消息
    private fun sendFileMessage(from: String, to: String, path: String) {
        val mMessgae = getBaseSendMessage(MsgType.FILE)
        val mFileMsgBody = FileMsgBody()
        mFileMsgBody.localPath = path
        mFileMsgBody.displayName = FileUtils.getFileName(path)
        mFileMsgBody.size = FileUtils.getFileLength(path)
        mMessgae.body = mFileMsgBody
        //开始发送
        mAdapter?.addData(mMessgae)
        //模拟两秒后发送成功
        updateMsg(mMessgae)
    }

    //语音消息
    private fun sendAudioMessage(path: String, time: Int) {
        val mMessgae: Message = getBaseSendMessage(
            MsgType.AUDIO
        )
        val mFileMsgBody = AudioMsgBody()
        mFileMsgBody.localPath = path
        mFileMsgBody.duration = time.toLong()
        mMessgae.body = mFileMsgBody
        //开始发送
        mAdapter?.addData(mMessgae)
        //模拟两秒后发送成功
        updateMsg(mMessgae)
    }

    private fun getBaseSendMessage(msgType: MsgType): Message {
        val mMessgae = Message()
        mMessgae.uuid = UUID.randomUUID().toString() + ""
        mMessgae.senderId = mSenderId
        mMessgae.targetId = mTargetId
        mMessgae.sentTime = System.currentTimeMillis()
        mMessgae.sentStatus = MsgSendStatus.SENDING
        mMessgae.msgType = msgType
        return mMessgae
    }


    private fun getBaseReceiveMessage(msgType: MsgType): Message? {
        val mMessgae = Message()
        mMessgae.uuid = UUID.randomUUID().toString() + ""
        mMessgae.senderId = mTargetId
        mMessgae.targetId = mSenderId
        mMessgae.sentTime = System.currentTimeMillis()
        mMessgae.sentStatus = MsgSendStatus.SENDING
        mMessgae.msgType = msgType
        return mMessgae
    }

    private fun updateMsg(mMessgae: Message) {
        mAdapter?.let {
            rv_chat_list.scrollToPosition(it.itemCount - 1)
            //模拟2秒后发送成功
            Handler().postDelayed({
                var position = 0
                mMessgae.sentStatus = MsgSendStatus.SENT
                //更新单个子条目
                for (i in it.data.indices) {
                    val mAdapterMessage = it.data[i]
                    if (mMessgae.uuid == mAdapterMessage.uuid) {
                        position = i
                    }
                }
                it.notifyItemChanged(position)
            }, 2000)
        }
    }


    override fun getMessageResponse(data: ArrayList<MessageEntity>?) {

    }

    override var mPresenter: IChatContact.Presenter = ChatPresenter()

    override fun onRefresh() {
        //下拉刷新模拟获取历史消息
        val mReceiveMsgList: MutableList<Message?> = ArrayList()
        //构建文本消息
        //构建文本消息
        val mMessgaeText = getBaseReceiveMessage(MsgType.TEXT)
        val mTextMsgBody = TextMsgBody()
        mTextMsgBody.message = "收到的消息"
        mMessgaeText?.body = mTextMsgBody
        mReceiveMsgList.add(mMessgaeText)
        //构建图片消息
        //构建图片消息
        val mMessgaeImage = getBaseReceiveMessage(MsgType.IMAGE)
        val mImageMsgBody = ImageMsgBody()
        mImageMsgBody.thumbUrl =
            "https://c-ssl.duitang.com/uploads/item/201208/30/20120830173930_PBfJE.thumb.700_0.jpeg"
        mMessgaeImage!!.body = mImageMsgBody
        mReceiveMsgList.add(mMessgaeImage)
        //构建文件消息
        //构建文件消息
        val mMessgaeFile = getBaseReceiveMessage(MsgType.FILE)
        val mFileMsgBody = FileMsgBody()
        mFileMsgBody.displayName = "收到的文件"
        mFileMsgBody.size = 12
        mMessgaeFile!!.body = mFileMsgBody
        mReceiveMsgList.add(mMessgaeFile)
        mAdapter?.addData(0, mReceiveMsgList)
        swipe_chat.isRefreshing = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_FILE -> {
                }
                REQUEST_CODE_IMAGE -> {
                    // 图片选择结果回调
                    val selectListPic = PictureSelector.obtainMultipleResult(data)
                    for (media in selectListPic) {
                        LogUtil.d("获取图片路径成功:" + media.path)
                        sendImageMessage(media)
                    }
                }
                REQUEST_CODE_VEDIO -> {
                    // 视频选择结果回调
                    val selectListVideo = PictureSelector.obtainMultipleResult(data)
                    for (media in selectListVideo) {
                        LogUtil.d("获取视频路径成功:" + media.path)
                        sendVedioMessage(media)
                    }
                }
            }
        }
    }


}
