package com.example.android.track.Activity;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SearchEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.track.Adapter.FeedAdapter;
import com.example.android.track.Adapter.FollowAdapter;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.Model.Feed;
import com.example.android.track.Model.Follow;
import com.example.android.track.R;
import com.example.android.track.Util.FeedRequester;
import com.example.android.track.Util.UserRequester;
import com.example.android.track.View.RemindView;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.id;
import static android.R.attr.offset;
import static com.example.android.track.R.id.cancel_action;
import static com.example.android.track.R.id.chat_layout;
import static com.example.android.track.R.id.chat_tv;
import static com.example.android.track.R.id.feed_layout;
import static com.example.android.track.R.id.notification_layout;
import static com.example.android.track.R.id.notification_tv;
import static com.example.android.track.R.id.search;
import static com.example.android.track.R.id.user_layout;
import static com.example.android.track.R.id.viewPager;

public class SearchActivity extends AppCompatActivity {
    private View search_user_view;
    private View search_feed_view;
    private RecyclerView feedRecyclerView;
    private RecyclerView userRecyclerView;
    RelativeLayout feed_layout;
    RelativeLayout user_layout;
    private TextView feed_tv;
    private TextView user_tv;
    private String which_page;
    private ArrayList<View> pageview;
    // 滚动条图片
    private ImageView scrollbar;
    // 滚动条初始偏移量
    private int offset = 0;
    // 滚动条宽度
    private int bmpW;
    //一倍滚动量
    private int one;
    
    // message
    private static final int GET_SEARCH_RESULT_OK = 1;
    private static final int GET_SEARCH_RESULT_FAILED = 2;
    private static final int GET_EMPTY_RESULT = 3;
    
    private List<Follow> userSearchResult;
    private List<Feed> feedSearchResult;

    private ProgressDialog progressDialog;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //setToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.searchToolBar);
        toolbar.setTitleTextColor(SearchActivity.this.getResources().getColor(R.color.gray));
        setSupportActionBar(toolbar);

        // a hint shown while searching something
        progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setTitle("搜索");
        progressDialog.setMessage("正在拼命检索....");

        feed_layout = (RelativeLayout) findViewById(R.id.feed_layout);
        user_layout = (RelativeLayout) findViewById(R.id.user_layout);
        
        setSearchView();
        setViewPager();

        // init click
        feed_layout.callOnClick();
    }
    
    private void setSearchView(){
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length() > 30){
                    Toast.makeText(SearchActivity.this,"您输入的字段太长啦",Toast.LENGTH_LONG).show();
                    return true;
                }
                if(query.equals("")){
                    Toast.makeText(SearchActivity.this,"搜索内容不能为空",Toast.LENGTH_LONG).show();
                    return true;
                }
                // show search hint
                progressDialog.show();

                search(query);
                return true;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(SearchActivity.this,"typing...",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void setViewPager(){
        // set view pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater = getLayoutInflater();
        search_user_view = inflater.inflate(R.layout.view_pager_search, null);
        search_feed_view = inflater.inflate(R.layout.view_pager_search, null);
        
        feed_tv = (TextView) findViewById(R.id.feed_tv);
        user_tv = (TextView) findViewById(R.id.user_tv); 
        scrollbar = (ImageView)findViewById(R.id.scrollbar);
        
        pageview =new ArrayList<View>();
        //添加想要切换的界面
        pageview.add(search_feed_view);
        pageview.add(search_user_view);
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
        which_page = "feed";

        // 获取滚动条的宽度
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.scrollbar).getWidth();
        //为了获取屏幕宽度，新建一个DisplayMetrics对象
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //将当前窗口的一些信息放在DisplayMetrics类中
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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
        feed_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        user_layout.setOnClickListener(new View.OnClickListener() {
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
                    animation = new TranslateAnimation(one, 0, 0, 0);

                    feed_tv.setTextColor(getResources().getColor(R.color.gray));
                    user_tv.setTextColor(getResources().getColor(R.color.white));
                    which_page = "feed";
                    break;
                case 1:
                    animation = new TranslateAnimation(offset, one, 0, 0);

                    user_tv.setTextColor(getResources().getColor(R.color.gray));
                    feed_tv.setTextColor(getResources().getColor(R.color.white));
                    which_page = "user";
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
    
    private void search(String rawQuery){
        String query = transSpecialChar(rawQuery);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(which_page.equals("feed")){
                    FeedRequester requester = new FeedRequester();
                    feedSearchResult = requester.searchFeed(query);
                    Message message = new Message();
                    if(feedSearchResult == null)
                        message.what = GET_SEARCH_RESULT_FAILED;
                    else if (feedSearchResult.size() == 0)
                        message.what = GET_EMPTY_RESULT;
                    else
                        message.what = GET_SEARCH_RESULT_OK;
                    handler.sendMessage(message);
                }
                else{
                    UserRequester requester = new UserRequester();
                    userSearchResult = requester.searchUser(query);
                    Message message = new Message();
                    if(userSearchResult == null)
                        message.what = GET_SEARCH_RESULT_FAILED;
                    else if (userSearchResult.size() == 0)
                        message.what = GET_EMPTY_RESULT;
                    else
                        message.what = GET_SEARCH_RESULT_OK;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
    
    private void showResult(){
        if(which_page.equals("feed")){
            feedRecyclerView = (RecyclerView) search_feed_view.findViewById(R.id.search_recyclerView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
            feedRecyclerView.setLayoutManager(layoutManager);
            FeedAdapter adapter = new FeedAdapter(SearchActivity.this, feedSearchResult);
            feedRecyclerView.setAdapter(adapter);
        }
        else{
            userRecyclerView = (RecyclerView) search_user_view.findViewById(R.id.search_recyclerView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
            userRecyclerView.setLayoutManager(layoutManager);
            FollowAdapter adapter = new FollowAdapter(userSearchResult, SearchActivity.this);
            userRecyclerView.setAdapter(adapter);
        }
    }
    
    private String transSpecialChar(String rawStr){
        rawStr = rawStr.replace("%", "%25");
        rawStr = rawStr.replace("+","%2B");
        rawStr = rawStr.replace("/", "%2F");
        rawStr = rawStr.replace(" ","%20" );
        rawStr = rawStr.replace("?","%3F");
        rawStr = rawStr.replace("#", "%23");
        rawStr = rawStr.replace("&", "%26");
        rawStr = rawStr.replace("=", "%3D");

        return rawStr;
    }
    
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            switch(msg.what){
                case GET_SEARCH_RESULT_OK:
                    showResult();
                    break;
                case GET_SEARCH_RESULT_FAILED:
                    Toast.makeText(SearchActivity.this, "search failed", Toast.LENGTH_SHORT).show();
                    break;
                case GET_EMPTY_RESULT:
                    Toast.makeText(SearchActivity.this, "啥也没搜到呢，再试试吧~", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

}
