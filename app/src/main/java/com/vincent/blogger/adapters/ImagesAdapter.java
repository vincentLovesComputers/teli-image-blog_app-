package com.vincent.blogger.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.vincent.blogger.R;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private List<Uri> imagesList;
    private Context mContext;
    private ShapeableImageView imageView;


    public ImagesAdapter(Context context, List<Uri> imagesList){
        this.imagesList = imagesList;
        this.mContext = context;
    }


    @Override
    public ImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.images_recycler, parent, false);
        return new ImagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesAdapter.ViewHolder holder, int position) {
        Uri uri = imagesList.get(position);
        holder.setImage(uri);

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
        return imagesList.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View view){
            super(view);
            initViews(view);
        }

        public void setImage(Uri imageUri){
            imageView.setImageURI(imageUri);

        }

    }

    public void initViews(View view){
        imageView = view.findViewById(R.id.recycler_image_item);

    }
}
