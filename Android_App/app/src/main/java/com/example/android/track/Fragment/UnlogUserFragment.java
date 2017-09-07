package com.example.android.track.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.track.Activity.LogInActivity;
import com.example.android.track.Activity.PersonalHomeActivity;
import com.example.android.track.R;
import com.example.android.track.Activity.SettingActivity;

/**
 * Created by thor on 2017/6/28.
 */

public class UnlogUserFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unlog_user, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //set tool bar
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.userToolBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // set button listener
        Button sign_btn = (Button) getActivity().findViewById(R.id.sign_btn);
        sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LogInActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout mycomment_btn = (LinearLayout) getActivity().findViewById(R.id.unlog_mycomment_btn);
        mycomment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast();
            }
        });

        LinearLayout myshare_btn = (LinearLayout) getActivity().findViewById(R.id.unlog_myshare_btn);
        myshare_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast();
            }
        });

        LinearLayout mylike_btn = (LinearLayout) getActivity().findViewById(R.id.unlog_mylike_btn);
        mylike_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast();
            }
        });

        LinearLayout myalbum_btn = (LinearLayout) getActivity().findViewById(R.id.unlog_myalbum_btn);
        myalbum_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast();
            }
        });
    }

    private void makeToast(){
        Toast.makeText(getActivity(), "  请您先登录~\n稍后发现更多精彩内容！",Toast.LENGTH_SHORT).show();
        return;
    }
}
