package com.vincent.blogger.models;

import java.util.Date;

public class Notification {

    private String liked_user_id;
    private Date timeStamp;
    private String description_of_post;
    private Boolean is_viewed;

    Notification(){

    }

    Notification(String liked_user_id, Date timeStamp, String description_of_post, boolean is_viewed){
        this.liked_user_id  = liked_user_id;
        this.timeStamp = timeStamp;
        this.description_of_post = description_of_post;
        this.is_viewed = is_viewed;

    }

    public Boolean getIs_viewed() {
        return is_viewed;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getDescription_of_post() {
        return description_of_post;
    }

    public String getLiked_user_id() {
        return liked_user_id;
    }
}
