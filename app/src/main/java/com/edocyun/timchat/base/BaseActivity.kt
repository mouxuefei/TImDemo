package com.edocyun.timchat.base

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.edocyun.timchat.R
import kotlin.properties.Delegates


/**
 * @FileName: com.mou.demo.basekotlin.BaseActivity.java
 * @author: mouxuefei
 * @date: 2017-12-19 15:05
 * @version V1.0 Activity的基类
 * @desc
 */
abstract class BaseActivity : AppCompatActivity() {
    open var mContext: Context by Delegates.notNull()//非空属性:Delegates.notNull()
    open var mProgressDialog: MLoadingView? = null
    open fun onSetContentViewNext(savedInstanceState: Bundle?) {}
    @LayoutRes
    abstract fun getContentView(): Int


    open fun useEventBus(): Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//强制竖屏
        mContext = this
        setContentView(getContentView())
        mProgressDialog = MLoadingView(this, R.style.dialog_transparent_style)
        bindView()
        initView()
        initData()
        onSetContentViewNext(savedInstanceState)

    }

    abstract fun initView()

    abstract fun initData()

    open fun bindView() {

    }



//    override fun startActivity(intent: Intent) {
//        super.startActivity(intent)
//        if (hasEnterTransitionAnim()) {
//            overridePendingTransitionEnter()
//        }
//    }
//
//    override fun finish() {
//        super.finish()
//        if (hasFinishTransitionAnim()) {
//            overridePendingTransitionExit()
//        }
//    }

    open fun hasFinishTransitionAnim(): Boolean {
        return true
    }

    open fun hasEnterTransitionAnim(): Boolean {
        return true
    }

    private fun overridePendingTransitionEnter() {
    }

    private fun overridePendingTransitionExit() {
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            val v = currentFocus
            //如果不是落在EditText区域，则需要关闭输入法
            if (hideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private fun hideKeyboard(view: View?, event: MotionEvent): Boolean {
        if (view != null && view is EditText) {
            val location = intArrayOf(0, 0)
            view.getLocationInWindow(location)
            //获取现在拥有焦点的控件view的位置，即EditText
            val left = location[0]
            val top = location[1]
            val bottom = top + view.height
            val right = left + view.width
            //判断我们手指点击的区域是否落在EditText上面，如果不是，则返回true，否则返回false
            val isInEt = (event.x > left && event.x < right && event.y > top && event.y < bottom)
            return !isInEt
        }
        return false
    }
}