package com.vincent.blogger.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.vincent.blogger.R;
import com.vincent.blogger.models.Interests;
import com.vincent.blogger.ui.Setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class AccountInterestsAdapter extends RecyclerView.Adapter<AccountInterestsAdapter.ViewHolder> {

    private List<String>  interestsList;

    private Context mContext;
    private ImageView imageView;
    private ImageView checkMark;
    private TextView interestNameView;
    private MaterialCardView interestCard;
    private Setup mSetup;

    public AccountInterestsAdapter(Context context, List<String> interestsList) {
        this.mContext = context;
        this.interestsList = interestsList;
    }

    @NonNull
    @Override
    public AccountInterestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.interests_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountInterestsAdapter.ViewHolder holder, int position) {

        String item = interestsList.get(position);

        String[] splitItem = item.split(":", 2);
        String name = splitItem[0];
        String image = splitItem[1];

        holder.setInterests(mContext, name, image);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return interestsList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        ViewHolder(View view) {
            super(view);
            mView = view;
        }

        @SuppressLint("ClickableViewAccessibility")
        public void setInterests(final Context context, final String name, final String image) {
            imageView = mView.findViewById(R.id.int_img);
            interestNameView = mView.findViewById(R.id.int_name);
            interestCard = mView.findViewById(R.id.interest_card);

            interestNameView.setText(name);

            RequestOptions reqOpt = new RequestOptions();
            reqOpt.placeholder(R.drawable.add_image_icon);
            Glide.with(context).setDefaultRequestOptions(reqOpt).load(image).into(imageView);


        }



    }
}
