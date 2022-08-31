package com.edocyun.timchat.vp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.edocyun.timchat.R
import com.edocyun.timchat.base.mvp.BaseMvpActivity
import com.edocyun.timchat.constants.Constants.*
import com.edocyun.timchat.helper.ChatUiHelper
import com.edocyun.timchat.util.*
import com.edocyun.timchat.vp.adapter.ChatAdapter
import com.edocyun.timchat.vp.api.*
import com.edocyun.timchat.vp.contract.IChatContact
import com.edocyun.timchat.vp.presenter.ChatPresenter
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.include_add_layout.*
import java.io.File
import java.util.*


class ChatActivity : BaseMvpActivity<IChatContact.View, IChatContact.Presenter>(),
    IChatContact.View, SwipeRefreshLayout.OnRefreshListener {
    private var mAdapter: ChatAdapter? = null
    private var ivAudio: ImageView? = null
    override fun getContentView() = R.layout.activity_chat
    override var mPresenter: IChatContact.Presenter = ChatPresenter()

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
            PictureFileUtil.openGalleryVideo(this@ChatActivity, REQUEST_CODE_VEDIO)
        }
        rlFile.setOnClickListener {
            PictureFileUtil.openFile(this@ChatActivity, REQUEST_CODE_FILE)
        }

        rlLocation.setOnClickListener { }
    }

    private fun initRv() {
        mAdapter = ChatAdapter(this, ArrayList())
        rv_chat_list.adapter = mAdapter
        swipe_chat.setOnRefreshListener(this)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as Message
            when (item.msgType) {
                MsgType.AUDIO -> {
                    onPressAudio(item, view, position)
                }
                MsgType.IMAGE -> {
                    onPressImage(item)
                }
            }
        }
    }

    /**
     * 点击音频
     */
    private fun onPressAudio(msg: Message, view: View, position: Int) {
        val isSend = msg.userId == myId
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

    /**
     * 点击图片
     */
    private fun onPressImage(msg: Message) {
        LogUtil.e("onPressImage")
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
        //底部布局弹出,聊天列表上滑到最后一位
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

        //录音结束回调
        btnAudio.setOnFinishedRecordListener { audioPath, time ->
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
    private fun sendVideoMessage(media: LocalMedia) {
        val mMessgae = getBaseSendMessage(MsgType.VIDEO)
        val path = media.path
        val videoPath = FileUtil.getPathFromUri(Uri.parse(path))
        val mediaMetadataRetriever = MediaMetadataRetriever()
        try {
            mediaMetadataRetriever.setDataSource(videoPath);
            val sDuration =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) //时长(毫秒)
            val bitmap =
                mediaMetadataRetriever.getFrameAtTime(
                    0,
                    MediaMetadataRetriever.OPTION_NEXT_SYNC
                ) //缩略图
            val imgWidth = bitmap?.width
            val imgHeight = bitmap?.height
            if (bitmap == null) {
                LogUtil.e("buildVideoMessage() bitmap is null")
                return
            }
            val duration = java.lang.Long.valueOf(sDuration)
            val imgPath: String = FileUtil.saveBitmap("JCamera", bitmap)
            val mImageMsgBody = VideoMsgBody()
            mImageMsgBody.extra = imgPath
            mMessgae.body = mImageMsgBody
            //开始发送
            mAdapter?.addData(mMessgae)
            //模拟两秒后发送成功
            updateMsg(mMessgae)
        } catch (e: Exception) {
            LogUtil.d("视频缩略图路径获取失败：$e")
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever.release()
        }

    }

    //文件消息
    private fun sendFileMessage(from: String, to: String, path: String) {
        val mMessgae = getBaseSendMessage(MsgType.FILE)
        val mFileMsgBody = FileMsgBody()
        mFileMsgBody.localPath = path
        mFileMsgBody.displayName = FileUtil.getFileName(this, Uri.parse(path))
        mFileMsgBody.size = FileUtil.getFileLength(path)
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
        mMessgae.userId = myId
        mMessgae.sentTime = System.currentTimeMillis()
        mMessgae.sentStatus = MsgSendStatus.SENDING
        mMessgae.msgType = msgType
        return mMessgae
    }


    private fun getBaseReceiveMessage(msgType: MsgType): Message? {
        val mMessgae = Message()
        mMessgae.uuid = UUID.randomUUID().toString() + ""
        mMessgae.userId = otherId
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
                        sendVideoMessage(media)
                    }
                }
            }
        }
    }


}
