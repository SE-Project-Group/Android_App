package com.example.android.android_app.fragment;

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

import com.example.android.android_app.Activity.AlbumActivity;
import com.example.android.android_app.Activity.FansActivity;
import com.example.android.android_app.Activity.LogInActivity;
import com.example.android.android_app.Activity.MyShareActivity;
import com.example.android.android_app.R;
import com.example.android.android_app.Activity.SettingActivity;

/**
 * Created by thor on 2017/6/28.
 */

public class UserFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container, false);
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
        LinearLayout setting_btn = (LinearLayout) getActivity().findViewById(R.id.unlog_setting_btn);
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout mycomment_btn = (LinearLayout) getActivity().findViewById(R.id.unlog_mycomment_btn);
        mycomment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                startActivity(intent);
            }
        });
    }
}
