package com.example.android.android_app.Class;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import static android.R.attr.contextUri;
import static android.R.attr.data;
import static com.baidu.location.d.j.n;

/**
 * Created by thor on 2017/6/30.
 */

public class ImageUriParser {
    private Activity context;

    public ImageUriParser(Activity context) {
        this.context = context;
    }

    public String parse(Uri uri){
        if(Build.VERSION.SDK_INT >= 19){
            return parseOnKitKat(uri);
        }else{
            return parseBeforeKitKat(uri);
        }
    }

    @TargetApi(19)
    private String parseOnKitKat(Uri uri){
        String result = null;
        // if document type Uri, deal with document id
        if(DocumentsContract.isDocumentUri(context, uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if(uri.getAuthority().equals("com.android.providers.media.documents")){
                // get id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                result =  getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if (uri.getAuthority().equals("com.android.providers.downloads.documents")){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                result = getImagePath(contentUri, null);
            }
        }else if(uri.getScheme().equals("content")){
            // content type uri
            result = getImagePath(uri, null);
        }else if(uri.getScheme().equals("file")){
            // file type uri
            result = uri.getPath();
        }
        return result;

    }
    private String parseBeforeKitKat(Uri uri){
        return getImagePath(uri, null);
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = context.getContentResolver()
                .query(uri, null, selection, null, null);
        if(cursor == null ){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}

