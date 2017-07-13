package com.example.android.android_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.SearchView;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(SearchActivity.this,"search...",Toast.LENGTH_LONG).show();
                return true;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                /*
                if (!TextUtils.isEmpty(newText)){
                    //mListView.setFilterText(newText);
                }else{
                    //mListView.clearTextFilter();
                }
                */
                Toast.makeText(SearchActivity.this,"typing...",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //setToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.searchToolBar);
        setSupportActionBar(toolbar);

    }
}
