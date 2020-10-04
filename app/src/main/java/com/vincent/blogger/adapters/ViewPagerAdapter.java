package com.vincent.blogger.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.card.MaterialCardView;
import com.vincent.blogger.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();
    private Context mContext;

    public ViewPagerAdapter(Context context, FragmentManager fm, int behaviour){
        super(fm, behaviour);
        this.mContext = context;

    }

    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        titles.add(title);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        Drawable image = mContext.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString("  ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;

    }

    public View getTabView(int position){
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_tab_view, null);
        TextView titleView =  view.findViewById(R.id.categories_title);
        titleView.setText(titles.get(position));
        ImageView categoryView =  view.findViewById(R.id.categories_img);
        categoryView.setImageResource(imageResId[position]);

        return view;
    }

    private int[] imageResId = {
            R.drawable.all,
            R.drawable.fashion,
            R.drawable.nature,
            R.drawable.sports,
            R.drawable.gym,
            R.drawable.science,
            R.drawable.food,
            R.drawable.automobile,
            R.drawable.beauty,



    };


}
