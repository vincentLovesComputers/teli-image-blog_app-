package com.vincent.blogger.interfaces;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public interface BlogPostInterface {
    void getUserData(Task<DocumentSnapshot> task);
    void getLikes(DocumentSnapshot doc_snap);
    void getLikesCount(QuerySnapshot querySnapshot);


}
