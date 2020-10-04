package com.vincent.blogger.models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

public class BlogPost extends BlogPostId{

    ArrayList<String> image;
     String cover_image, thumb, topic, description, user_id, category;
    Date timeStamp;


    BlogPost(){

    }

    BlogPost(String cover_image, ArrayList<String> images, String thumb, String topic, String description, String category,  String user_id, Date timeStamp){
        this.image = images;
        this.thumb = thumb;
        this.topic = topic;
        this.description = description;
        this.user_id = user_id;
        this.timeStamp = timeStamp;
        this.category = category;
        this.cover_image = cover_image;

    }

    public String getCover_image() {
        return cover_image;
    }

    public String getThumb() {
        return thumb;
    }

    public String getDescription(){
        return description;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public String getUser_id(){
        return user_id;
    }
    public Date getTimeStamp(){
        return timeStamp;
    }

    public String getTopic() {
        return topic;
    }

    public String getCategory() {
        return category;
    }
}
