package com.example.android.android_app.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;;

import com.example.android.android_app.Model.Album;
import com.example.android.android_app.R;

import java.util.List;

/**
 * Created by jarvis on 2017/7/14.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>  {
    private List<Album> mAlbumList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView pic_view;

        public ViewHolder (View view){
            super(view);
            pic_view = (ImageView)view.findViewById(R.id.album_image);
        }
    }

    public AlbumAdapter(List<Album> albumList){
        mAlbumList = albumList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album,parent,false);
        ViewHolder holder = new AlbumAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Album album = mAlbumList.get(position);
        holder.pic_view.setImageResource(album.getPic_id());
    }

    @Override
    public int getItemCount(){
        return mAlbumList.size();
    }
}
