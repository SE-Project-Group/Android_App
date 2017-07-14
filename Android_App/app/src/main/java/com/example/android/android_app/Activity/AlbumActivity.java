package com.example.android.android_app.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.example.android.android_app.Class.AlbumAdapter;

import com.example.android.android_app.Class.Album;
import com.example.android.android_app.R;

import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    private List<Album> albumList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initPics();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.album_View);
        StaggeredGridLayoutManager layoutManager = new
        StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        AlbumAdapter adapter = new AlbumAdapter(albumList);
        recyclerView.setAdapter(adapter);
    }

    private void initPics(){
        Album album1 = new Album(R.drawable.exp_pic);
        albumList.add((album1));
        albumList.add((album1));
        albumList.add((album1));
        albumList.add((album1));
        albumList.add((album1));
        albumList.add((album1));
        Album album2 = new Album(R.drawable.comment_down);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
        albumList.add(album2);
    }
}
