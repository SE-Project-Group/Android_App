package com.example.android.android_app;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.WindowManager.LayoutParams;

import com.example.android.android_app.R;

import static com.baidu.location.d.j.W;

/**
 * Created by thor on 2017/6/30.
 */

public abstract class BottomPopView {
    private Context mContext;
    private View anchor;
    private LayoutInflater mInflater;
    private TextView mTvTop;
    private TextView mTvBottom;
    private TextView mTvCancel;
    private PopupWindow mPopupWindow;
    LayoutParams params;
    WindowManager windowManager;
    Window window;

    /**
     * @param context
     * @param anchor  依附在哪个View下面
     */
    public BottomPopView(Activity context, View anchor) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.anchor = anchor;
        windowManager = context.getWindowManager();
        window = context.getWindow();
        params = context.getWindow().getAttributes();
        init();
    }

    public void init() {
        View view = mInflater.inflate(R.layout.bottom_pop_window, null);
        params.dimAmount = 0.5f;
        window.addFlags(LayoutParams.FLAG_DIM_BEHIND);
        mTvBottom = (TextView) view.findViewById(R.id.tv_choose_photo);
        mTvTop = (TextView) view.findViewById(R.id.tv_take_photo);
        mTvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        mTvTop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onTopButtonClick();
            }
        });
        mTvBottom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBottomButtonClick();
            }
        });
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //监听PopupWindow的dismiss，当dismiss时屏幕恢复亮度
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                window.setAttributes(params);
            }
        });
        mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        // 动画效果 从底部弹起
        mPopupWindow.setAnimationStyle(R.style.animation);
    }

    /**
     * 显示底部对话框
     */
    public void show() {
        mPopupWindow.showAtLocation(anchor, Gravity.BOTTOM, 0, 0);
        params.alpha = 0.5f;
        window.setAttributes(params);
    }

    /**
     * 第一个按钮被点击的回调
     */
    public abstract void onTopButtonClick();

    /**
     * 第二个按钮被点击的回调
     */
    public abstract void onBottomButtonClick();

    public void setTopText(String text) {
        mTvTop.setText(text);
    }

    public void setBottomText(String text) {
        mTvBottom.setText(text);
    }
    public void dismiss(){
        if(mPopupWindow!=null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
    }
}
