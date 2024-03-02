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
    private ArrayList<Uri> imageUris; // This will hold your image URIs

    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;
        imageUris = new ArrayList<>(); // Initialize the ArrayList
    }

    // Returns the number of items in the adapter
    public int getCount() {
        return imageUris.size();
    }

    // Returns the item at the specified position in the adapter
    public Object getItem(int position) {
        return imageUris.get(position);
    }

    // Returns the row ID of the item at the specified position
    public long getItemId(int position) {
        return 0;
    }

    // Returns the view for each item in the adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // If it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            int size = (int) (200* mContext.getResources().getDisplayMetrics().density); // 50dp in pixels
            imageView.setLayoutParams(new GridView.LayoutParams(size, size)); // Make the image view square
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageURI(imageUris.get(position));
        return imageView;
    }

    // Adds an image URI to the adapter and refreshes the GridView
    public void addImage(Uri uri) {
        imageUris.add(uri);
        notifyDataSetChanged(); // This will update the GridView
    }
}
