package com.vincent.blogger.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.vincent.blogger.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ExploreImagesAdapter extends RecyclerView.Adapter<ExploreImagesAdapter.ViewHolder> {

    private ArrayList<String> downloadedImagesList;
    private Context mContext;
    private ShapeableImageView imageView;


    public ExploreImagesAdapter(Context context, ArrayList<String> downloadedImagesList){
        this.mContext = context;
        this.downloadedImagesList = downloadedImagesList;
    }


    @Override
    public ExploreImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.explore_images_recycler, parent, false);
        return new ExploreImagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreImagesAdapter.ViewHolder holder, int position) {
        String image = downloadedImagesList.get(position);
        Log.d(TAG, "Explore images adapter: " + position + "-->" + image);
        holder.setExploreImages(image);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return downloadedImagesList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View view){
            super(view);
            initViews(view);
        }

        public void setImage(Uri imageUri){
            imageView.setImageURI(imageUri);

        }

        public void setExploreImages(String image){
            RequestOptions reqOpt = new RequestOptions();
            reqOpt = reqOpt.placeholder(mContext.getDrawable(R.drawable.add_image_icon));
            Glide.with(mContext).setDefaultRequestOptions(reqOpt).load(image).into(imageView);
        }

    }

    public void initViews(View view){
        imageView = view.findViewById(R.id.explore_recycler_image_item);

    }
}
