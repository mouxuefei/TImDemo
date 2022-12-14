package com.edocyun.timchat.mvp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.edocyun.timchat.R
import com.edocyun.timchat.base.BaseMvpActivity
import com.edocyun.timchat.base.toast
import com.edocyun.timchat.constants.Constants.REQUEST_CODE_FILE
import com.edocyun.timchat.constants.Constants.REQUEST_CODE_IMAGE
import com.edocyun.timchat.constants.Constants.REQUEST_CODE_VEDIO
import com.edocyun.timchat.constants.Constants.myId
import com.edocyun.timchat.mvp.adapter.ChatAdapter
import com.edocyun.timchat.mvp.api.*
import com.edocyun.timchat.mvp.contract.IChatContact
import com.edocyun.timchat.mvp.entity.*
import com.edocyun.timchat.mvp.presenter.ChatPresenter
import com.edocyun.timchat.util.*
import com.edocyun.timchat.util.dialog.TipDialogEntity
import com.edocyun.timchat.util.dialog.TipDialogType
import com.edocyun.timchat.util.dialog.showTipDialog
import com.edocyun.timchat.util.morelayout.MoreLayoutItemBean
import com.edocyun.timchat.widget.photoviewerlibrary.PhotoViewer
import com.luck.picture.lib.PictureSelector
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.layout_chat_input.*
import kotlinx.android.synthetic.main.layout_common_titlebar.*
import java.io.File
import java.util.*


