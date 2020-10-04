package com.vincent.blogger.models;

public class BlogPostId{
    public String BlogPostId;

    //classes can extend this method
    public <T extends BlogPostId> T withId(String id){
        this.BlogPostId = id;
        return (T) this;
    }
}
