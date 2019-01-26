package com.example.android.sheinnovates;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.json.JSONException;

import java.net.URI;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return LaunchActivity.imagesdata.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(365, 365));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }
        Uri imguri = null;
        try {
            imguri = Uri.parse((LaunchActivity.imagesdata.get(position)).getString("uri"));
        } catch(JSONException e) {
            //failed
        }
        if (imguri != null) {
            imageView.setImageURI(imguri);
        }
        //imageView.setImageResource(LaunchActivity.mThumbIds[position]);
        return imageView;
    }


}
