package com.example.servseek.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.servseek.R;
import com.example.servseek.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {

    private List<String> imageUrls = new ArrayList<>();
    private LayoutInflater mInflater;
    private Consumer<Integer> onImageClick;
    private int MAX_ITEMS = 50; // Example limit to the number of items

    public PortfolioAdapter(Context context, Consumer<Integer> onImageClick) {
        this.mInflater = LayoutInflater.from(context);
        this.onImageClick = onImageClick;
        addPlaceholder(); // Initially add a placeholder if the list is empty
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_portfolio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Load image using your preferred image loading library (e.g., Glide, Picasso)
            // Example using Glide:
            Glide.with(holder.imageView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.baseline_add_24) // Placeholder image while loading
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.baseline_add_24); // Set a placeholder image
        }
        holder.imageView.setOnClickListener(v -> onImageClick.accept(position)); // Handle image click
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void addImageUri(String imageUrl) {
        if (imageUrls.size() < MAX_ITEMS) {
            imageUrls.add(imageUrl);
            notifyDataSetChanged();
            // Update Firebase Database or Firestore with the new image URL
            FirebaseUtil.updatePortfolioImageUrl(imageUrl);
        }
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        notifyDataSetChanged();
    }

    public void addImageUrl(String imageUrl) {
        if (imageUrls.size() < MAX_ITEMS) {
            imageUrls.add(imageUrl);
            notifyDataSetChanged();
        }
    }

    private void addPlaceholder() {
        if (imageUrls.isEmpty() || (imageUrls.get(imageUrls.size() - 1) != null && imageUrls.size() < MAX_ITEMS)) {
            imageUrls.add(null); // Add a null as a placeholder for the add icon
            notifyItemInserted(imageUrls.size() - 1);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewPortfolioItem);
        }
    }
}
