package com.example.android.track.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.android.track.Adapter.MentionAdapter;
import com.example.android.track.Model.Follow;
import com.example.android.track.Model.LitePal_Entity.Acquaintance;
import com.example.android.track.R;
import com.example.android.track.Util.UserRequester;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thor on 2017/9/5.
 */

public class ChooseMentionActivity extends AppCompatActivity{
    private List<Follow> follows = new ArrayList<>();
    private List<Integer> chooseList = new ArrayList<>();
    private List<String> chooseNames = new ArrayList<>();
    
    // View
    private ProgressBar progressBar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    
    
    // Signal
    private final static int GET_FOLLOW_OK = 1;
    private final static int GET_SEARCH_RESULT_OK = 2;
    private final static int GET_SEARCH_RESULT_FAILED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mention);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.acquaintance_list);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Intent intent = getIntent();
        chooseList = intent.getIntegerArrayListExtra("chooseList"); // get choose list from last activity
        chooseNames = intent.getStringArrayListExtra("chooseNames");
        getAcquaintance();
    }

    // set toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choose_mention_toolbar,menu);
        MenuItem menuItem=menu.findItem(R.id.search);//
        searchView= (SearchView) MenuItemCompat.getActionView(menuItem);//加载searchview
        searchView.setSubmitButtonEnabled(true);//设置是否显示搜索按钮
        searchView.setQueryHint("查找");//设置提示信息
        searchView.setIconifiedByDefault(true);//设置搜索默认为图标
        setListener();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ok_btn:
                Intent intent = new Intent();
                ArrayList arrayList1 = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                arrayList1.addAll(chooseList);
                arrayList2.addAll(chooseNames);
                
                intent.putIntegerArrayListExtra("chooseList", arrayList1);
                intent.putStringArrayListExtra("chooseNames", arrayList2);
                
                setResult(RESULT_OK, intent);
                finish();
                break;

            default:
                break;
        }
        
        return true;
    }
    
    private void setListener(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(ChooseMentionActivity.this,"search...",Toast.LENGTH_LONG).show();
                if(query.length() > 20){
                    Toast.makeText(ChooseMentionActivity.this,"too long",Toast.LENGTH_LONG).show();
                    return true;
                }
                if(query.equals("")){
                    Toast.makeText(ChooseMentionActivity.this,"nothing",Toast.LENGTH_LONG).show();
                    return true;
                }
                search(query);
                return true;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(ChooseMentionActivity.this,"typing...",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        
    }
    

    private void getAcquaintance(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserRequester requester = new UserRequester();
                List<Acquaintance> acquaintanceList = DataSupport.select("*").find(Acquaintance.class);
                for(Acquaintance acquaintance : acquaintanceList){
                    Follow follow = new Follow();
                    follow.setUser_name(acquaintance.getUser_name());
                    follow.setUser_id(acquaintance.getUser_id());
                    follow.setState("stranger");
                    follow.setportrait_url(requester.getPortraitUrl(acquaintance.getUser_id()));
                    follows.add(follow);
                }

                Message message = new Message();
                message.what = GET_FOLLOW_OK;
                handler.sendMessage(message);

            }
        }).start();
    }

    private void setRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MentionAdapter adapter = new MentionAdapter(follows, chooseList, chooseNames, ChooseMentionActivity.this);
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
    }

    private void search(String query){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserRequester requester = new UserRequester();
                follows = requester.searchUser(query);
                Message message = new Message();
                if (follows == null)
                    message.what = GET_SEARCH_RESULT_FAILED;
                else
                    message.what = GET_SEARCH_RESULT_OK;
                handler.sendMessage(message);

            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_FOLLOW_OK:
                    setRecyclerView();
                    break;
                case GET_SEARCH_RESULT_OK:
                    setRecyclerView();
                    break;
                case GET_SEARCH_RESULT_FAILED:
                    Toast.makeText(ChooseMentionActivity.this, "没有匹配结果", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
}
