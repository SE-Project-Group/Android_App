package com.example.android.track.Fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.track.Activity.MentionRemindActivity;
import com.example.android.track.Adapter.MessageAdapter;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.Message;
import com.example.android.track.Activity.CommentRemindActivity;
import com.example.android.track.Activity.LikeRemindActivity;
import com.example.android.track.R;
import com.example.android.track.Util.RemindView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.baidu.location.d.j.O;

/**
 * Created by thor on 2017/6/28.
 */

public class MessageFragment extends Fragment {
    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private TextView notification_tv;
    private TextView chat_tv;
    // 滚动条图片
    private ImageView scrollbar;
    // 滚动条初始偏移量
    private int offset = 0;
    // 当前页编号
    private int currIndex = 0;
    // 滚动条宽度
    private int bmpW;
    //一倍滚动量
    private int one;



    private List<Message> messagesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.messageToolBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setViewPager();

    }

    private void setUnReadRemind(){
        RemindView likeRemindView = (RemindView) getActivity().findViewById(R.id.ic_like);
        RemindView commentRemindView = (RemindView) getActivity().findViewById(R.id.ic_comment);
        RemindView shareRemindView = (RemindView) getActivity().findViewById(R.id.ic_share);
        RemindView mentionRemindView = (RemindView) getActivity().findViewById(R.id.ic_mention);

        likeRemindView.setBackground(R.drawable.feed_item_like);
        commentRemindView.setBackground(R.drawable.user_comment);
        shareRemindView.setBackground(R.drawable.share_1);
        mentionRemindView.setBackground(R.drawable.new_feed_at);

        likeRemindView.setMessageCount(MyApplication.getUnReadLikenCnt());
        commentRemindView.setMessageCount(MyApplication.getUnReadCommentCnt());
        shareRemindView.setMessageCount(MyApplication.getUnReadShareCnt());
        mentionRemindView.setMessageCount(MyApplication.getUnReadMentionCnt());
    }

    private void setClickListener(){
        LinearLayout mycomment_btn = (LinearLayout) getActivity().findViewById(R.id.comment_remind);
        mycomment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CommentRemindActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout at_me_btn = (LinearLayout) getActivity().findViewById(R.id.at_me_remind);
        at_me_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MentionRemindActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout mylike_btn = (LinearLayout) getActivity().findViewById(R.id.like_remind);
        mylike_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LikeRemindActivity.class);
                startActivity(intent);
            }
        });
    }

    // get storages from SQLite
    private void initRecyclerView(){
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Message messageA = new Message();
        messageA.setDate(time);
        messageA.setMessage_text("hahaha");
        messageA.setPortrait_url("");
        messageA.setUser_id(0);
        messageA.setUser_name("Root");
        messagesList.add(messageA);

        // init recyclerView
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.message_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        MessageAdapter adapter = new MessageAdapter(messagesList, getContext());
        recyclerView.setAdapter(adapter);
    }

    private void setViewPager(){
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View notification_view = inflater.inflate(R.layout.view_pager_notification, null);
        View chat_view = inflater.inflate(R.layout.view_pager_chat, null);
        notification_tv = (TextView)getActivity().findViewById(R.id.videoLayout);
        chat_tv = (TextView)getActivity().findViewById(R.id.musicLayout);
        scrollbar = (ImageView)getActivity().findViewById(R.id.scrollbar);

        notification_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                setUnReadRemind();
                setClickListener();
            }
        });
        chat_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
                initRecyclerView();
            }
        });

        pageview =new ArrayList<View>();
        //添加想要切换的界面
        pageview.add(notification_view);
        pageview.add(chat_view);
        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter(){

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }

            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0==arg1;
            }
            //使从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1){
                ((ViewPager)arg0).addView(pageview.get(arg1));
                return pageview.get(arg1);
            }
        };
        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);
        //设置viewPager的初始界面为第一个界面
        viewPager.setCurrentItem(0);
        //添加切换界面的监听器
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        // 获取滚动条的宽度
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.scrollbar).getWidth();
        //为了获取屏幕宽度，新建一个DisplayMetrics对象
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //将当前窗口的一些信息放在DisplayMetrics类中
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //得到屏幕的宽度
        int screenW = displayMetrics.widthPixels;
        //计算出滚动条初始的偏移量
        offset = (screenW / 2 - bmpW) / 2;
        //计算出切换一个界面时，滚动条的位移量
        one = offset * 2 + bmpW;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        //将滚动条的初始位置设置成与左边界间隔一个offset
        scrollbar.setImageMatrix(matrix);
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    /**
                     * TranslateAnimation的四个属性分别为
                     * float fromXDelta 动画开始的点离当前View X坐标上的差值
                     * float toXDelta 动画结束的点离当前View X坐标上的差值
                     * float fromYDelta 动画开始的点离当前View Y坐标上的差值
                     * float toYDelta 动画开始的点离当前View Y坐标上的差值
                     **/
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    break;
                case 1:
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    break;
            }
            //arg0为切换到的页的编码
            currIndex = arg0;
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
            //滚动条开始动画
            scrollbar.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
}
