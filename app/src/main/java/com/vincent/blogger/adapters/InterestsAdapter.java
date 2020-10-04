package com.vincent.blogger.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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


public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.ViewHolder> {

    private List<Interests> interestsList;
    private final Map<String, String> chosenInterestData = new HashMap<>();

    private Context mContext;
    private ImageView imageView;
    private TextView interestNameView;
    private MaterialCardView interestCard;
    private Setup mSetup;

    public InterestsAdapter(Context context, List<Interests> interestsList, Setup setup) {
        this.mContext = context;
        this.interestsList = interestsList;
        this.mSetup = setup;
    }

    @NonNull
    @Override
    public InterestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.interests_recycler, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestsAdapter.ViewHolder holder, int position) {
        String interestName = interestsList.get(position).getName();
        String interestImg = interestsList.get(position).getImage();

        holder.setInterests(mContext, interestName, interestImg, position);




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

        private ArrayList<Map<String, String>> chosenIntrest;
        private View mView;
        private boolean clicked;

        ViewHolder(View view) {
            super(view);
            mView = view;
        }

        @SuppressLint("ClickableViewAccessibility")
        public void setInterests(final Context context, final String name, final String image, int position) {
            chosenIntrest = new ArrayList<Map<String, String>>();

            imageView = mView.findViewById(R.id.int_img);
            interestNameView = mView.findViewById(R.id.int_name);
            interestCard = mView.findViewById(R.id.interest_card);

            RequestOptions reqOpt = new RequestOptions();
            reqOpt.placeholder(R.drawable.add_image_icon);
            Glide.with(context).setDefaultRequestOptions(reqOpt).load(image).into(imageView);

            interestNameView.setText(name);
            chosenIntrest.add(chosenInterestData);

            imageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

//                            myMap.put("Vincent", "Masuku");
//                            myMap.put("Phumlani", "Masuku");
//                            myList.add(myMap);

                            for(int i=0; i<chosenIntrest.size(); i++){
                                String keyToBeChecked = name;

                                // Get the iterator over the HashMap
                                Iterator<Map.Entry<String, String> >
                                        iterator = chosenInterestData.entrySet().iterator();

                                // flag to store result
                                boolean isKeyPresent = false;

                                while (iterator.hasNext()) {

                                    // Get the entry at this iteration
                                    Map.Entry<String, String>
                                            entry
                                            = iterator.next();

                                    // Check if this key is the required key
                                    if (keyToBeChecked == entry.getKey()) {
                                        isKeyPresent = true;
                                    }
                                }

                                if(isKeyPresent){
                                    chosenInterestData.remove(name);
                                    v.setAlpha((float) 1.0);
                                }else{
                                    chosenInterestData.put(name, image);
                                    v.setAlpha((float) 0.6);
                                }
                            }

                            for(int i=0; i<chosenIntrest.size(); i++){
                                for (Map.Entry<String, String> entry : chosenIntrest.get(i).entrySet()){
                                    mSetup.chosenInterests.put(entry.getKey(), entry.getValue());
                                }

                            }

                        }
                    });





        }

    }
}
