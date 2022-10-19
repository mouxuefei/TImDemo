package com.edocyun.timchat.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.edocyun.timchat.R;


public class GlideUtils {

    public static void loadChatImage(final Context mContext, String imgUrl, final ImageView imageView) {
        final RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.default_img_failed)// 正在加载中的图片
                .error(R.mipmap.default_img_failed); // 加载失败的图片
        Glide.with(mContext)
                .load(imgUrl) // 图片地址
                .apply(options)
                .into(imageView);
    }

}
