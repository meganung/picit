package com.example.android.sheinnovates;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LaunchActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_MULTIPLE = 1001;
    private Uri imageUri;
    private static final String TAG = "LaunchActivity";

    // references to our images
    public static Integer[] mThumbIds = {
            R.drawable.test, R.drawable.test1,
            R.drawable.test2, R.drawable.test3,
            R.drawable.test4, R.drawable.test5,
            R.drawable.test, R.drawable.test1,
            R.drawable.test2, R.drawable.test3,
            R.drawable.test4, R.drawable.test5,
            R.drawable.test, R.drawable.test1,
            R.drawable.test2, R.drawable.test3,
            R.drawable.test4, R.drawable.test5,
            R.drawable.test, R.drawable.test1,
            R.drawable.test2, R.drawable.test3,
            R.drawable.test4, R.drawable.test5
    };
    public static ArrayList<Uri> mThumbUris = new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent myIntent = new Intent(LaunchActivity.this, MainActivity.class);
//                myIntent.putExtra("key", value); //Optional parameters
                LaunchActivity.this.startActivity(myIntent);
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click upload image
                startChooseImageIntentForResult();

            }
        });

    }

    private void startChooseImageIntentForResult() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            List<String> imagesEncodedList = new ArrayList<String>();
            if(data.getData()!=null) {
                imageUri = data.getData();
                // Get the cursor
                Cursor cursor = getContentResolver().query(imageUri,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imageEncoded = cursor.getString(columnIndex);
                cursor.close();


                mThumbUris.add(imageUri);
                //tryReloadAndDetectInImage();
            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mThumbUris.add(uri);
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imageEncoded  = cursor.getString(columnIndex);
                        imagesEncodedList.add(imageEncoded);
                        cursor.close();
                    }
                    Log.v("LOG_TAG", "Selected Images" + mThumbUris.size());
                }
            }
            gotoGallery();
        }
    }


    private void gotoGallery() {

        Intent myIntent = new Intent(LaunchActivity.this, MainActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        LaunchActivity.this.startActivity(myIntent);
    }

}
