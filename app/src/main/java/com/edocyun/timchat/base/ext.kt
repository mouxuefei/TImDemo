package com.edocyun.timchat.base

import android.content.Context
import android.widget.Toast

/**
 * @FileName: ``.java
 * @author: villa_mou
 * @date: 08-15:33
 * @version V1.0 <描述当前版本功能>
 * @desc
 */

fun Context.toast(msg: String) {
    val sToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    sToast.show()
}