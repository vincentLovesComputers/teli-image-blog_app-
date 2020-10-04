package com.vincent.blogger.models;

public class Interests {
    String name;
    String image;

    Interests(String name, String image){
        this.name = name;
        this.image = image;
    }

    Interests(){

    }

    public void pickInterest(){

    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
