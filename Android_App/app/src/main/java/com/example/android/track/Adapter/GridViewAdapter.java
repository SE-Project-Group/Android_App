package com.example.android.track.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.track.Activity.NewFeedActivity;
import com.example.android.track.Application.MyApplication;
import com.example.android.track.R;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by thor on 2017/8/1.
 */

public class GridViewAdapter extends BaseAdapter {
    private List<String> pathList = new ArrayList<>();
    private Context context;

    private static final int ADD_PHOTO = 1;

    public GridViewAdapter(List<String> pathList, Context context) {
        super();
        this.pathList = pathList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return pathList.size() + 1;  //  add button
    }

    @Override
    public Object getItem(int position) {
        return pathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // 获得容器
            convertView = LayoutInflater.from(this.context).inflate(R.layout.item_gridview, null);

            // 初始化组件
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            // 给converHolder附加一个对象
            convertView.setTag(viewHolder);
        } else {
            // 取得converHolder附加的对象
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 给组件设置资源
        if(position < pathList.size()) {
            String path = pathList.get(position);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            Bitmap scaledBitmap = scaleBitmap(bitmap, 300, 300);
            viewHolder.image.setImageBitmap(scaledBitmap);
        }
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position == pathList.size()){
                    ArrayList<String> origin = new ArrayList<String>();
                    origin.addAll(pathList);
                    MultiImageSelector.create(MyApplication.getContext())
                            .origin(origin)
                            .start((Activity) context, ADD_PHOTO);
                }
                else{
                    ((NewFeedActivity) context).setPopView(position);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        public ImageView image;
    }


    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight){
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }


}
