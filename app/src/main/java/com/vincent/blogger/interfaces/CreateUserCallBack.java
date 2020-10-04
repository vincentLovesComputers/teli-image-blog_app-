package com.vincent.blogger.interfaces;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public interface CreateUserCallBack {
    void onUserCallBack(Task<Void> task);
    void onUserImageCallBack(Task<Uri> task);
}
