package com.edocyun.timchat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edocyun.timchat.R;
import com.edocyun.timchat.util.ScreenUtil;

import androidx.annotation.Nullable;

public class TitleBarLayout extends LinearLayout {
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    private LinearLayout mLeftGroup;
    private LinearLayout mRightGroup;
    private TextView mLeftTitle;
    private TextView mCenterTitle;
    private TextView mRightTitle;
    private ImageView mLeftIcon;
    private ImageView mRightIcon;
    private RelativeLayout mTitleLayout;
    private UnreadCountTextView unreadCountTextView;

    public TitleBarLayout(Context context) {
        super(context);
        init();
    }

    public TitleBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.title_bar_layout, this);
        mTitleLayout = findViewById(R.id.page_title_layout);
        mLeftGroup = findViewById(R.id.page_title_left_group);
        mRightGroup = findViewById(R.id.page_title_right_group);
        mLeftTitle = findViewById(R.id.page_title_left_text);
        mRightTitle = findViewById(R.id.page_title_right_text);
        mCenterTitle = findViewById(R.id.page_title);
        mLeftIcon = findViewById(R.id.page_title_left_icon);
        mRightIcon = findViewById(R.id.page_title_right_icon);
        unreadCountTextView = findViewById(R.id.new_message_total_unread);

        LayoutParams params = (LayoutParams) mTitleLayout.getLayoutParams();
        params.height = ScreenUtil.getPxByDp(50);
        mTitleLayout.setLayoutParams(params);

        int iconSize = ScreenUtil.dip2px(20);
        ViewGroup.LayoutParams iconParams = mLeftIcon.getLayoutParams();
        iconParams.width = iconSize;
        iconParams.height = iconSize;
        mLeftIcon.setLayoutParams(iconParams);
        iconParams = mRightIcon.getLayoutParams();
        iconParams.width = iconSize;
        iconParams.height = iconSize;

        mRightIcon.setLayoutParams(iconParams);

    }

    public void setOnLeftClickListener(OnClickListener listener) {
        mLeftGroup.setOnClickListener(listener);
    }

    public void setOnRightClickListener(OnClickListener listener) {
        mRightGroup.setOnClickListener(listener);
    }

    public void setTitle(String title, int position) {
        switch (position) {
            case LEFT:
                mLeftTitle.setText(title);
                break;
            case RIGHT:
                mRightTitle.setText(title);
                break;
            case CENTER:
                mCenterTitle.setText(title);
                break;
        }
    }

    public LinearLayout getLeftGroup() {
        return mLeftGroup;
    }

    public LinearLayout getRightGroup() {
        return mRightGroup;
    }

    public ImageView getLeftIcon() {
        return mLeftIcon;
    }

    public void setLeftIcon(int resId) {
        mLeftIcon.setBackgroundResource(resId);
    }

    public ImageView getRightIcon() {
        return mRightIcon;
    }

    public void setRightIcon(int resId) {
        mRightIcon.setBackgroundResource(resId);
    }

    public TextView getLeftTitle() {
        return mLeftTitle;
    }

    public TextView getMiddleTitle() {
        return mCenterTitle;
    }

    public TextView getRightTitle() {
        return mRightTitle;
    }

    public UnreadCountTextView getUnreadCountTextView() {
        return unreadCountTextView;
    }
}
