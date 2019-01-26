package com.example.android.sheinnovates;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int position = getIntent().getIntExtra("position", -1);
        Uri uri = LaunchActivity.mThumbUris.get(position);
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        imageView.setImageURI(uri);
    }
}
