package com.example.android.track.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

/**
 * Created by thor on 2017/6/28.
 */

public class MessageFragment extends Fragment {
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

        setUnReadRemind();
        setClickListener();
        initRecyclerView();

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
}
