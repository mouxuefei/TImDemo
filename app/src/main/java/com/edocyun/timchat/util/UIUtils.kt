package com.edocyun.timchat.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.edocyun.timchat.MainApplication

/**
 * @FileName: MUIUtils.java
 * @author: villa_mou
 * @date: 10-17:15
 * @version V1.0 <描述当前版本功能>
 * @desc
 */
class UIUtils {
    companion object {
        private val resources: Resources
            get() = MainApplication.getApp().resources
        val displayMetrics: DisplayMetrics
            get() {
                val displayMetrics = DisplayMetrics()
                val windowManager = MainApplication.getApp()
                    .getSystemService(Context.WINDOW_SERVICE) as WindowManager
                windowManager.defaultDisplay.getMetrics(displayMetrics)

                return displayMetrics
            }

        /**
         * 获取屏幕宽度 像素值
         * @return 屏幕宽度
         */
        val screenWidth: Int
            get() = displayMetrics.widthPixels

        /**
         * 获取屏幕高度 像素值
         * @return 屏幕高度
         */
        val screenHegith: Int
            get() = displayMetrics.heightPixels

        /**
         * 获取颜色值
         * @param resId 颜色资源id
         * @return 颜色值
         */
        fun getColor(resId: Int): Int {
            return resources.getColor(resId)
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        fun getColor(resId: Int, theme: Resources.Theme): Int {
            return resources.getColor(resId, theme)
        }

        fun getColor(color: String): Int {
            return Color.parseColor(color)
        }

        /**
         * 获取Drawable
         * @param resTd Drawable资源id
         * @return Drawable
         */
        fun getDrawable(resTd: Int): Drawable {
            return resources.getDrawable(resTd)
        }

        /**
         * 获取字符串
         * @param resId 字符串资源id
         * @return 字符串
         */
        fun getString(resId: Int): String {
            return resources.getString(resId)
        }

        /**
         * 获取字符串数组
         * @param resId 数组资源id
         * @return 字符串数组
         */
        fun getStringArray(resId: Int): Array<String> {
            return resources.getStringArray(resId)
        }

        /**
         * 将dp值转换为px值
         * @param dp 需要转换的dp值
         * @return px值
         */
        fun dp2px(dp: Float): Float {
            return (resources.displayMetrics.density * dp + 0.5f)
            //        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        }

        /**
         * 将px值转换为dp值
         * @param px 需要转换的px值
         * @return dp值
         */
        fun px2dp(px: Float): Float {
            return (px / resources.displayMetrics.density + 0.5f)
        }

        fun sp2px(sp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                resources.displayMetrics
            )
        }
    }

}