package com.example.android.testaviary;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.adobe.creativesdk.aviary.AdobeImageIntent;

import me.nereo.multi_image_selector.MultiImageSelector;

public class MainActivity extends AppCompatActivity {

    private ImageView mEditedImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

                /* 2) Find the layout's ImageView by ID */
        mEditedImageView = (ImageView) findViewById(R.id.editedImageView);

        MultiImageSelector.create(getApplicationContext())
                .start(MainActivity.this, 1);

/*        Uri imageUri = Uri.parse("/storage/emulated/0/DCIM/Camera/IMG_20170608_142719.jpg");

        Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                .setData(imageUri)
                .build();

        startActivityForResult(imageEditorIntent, 1);*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                /* 4) Make a case for the request code we passed to startActivityForResult() */
                case 1:

                    /* 5) Show the image! */
                    Uri editedImageUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
                    mEditedImageView.setImageURI(editedImageUri);

                    break;
            }
        }
    }
}
