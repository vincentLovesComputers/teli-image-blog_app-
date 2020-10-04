package com.vincent.blogger.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;


public class User {

    String user_name;
    String image;
    String user_id;
    String bio;
    Date timeStamp;
    HashMap<String, String> chosen_interests;

    User(String user_id, String userName, String image, String bio, Date date_joined,HashMap<String, String> interests){
        this.image = image;
        this.user_name = userName;
        this.user_id = user_id;
        this.bio = bio;
        this.timeStamp = date_joined;
        this.chosen_interests = interests;

    }

    User(){

    }

    public HashMap<String, String> getChosen_interests() {
        return chosen_interests;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getBio() {
        return bio;
    }

    public String getImage() {
        return image;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_id() {
        return user_id;
    }
}
