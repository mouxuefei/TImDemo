package com.edocyun.timchat.mvp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.edocyun.timchat.R
import com.edocyun.timchat.base.BaseMvpActivity
import com.edocyun.timchat.constants.Constants.REQUEST_CODE_FILE
import com.edocyun.timchat.constants.Constants.REQUEST_CODE_IMAGE
import com.edocyun.timchat.constants.Constants.REQUEST_CODE_VEDIO
import com.edocyun.timchat.constants.Constants.myId
import com.edocyun.timchat.util.*
import com.edocyun.timchat.mvp.adapter.ChatAdapter
import com.edocyun.timchat.mvp.api.*
import com.edocyun.timchat.mvp.contract.IChatContact
import com.edocyun.timchat.mvp.entity.AudioMsgBody
import com.edocyun.timchat.mvp.entity.Message
import com.edocyun.timchat.mvp.entity.MsgSendStatus
import com.edocyun.timchat.mvp.entity.MsgType
import com.edocyun.timchat.mvp.presenter.ChatPresenter
import com.luck.picture.lib.PictureSelector
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.common_titlebar.*
import kotlinx.android.synthetic.main.include_add_layout.*
import java.io.File
import java.util.*


class ChatActivity : BaseMvpActivity<IChatContact.View, IChatContact.Presenter>(),
    IChatContact.View, SwipeRefreshLayout.OnRefreshListener {
    private var mAdapter: ChatAdapter? = null
    private var mRecommendAdapter: BaseQuickAdapter<String, BaseViewHolder>? = null
    private var ivItemAudio: ImageView? = null
    override fun getContentView() = R.layout.activity_chat
    override var mPresenter: IChatContact.Presenter = ChatPresenter()

    override fun initView() {
        initRv()
        initListener()
        initChatUi()
        initTopBar()
    }

    private fun initTopBar() {
        common_toolbar_back.setOnClickListener {
            finish()
        }
        common_toolbar_title.text = "xxx医生"
    }

    override fun initData() {
        mPresenter.fetchData(false)
    }

    private fun initListener() {
        btnSend.setOnClickListener {
            mPresenter.sendTextMsg(etContent.text.toString())
            etContent.setText("")
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
        rlLocation.setOnClickListener {
            //TODO:位置
        }
    }

    private fun initRv() {
        mAdapter = ChatAdapter(this, ArrayList())
        rvChatList.adapter = mAdapter
        swipeChat.setOnRefreshListener(this)
        mAdapter?.addChildClickViewIds(R.id.chat_item_header, R.id.chat_item_layout_content)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as Message
            when (view.id) {
                //头像
                R.id.chat_item_header -> {

                }
                //内容
                R.id.chat_item_layout_content -> {
                    when (item.msgType) {
                        MsgType.AUDIO -> {
                            onPressAudio(item, view, position)
                        }
                        MsgType.IMAGE -> {
                            onPressImage(item)
                        }
                        else -> {

                        }
                    }
                }
            }
        }

        rvRecommend.adapter = object :
            BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_recommend, null) {
            override fun convert(holder: BaseViewHolder, item: String) {
                holder.setText(R.id.itemRecommendTitle, item)
            }

        }.also { mRecommendAdapter = it }
    }

    /**
     * 点击音频，播放
     */
    private fun onPressAudio(msg: Message, view: View, position: Int) {
        val isSend = msg.userId == myId
        if (ivItemAudio != null) {
            if (isSend) {
                ivItemAudio?.setBackgroundResource(R.mipmap.audio_animation_list_right_3)
            } else {
                ivItemAudio?.setBackgroundResource(R.mipmap.audio_animation_list_left_3)
            }
            ivItemAudio = null
            MediaManager.reset()
        } else {
            ivItemAudio = view.findViewById<ImageView>(R.id.ivAudio)
            MediaManager.reset()
            if (isSend) {
                ivItemAudio?.setBackgroundResource(R.drawable.audio_animation_right_list)
            } else {
                ivItemAudio?.setBackgroundResource(R.drawable.audio_animation_left_list)
            }
            val drawable = ivItemAudio?.background as AnimationDrawable
            drawable.start()
            mAdapter?.let {
                MediaManager.playSound(
                    this@ChatActivity, (it.data[position].body as AudioMsgBody).localPath
                ) {
                    if (isSend) {
                        ivItemAudio?.setBackgroundResource(R.mipmap.audio_animation_list_right_3)
                    } else {
                        ivItemAudio?.setBackgroundResource(R.mipmap.audio_animation_list_left_3)
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
        //TODO: 查看大图
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initChatUi() {
        //mBtnAudio
        val uiHelper = ChatUiHelper.with(this)
        uiHelper.bindContentLayout(llContent)
            .bindttToSendButton(btnSend)
            .bindEditText(etContent)
            .bindBottomLayout(bottomLayout)
            .bindEmojiLayout(rlEmotion as LinearLayout?)
            .bindAddLayout(llAdd as LinearLayout?)
            .bindToAddButton(ivAdd)
            .bindToEmojiButton(ivEmo)
            .bindAudioBtn(btnAudio)
            .bindAudioIv(ivAudioIcon)
//            .bindEmojiData()
        //底部布局弹出,聊天列表上滑到最后一位
        rvChatList.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                rvChatList.post(Runnable {
                    mAdapter?.let {
                        if (it.itemCount > 0) {
                            rvChatList.scrollToPosition(it.itemCount - 1)
                        }
                    }

                })
            }
        })

        //点击空白区域关闭键盘
        rvChatList.setOnTouchListener(OnTouchListener { _, _ ->
            uiHelper.hideBottomLayout(false)
            uiHelper.hideSoftInput()
            etContent.clearFocus()
            ivEmo.setImageResource(R.mipmap.ic_emoji)
            false
        })

        //录音结束回调
        btnAudio.setOnFinishedRecordListener { audioPath, time ->
            val file = File(audioPath)
            if (file.exists()) {
                mPresenter.sendAudioMessage(audioPath, time)
            }
        }
    }

    private fun updateMsg(mMessgae: Message) {
        mAdapter?.let {
            rvChatList.scrollToPosition(it.itemCount - 1)
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


    override fun fetchDataSuccess(isLoadMore: Boolean, data: MutableList<Message>) {
        if (isLoadMore) {
            mAdapter?.addData(0, data)
            swipeChat.isRefreshing = false
        } else {
            mAdapter?.setNewInstance(data)
            mRecommendAdapter?.setNewInstance(mutableListOf("热门话题", "张三李四自己", "ssssss"))
        }
    }

    override fun fetchDataError() {
        swipeChat.isRefreshing = false
    }

    override fun updateMessage(message: Message) {
        //开始发送
        mAdapter?.addData(message)
        //模拟两秒后发送成功
        updateMsg(message)
    }

    override fun onRefresh() {
        mPresenter.fetchData(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_FILE -> {
                    //TODO:
                }
                REQUEST_CODE_IMAGE -> {
                    // 图片选择结果回调
                    val selectListPic = PictureSelector.obtainMultipleResult(data)
                    for (media in selectListPic) {
                        LogUtil.d("获取图片路径成功:" + media.path)
                        mPresenter.sendImageMessage(media)
                    }
                }
                REQUEST_CODE_VEDIO -> {
                    // 视频选择结果回调
                    val selectListVideo = PictureSelector.obtainMultipleResult(data)
                    for (media in selectListVideo) {
                        LogUtil.d("获取视频路径成功:" + media.path)
                        mPresenter.sendVideoMessage(media)
                    }
                }
            }
        }
    }
}
