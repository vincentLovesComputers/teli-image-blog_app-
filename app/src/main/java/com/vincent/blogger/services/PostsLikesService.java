package com.vincent.blogger.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.vincent.blogger.interfaces.BlogPostInterface;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class PostsLikesService {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String postsCollection = "Posts";
    private String likesCollection = "Likes";
    private String currentUser = auth.getUid();

    private Context mContext;

    public PostsLikesService(Context context){
        this.mContext = context;

    }

    public void getLikesData(final String desc, String user_id, final String blogPostId, final BlogPostInterface blogPostInterface){

        firestore
                .collection("Users")
                .document(user_id)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                blogPostInterface.getUserData(task);
                            }
                        }
                );

        //get likes
        firestore
                .collection(postsCollection + "/" + blogPostId +  "/" + likesCollection)
                .document(currentUser)
                .addSnapshotListener(
                        new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot doc_snap, @Nullable FirebaseFirestoreException error) {
                                if(doc_snap != null){
                                    blogPostInterface.getLikes(doc_snap);
                                }else{
                                    Log.d(TAG, "Document is null");
                                }
                            }
                        }
                );

        //get likes count
        firestore
                .collection(postsCollection + "/" + blogPostId +  "/" + likesCollection)
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {

                                if(querySnapshot !=null){
                                    blogPostInterface.getLikesCount(querySnapshot);

                                }else{
                                    Log.d(TAG, "Get likes count: " + "Query snapshot is null");
                                }

                            }
                        }
                );

        //get user data



    }
}


