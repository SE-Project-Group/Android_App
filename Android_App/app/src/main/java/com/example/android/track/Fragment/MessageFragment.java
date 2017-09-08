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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.track.Activity.FollowActivity;
import com.example.android.track.Activity.RemindActivity;
import com.example.android.track.Adapter.ConversationAdapter;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.LitePal_Entity.Acquaintance;
import com.example.android.track.Model.Remind;
import com.example.android.track.R;
import com.example.android.track.Util.AcquaintanceManager;
import com.example.android.track.Util.Verify;
import com.example.android.track.View.RemindView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;

import static org.litepal.crud.DataSupport.select;

/**
 * Created by thor on 2017/6/28.
 */

public class MessageFragment extends Fragment {
    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private TextView notification_tv;
    private TextView chat_tv;

    private RemindView notification_ic;
    private RemindView chat_ic;

    private RelativeLayout notification_layout;
    private RelativeLayout chat_layout;
    // 滚动条图片
    private ImageView scrollbar;
    // 滚动条初始偏移量
    private int offset = 0;
    // 滚动条宽度
    private int bmpW;
    //一倍滚动量
    private int one;

    private View notification_view;
    private View chat_view;

    private boolean logged;

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

        logged = new Verify().getLoged();
        setViewPager();
        notification_layout.performClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        // update unread cnt
        notification_ic.setMessageCount(MyApplication.getUnReadMsgCnt());
        chat_ic.setMessageCount(JMessageClient.getAllUnReadMsgCount());
        initChatRecord(chat_view);
        setUnReadRemind(notification_view);
    }

    private void setUnReadRemind(View parentView){
        RemindView likeRemindView = (RemindView) parentView.findViewById(R.id.ic_like);
        RemindView commentRemindView = (RemindView) parentView.findViewById(R.id.ic_comment);
        RemindView shareRemindView = (RemindView) parentView.findViewById(R.id.ic_share);
        RemindView mentionRemindView = (RemindView) parentView.findViewById(R.id.ic_mention);
        RemindView followMeRemindView = (RemindView) parentView.findViewById(R.id.ic_followMe);

        likeRemindView.setBackground(R.drawable.heart_filled_24);
        commentRemindView.setBackground(R.drawable.comment_24);
        shareRemindView.setBackground(R.drawable.share_24);
        mentionRemindView.setBackground(R.drawable.mention_24);
        followMeRemindView.setBackground(R.drawable.telescope_filled_20);

        likeRemindView.setMessageCount(MyApplication.getUnReadLikenCnt());
        commentRemindView.setMessageCount(MyApplication.getUnReadCommentCnt());
        shareRemindView.setMessageCount(MyApplication.getUnReadShareCnt());
        mentionRemindView.setMessageCount(MyApplication.getUnReadMentionCnt());
        followMeRemindView.setMessageCount(MyApplication.getUnReadFollowMeCnt());
    }

    private void setClickListener(View parentView){
        LinearLayout commentMe_btn = (LinearLayout) parentView.findViewById(R.id.comment_remind);
        commentMe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int unReadCnt = MyApplication.getUnReadCommentCnt();
                int new_cnt = MyApplication.getUnReadMsgCnt() - unReadCnt;
                MyApplication.setUnReadMsgCnt(new_cnt);
                MyApplication.setUnReadCommentCnt(0);
                Intent intent = new Intent(getActivity(),RemindActivity.class);
                intent.putExtra("type", "comment");
                startActivity(intent);
            }
        });

        LinearLayout mentionMe_btn = (LinearLayout) parentView.findViewById(R.id.mention_remind);
        mentionMe_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int unReadCnt = MyApplication.getUnReadMentionCnt();
                int new_cnt = MyApplication.getUnReadMsgCnt() - unReadCnt;
                MyApplication.setUnReadMentionCnt(new_cnt);
                MyApplication.setUnReadShareCnt(0);
                Intent intent = new Intent(getActivity(), RemindActivity.class);
                intent.putExtra("type", "mention");
                startActivity(intent);
            }
        });

        LinearLayout likeMe_btn = (LinearLayout) parentView.findViewById(R.id.like_remind);
        likeMe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int unReadCnt = MyApplication.getUnReadLikenCnt();
                int new_cnt = MyApplication.getUnReadMsgCnt() - unReadCnt;
                MyApplication.setUnReadMsgCnt(new_cnt);
                MyApplication.setUnReadLikenCnt(0);
                Intent intent = new Intent(getActivity(),RemindActivity.class);
                intent.putExtra("type", "like");
                startActivity(intent);
            }
        });
        LinearLayout shareMe_btn = (LinearLayout) parentView.findViewById(R.id.share_remind);
        shareMe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int unReadCnt = MyApplication.getUnReadShareCnt();
                int new_cnt = MyApplication.getUnReadMsgCnt() - unReadCnt;
                MyApplication.setUnReadMsgCnt(new_cnt);
                MyApplication.setUnReadShareCnt(0);
                Intent intent = new Intent(getActivity(),RemindActivity.class);
                intent.putExtra("type", "share");
                startActivity(intent);
            }
        });

        LinearLayout followMe_btn = (LinearLayout) parentView.findViewById(R.id.followMe_remind);
        followMe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int unReadFollowMeCnt = MyApplication.getUnReadFollowMeCnt();
                int new_cnt = MyApplication.getUnReadMsgCnt() - unReadFollowMeCnt;
                MyApplication.setUnReadMsgCnt(new_cnt);
                MyApplication.setUnReadFollowMeCnt(0);
                Intent intent = new Intent(getActivity(), FollowActivity.class);
                intent.putExtra("relationship", "follower");
                intent.putExtra("who", Integer.valueOf(new Verify().getUser_id()));
                startActivity(intent);
            }
        });
    }


    private void initChatRecord(View parentView){
        List<Conversation> conversationList = JMessageClient.getConversationList();
        if (conversationList == null) {
            Toast.makeText(getActivity(), "not log in", Toast.LENGTH_SHORT);
            return;
        }
        // download portraits and name
        for(Conversation conversation : conversationList){

            Acquaintance acquaintance = DataSupport.select("user_name").where("user_id = ?", conversation.getTargetId())
                    .findFirst(Acquaintance.class);
            if(acquaintance == null)
                AcquaintanceManager.saveAcquaintance(Integer.valueOf(conversation.getTargetId()));
        }
        // init recyclerView
        RecyclerView recyclerView = (RecyclerView) parentView.findViewById(R.id.message_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        ConversationAdapter adapter = new ConversationAdapter(conversationList, getContext());
        recyclerView.setAdapter(adapter);

    }

    private void setViewPager(){
        // set Icon
        notification_ic = (RemindView) getActivity().findViewById(R.id.ic_notification);
        chat_ic = (RemindView) getActivity().findViewById(R.id.ic_chat);
        //notification_ic.setBackground(R);

        notification_ic.setMessageCount(MyApplication.getUnReadMsgCnt());
        chat_ic.setMessageCount(JMessageClient.getAllUnReadMsgCount());

        // set view pager

        viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater = getActivity().getLayoutInflater();
        notification_view = inflater.inflate(R.layout.view_pager_notification, null);
        chat_view = inflater.inflate(R.layout.view_pager_chat, null);
        notification_tv = (TextView)getActivity().findViewById(R.id.notification_tv);
        chat_tv = (TextView)getActivity().findViewById(R.id.chat_tv);
        scrollbar = (ImageView)getActivity().findViewById(R.id.scrollbar);


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
        //添加切换界面的监听器
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        viewPager.setCurrentItem(1);

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


        // set Listener
        notification_layout = (RelativeLayout) getActivity().findViewById(R.id.notification_layout);
        chat_layout = (RelativeLayout) getActivity().findViewById(R.id.chat_layout);
        notification_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        chat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

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
                    // update unread cnt
                    notification_ic.setMessageCount(MyApplication.getUnReadMsgCnt());
                    chat_ic.setMessageCount(JMessageClient.getAllUnReadMsgCount());
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    // change color
                    //notification_ic.setBackground();
                    //chat_ic.setBackground();
                    notification_tv.setTextColor(getResources().getColor(R.color.gray));
                    chat_tv.setTextColor(getResources().getColor(R.color.white));

                    setUnReadRemind(notification_view);
                    if(logged) {
                        setClickListener(notification_view);
                    }
                    break;
                case 1:
                    // update unread cnt
                    notification_ic.setMessageCount(MyApplication.getUnReadMsgCnt());
                    chat_ic.setMessageCount(JMessageClient.getAllUnReadMsgCount());
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    // change color
                    //chat_ic.setBackground();
                    //notification_ic.setBackground();
                    chat_tv.setTextColor(getResources().getColor(R.color.gray));
                    notification_tv.setTextColor(getResources().getColor(R.color.white));
                    if(logged) {
                        initChatRecord(chat_view);
                    }
                    break;
            }
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(150);
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
