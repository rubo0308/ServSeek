package com.example.servseek.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList; // This line imports the ArrayList class

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Uri> imageUris;

    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;
        imageUris = new ArrayList<>();
    }


    public int getCount() {
        return imageUris.size();
    }


    public Object getItem(int position) {
        return imageUris.get(position);
    }


    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            int size = (int) (200* mContext.getResources().getDisplayMetrics().density);
            imageView.setLayoutParams(new GridView.LayoutParams(size, size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageURI(imageUris.get(position));
        return imageView;
    }


    public void addImage(Uri uri) {
        imageUris.add(uri);
        notifyDataSetChanged();
    }
}