class ChatActivity : BaseMvpActivity<IChatContact.View, IChatContact.Presenter>(),
    IChatContact.View, SwipeRefreshLayout.OnRefreshListener {
    private var mAdapter: ChatAdapter? = null
    private var chatUiHelper: ChatUiHelper? = null
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
        common_toolbar_title.text = "xxx??????"
    }

    override fun initData() {
        mPresenter.fetchData(false)
    }

    private fun initListener() {
        btnSend.setOnClickListener {
            mPresenter.sendTextMsg(etContent.text.toString())
            etContent.setText("")
        }
//        rlPhoto.setOnClickListener {
//            PictureFileUtil.openGalleryPic(this@ChatActivity, REQUEST_CODE_IMAGE)
//        }
//        rlVideo.setOnClickListener {
//            PictureFileUtil.openGalleryVideo(this@ChatActivity, REQUEST_CODE_VEDIO)
//        }
//        rlFile.setOnClickListener {
//            PictureFileUtil.openFile(this@ChatActivity, REQUEST_CODE_FILE)
//        }
//        rlLocation.setOnClickListener {
//            MaterialDialog.Builder(this)
//                .title("??????")
//                .content(
//                   ""
//                )
//                .positiveText("??????")
//                .negativeText("??????")
//                .canceledOnTouchOutside(false)
//                .cancelable(false)
//                .onPositive { dialog: MaterialDialog?, which: DialogAction? ->
//
//                }
//                .show()
//            val mTimer = CountDownTimer(
//                10 * 1000,
//                1000
//            )
//            mTimer.setTimerListener(object : NormalTimerListener() {
//                override fun onTick(millisUntilFinished: Long) {
//                    LogUtil.e("millisUntilFinished"  + millisUntilFinished)
//                    // ???????????????
//                }
//            })
//            mTimer.start()
//            val timer =
//                IntervalTimer(1000, object :
//                    IntervalTimer.OnTimerInter {
//                    override fun interval(time: Long) {
//                        LogUtil.e("interval" + time)
//                    }
//
//                    override fun cancel() {
//                         LogUtil.e("cancel")
//                    }
//                })
//            timer.start()
//
//            Handler().postDelayed({
//                timer.cancel()
//            }, 10000)
//            val entity = TipDialogEntity(
//                fragmentManager = supportFragmentManager,
//                title = "",
//                type = TipDialogType.Success,
//                buttonLeftClickListener = {
//
//                })
//            tipsDialog(entity)
//        }
    }

    private fun initRv() {
        mAdapter = ChatAdapter(this, ArrayList())
        rvChatList.adapter = mAdapter
        swipeChat.setOnRefreshListener(this)
        //??????????????????
        mAdapter?.addChildClickViewIds(
            R.id.chat_item_header,
            R.id.chat_item_layout_content,
            R.id.chat_item_fail
        )
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as Message
            dealItemClick(view, item, position)
        }
        rvRecommend.adapter = object :
            BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_recommend, null) {
            override fun convert(holder: BaseViewHolder, item: String) {
                holder.setText(R.id.itemRecommendTitle, item)
            }

        }.also { mRecommendAdapter = it }
    }

    /**
     * ????????????item??????
     */
    private fun dealItemClick(
        view: View,
        item: Message,
        position: Int
    ) {
        when (view.id) {
            //??????
            R.id.chat_item_header -> {
            }
            //????????????
            R.id.chat_item_fail -> {
                val entity = TipDialogEntity(
                    fragmentManager = supportFragmentManager,
                    title = "????????????",
                    desc = "????????????????????????",
                    type = TipDialogType.Info,
                    buttonRightTitle = "??????",
                    buttonRightColor = ContextCompat.getColor(this, R.color.primary),
                    buttonRightClickListener = {
                        //TODO:
                    },
                    buttonLeftTitle = "??????",
                    buttonLeftColor = ContextCompat.getColor(this, R.color.c_03081A),
                )
                showTipDialog(entity)
            }
            //??????
            R.id.chat_item_layout_content -> {
                when (item.msgType) {
                    MsgType.AUDIO -> {
                        onPressAudio(item, view, position)
                    }
                    MsgType.IMAGE -> {
                        onPressImage(view, item)
                    }
                    else -> {

                    }
                }
            }
        }
    }

    /**
     * ?????????????????????
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
                ivItemAudio?.setBackgroundResource(R.drawable.anim_audio_animation_right_list)
            } else {
                ivItemAudio?.setBackgroundResource(R.drawable.anim_audio_animation_left_list)
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
     * ????????????
     */
    private fun onPressImage(view: View, msg: Message) {
        if (msg.body is ImageMsgBody) {
            (msg.body as ImageMsgBody).thumbUrl?.let {
                PhotoViewer
                    .setClickSingleImg(it, view)   //?????????????????????????????????????????????????????????????????????
                    .setShowImageViewInterface(object : PhotoViewer.ShowImageViewInterface {
                        override fun show(iv: ImageView, url: String) {
                            Glide.with(iv.context).load(url).into(iv)
                        }
                    })
                    .start(this)
            }
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initChatUi() {
        val data = mutableListOf(
            MoreLayoutItemBean("??????", R.mipmap.ic_photo),
            MoreLayoutItemBean("??????", R.mipmap.ic_photo),
            MoreLayoutItemBean("??????", R.mipmap.ic_photo),
            MoreLayoutItemBean("??????", R.mipmap.ic_photo),
            MoreLayoutItemBean("??????", R.mipmap.ic_photo),
            MoreLayoutItemBean("??????", R.mipmap.ic_photo),
            MoreLayoutItemBean("??????", R.mipmap.ic_photo),
            MoreLayoutItemBean("??????", R.mipmap.ic_photo),
            MoreLayoutItemBean("??????", R.mipmap.ic_photo),
            MoreLayoutItemBean("??????", R.mipmap.ic_photo),
        )
        chatUiHelper = ChatUiHelper.with(this)
        chatUiHelper?.bindContentLayout(llContent)
            ?.bindToSendButton(btnSend)
            ?.bindEditText(etContent)
            ?.bindBottomLayout(bottomLayout)
            ?.bindEmojiLayout(rlEmotion as LinearLayout?)
            ?.bindAddLayout(llAdd as LinearLayout?)
            ?.bindToAddButton(ivAdd)
            ?.bindToEmojiButton(ivEmo)
            ?.bindAudioBtn(btnAudio)
            ?.bindAudioIv(ivAudioIcon)
            ?.bindMoreLayoutData(data)
//            .bindEmojiData()
        //??????????????????,?????????????????????????????????
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

        //??????????????????????????????
        rvChatList.setOnTouchListener(OnTouchListener { _, _ ->
            chatUiHelper?.hideBottomLayout(false)
            chatUiHelper?.hideSoftInput()
            etContent.clearFocus()
            ivEmo.setImageResource(R.mipmap.ic_emoji)
            false
        })

        //??????????????????
        btnAudio.setOnFinishedRecordListener { audioPath, time ->
            val file = File(audioPath)
            if (file.exists()) {
                mPresenter.sendAudioMessage(audioPath, time)
            }
        }
    }

    private fun updateMsg(message: Message) {
        mAdapter?.let {
            rvChatList.scrollToPosition(it.itemCount - 1)
            //??????2??????????????????
            Handler().postDelayed({
                var position = 0
                message.sentStatus = MsgSendStatus.FAILED
                //?????????????????????
                for (i in it.data.indices) {
                    val mAdapterMessage = it.data[i]
                    if (message.uuid == mAdapterMessage.uuid) {
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
            mRecommendAdapter?.setNewInstance(mutableListOf("????????????", "??????????????????", "ssssss"))
        }
    }

    override fun fetchDataError() {
        swipeChat.isRefreshing = false
    }

    override fun updateMessage(message: Message) {
        //????????????
        mAdapter?.addData(message)
        //???????????????????????????
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
                    // ????????????????????????
                    val selectListPic = PictureSelector.obtainMultipleResult(data)
                    for (media in selectListPic) {
                        LogUtil.d("????????????????????????:" + media.path)
                        mPresenter.sendImageMessage(media)
                    }
                }
                REQUEST_CODE_VEDIO -> {
                    // ????????????????????????
                    val selectListVideo = PictureSelector.obtainMultipleResult(data)
                    for (media in selectListVideo) {
                        LogUtil.d("????????????????????????:" + media.path)
                        mPresenter.sendVideoMessage(media)
                    }
                }
            }
        }
    }

}
