package com.edocyun.timchat.util.dialog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentManager
import com.edocyun.timchat.R

/**
 * @FileName: DialogExt.java
 * @author: villa_mou
 * @date: 10-09:27
 * @version V1.0 <描述当前版本功能>
 * @desc
 */

enum class TipDialogType {
    Error, Info, Success
}

class TipDialogEntity(
    val fragmentManager: FragmentManager,
    val title: String? = null,
    val desc: String? = null,
    val type: TipDialogType? = null,
    val buttonLeftTitle: String? = null,
    val buttonRightTitle: String? = null,
    val buttonLeftColor: String? = null,
    val buttonRightColor: String? = null,
    val buttonLeftClickListener: View.OnClickListener? = null,
    val buttonRightClickListener: View.OnClickListener? = null,
)

fun tipsDialog(entity: TipDialogEntity) {
    mDialog(entity.fragmentManager) {
        layoutId = R.layout.dialog_tip
        margin = 40
        outCancel = true
        viewClick = object : DialogListener() {
            override fun convertView(view: View, dialog: NiceDialog) {
                entity.title?.let {
                    val tv = view.getView<TextView>(R.id.dialog_tip_title)
                    tv.text = it
                }
                entity.desc?.let {
                    val tv = view.getView<TextView>(R.id.dialog_tip_desc)
                    tv.text = it
                }
                val right = view.getView<LinearLayout>(R.id.button_right)
                val line = view.getView<View>(R.id.dialog_tip_line)
                line.visibility = if (entity.buttonRightTitle == null) View.GONE else View.VISIBLE
                right.visibility = if (entity.buttonRightTitle == null) View.GONE else View.VISIBLE
                entity.buttonRightTitle?.let {
                    val tv = view.getView<TextView>(R.id.button_right_title)
                    tv.text = it
                    entity.buttonRightColor?.let { color ->
                        tv.setTextColor(Color.parseColor(color))
                    }
                }
                entity.buttonLeftTitle?.let {
                    val tv = view.getView<TextView>(R.id.button_Left_title)
                    tv.text = it
                    entity.buttonLeftColor?.let { color ->
                        tv.setTextColor(Color.parseColor(color))
                    }
                }
                entity.buttonLeftClickListener?.let {
                    view.getView<LinearLayout>(R.id.button_left).setOnClickListener { v ->
                        it.onClick(v)
                        dismiss()
                    }
                }
                entity.buttonRightClickListener?.let {
                    view.getView<LinearLayout>(R.id.button_right).setOnClickListener { v ->
                        it.onClick(v)
                        dismiss()
                    }
                }

                val icon = view.getView<AppCompatImageView>(R.id.dialog_tip_icon)
                entity.type?.let {
                    when (it) {
                        TipDialogType.Error -> {
                            icon.setImageBitmap(icon.context.getBitmapFromVectorDrawable(R.drawable.ic_error))
                        }
                        TipDialogType.Info -> {
                            icon.setImageBitmap(icon.context.getBitmapFromVectorDrawable(R.drawable.ic_info))
                        }
                        TipDialogType.Success -> {
                            icon.setImageBitmap(icon.context.getBitmapFromVectorDrawable(R.drawable.ic_success))
                        }
                    }
                }

            }
        }
    }
}

fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
    var drawable = ContextCompat.getDrawable(this, drawableId);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = (drawable?.let { DrawableCompat.wrap(it) })?.mutate();
    }

    val bitmap = drawable?.intrinsicHeight?.let {
        Bitmap.createBitmap(
            drawable.intrinsicWidth, it,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = bitmap?.let { Canvas(it) }
    canvas?.let {
        drawable?.setBounds(0, 0, canvas.width, canvas.height);
        drawable?.draw(canvas);
    }
    return bitmap
}

