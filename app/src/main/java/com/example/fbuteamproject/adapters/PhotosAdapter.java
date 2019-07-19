package com.example.fbuteamproject.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.example.fbuteamproject.R;
import com.example.fbuteamproject.models.Photo;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private Photo[] mPhotos;
    Context context;

    public PhotosAdapter(Context context, Photo[] photos) {
        this.context = context;
        this.mPhotos = photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);

        return new ViewHolder(view);
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PhotosAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Photo photo = mPhotos[position];

        // Set item views based on your views and data model
        ImageView ivPhoto = holder.ivPhoto;


        Glide.with(context)
                .load(R.drawable.earth)
                .into(ivPhoto);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPhotos.length;
    }


    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPhoto;


        public ViewHolder(View itemView) {
            super (itemView);

            // perform findViewById lookups
            ivPhoto = itemView.findViewById(R.id.ivPhoto);

        }

    }


}
