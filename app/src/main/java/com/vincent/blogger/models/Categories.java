package com.vincent.blogger.models;
import android.graphics.Color;

import com.vincent.blogger.R;

public class Categories {
    private String title;
    private int image;

    public Categories(String title, int image){
        this.title = title;
        this.image = image;
    }

    public static final Categories[] categoriesList = {
            new Categories("All", R.drawable.all),
            new Categories("Gym", R.drawable.gym),
            new Categories("Nature", R.drawable.nature),
            new Categories("Sports", R.drawable.sports),
            new Categories("Automobile", R.drawable.automobile),
            new Categories("Fashion", R.drawable.fashion),
            new Categories("Science", R.drawable.science),
            new Categories("Food", R.drawable.food),
            new Categories("Beauty", R.drawable.beauty),

        };

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }
}
