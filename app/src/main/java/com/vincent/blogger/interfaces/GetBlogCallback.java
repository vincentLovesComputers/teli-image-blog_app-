package com.vincent.blogger.interfaces;

import com.vincent.blogger.models.BlogPost;

public interface GetBlogCallback {
    void onCallBack(BlogPost blogPost);
}
