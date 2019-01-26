package com.example.android.sheinnovates;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.List;

public class LaunchActivity extends AppCompatActivity {
    private static final int REQUEST_CHOOSE_IMAGE = 1002;
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
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it
            imageUri = data.getData();
            mThumbUris.add(imageUri);
            tryReloadAndDetectInImage();
        }
    }
    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        return new Pair<>(480, 640);
    }
    private void tryReloadAndDetectInImage() {
        try {
            if (imageUri == null) {
                return;
            }
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Intent myIntent = new Intent(LaunchActivity.this, MainActivity.class);
//                myIntent.putExtra("key", value); //Optional parameters
            LaunchActivity.this.startActivity(myIntent);

        } catch (IOException e) {
            Log.e(TAG, "Error retrieving saved image");
        }
    }

}
