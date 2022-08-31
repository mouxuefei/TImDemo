package com.edocyun.timchat.util;

import android.app.Activity;

import com.edocyun.timchat.imageEngine.GlideEngine;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;


public class PictureFileUtil {

    /**
     * 从相册选择图片
     */
    public static void openGalleryPic(Activity mContext, int requstcode) {
        XXPermissions.with(mContext)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .request((permissions, all) -> {
                    if (!all) {
                        return;
                    }
                    // 进入相册 不需要的api可以不写
                    PictureSelector.create(mContext)
                            .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                            .maxSelectNum(1)// 最大图片选择数量
                            .minSelectNum(1)// 最小选择数量
                            .imageSpanCount(4)// 每行显示个数
                            .selectionMode(6 > 1 ?
                                    PictureConfig.MULTIPLE : PictureConfig.SINGLE)// 多选 or 单选
                            .isPreviewImage(true)// 是否可预览图片
                            .isCamera(false)// 是否显示拍照按钮
                            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                            .isEnableCrop(false)// 是否裁剪
                            .isCompress(true)// 是否压缩
                            .synOrAsy(true)//同步true或异步false 压缩 默认同步
                            .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                            .isGif(false)// 是否显示gif图片
                            .minimumCompressSize(100)// 小于100kb的图片不压缩
                            .imageEngine(GlideEngine.createGlideEngine())
                            .forResult(requstcode);//结果回调onActivityResult code
                });
    }


    public static void openGalleryVideo(Activity mContext, int requstcode) {

        XXPermissions.with(mContext)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .request((permissions, all) -> {
                    if (!all) {
                        return;
                    }
                    PictureSelector.create(mContext)
                            .openGallery(PictureMimeType.ofVideo())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                            .minSelectNum(1)// 最小选择数量
                            .maxSelectNum(1)
                            .imageSpanCount(3)// 每行显示个数
                            .isPreviewImage(true)// 是否可预览图片
                            .isPreviewVideo(true)// 是否可预览视频
                            .isEnablePreviewAudio(true) // 是否可播放音频
                            .isCamera(false)// 是否显示拍照按钮
                            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                            .isEnableCrop(false)// 是否裁剪
                            .isCompress(true)// 是否压缩
                            .synOrAsy(true)//同步true或异步false 压缩 默认同步
                            .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                            .isGif(false)// 是否显示gif图片
                            .minimumCompressSize(100)// 小于100kb的图片不压缩
                            .videoMaxSecond(300)//最大时长
                            .imageEngine(GlideEngine.createGlideEngine())
                            .forResult(requstcode);//结果回调onActivityResult code
                });

    }


    public static void openFile(Activity mContext, int requestCode) {
//        new MaterialFilePicker()
//                .withActivity(mContext)
//                .withRequestCode(requestCode)
//                //       .withFilter(Pattern.compile(".*\\.txt$")) // Filtering files and directories by file name using regexp
//                .withFilterDirectories(true) // Set directories filterable (false by default)
//                .withHiddenFiles(true) // Show hidden files and folders
//                .start();
    }
}
