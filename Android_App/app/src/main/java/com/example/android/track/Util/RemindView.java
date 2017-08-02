package com.example.android.track.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.track.R;

/**
 * Created by thor on 2017/8/2.
 */

public class RemindView extends RelativeLayout {

    private int msgCount;
    private ImageView background;
    private TextView bar_num;

    public RemindView(Context context) {
        this(context, null);
    }

    public RemindView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RemindView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        RelativeLayout rl = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.unread_remind, this, true);
        background = (ImageView) rl.findViewById(R.id.background_iv);
        bar_num = (TextView) rl.findViewById(R.id.bar_num);
    }

    public void setBackground(int id){
        background.setImageResource(id);
    }

    public void setMessageCount(int count) {
        msgCount = count;
        if (count == 0) {
            bar_num.setVisibility(View.GONE);
        } else {
            bar_num.setVisibility(View.VISIBLE);
            if (count < 100) {
                bar_num.setText(count + "");
            } else {
                bar_num.setText("99+");
            }
        }
        invalidate();
    }
}
