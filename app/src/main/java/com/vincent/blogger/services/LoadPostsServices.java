package com.vincent.blogger.services;


import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vincent.blogger.interfaces.GetBlogCallback;
import com.vincent.blogger.models.BlogPost;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LoadPostsServices {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userCollection = "Users";
    private String postsCollection = "Posts";
    private String currentUser = auth.getCurrentUser().toString();
    private DocumentSnapshot lastVisible;
    private List<BlogPost> blogPostList = new ArrayList<>();
    private Boolean isFirstLoaded = true;


    public void loadFirstPosts(final String category, Activity activity, final GetBlogCallback getBlogCallback){
        Query firstQuery;
        if(category.equals("latest")){
             firstQuery = firestore
                    .collection(postsCollection)
                    .orderBy("timeStamp", Query.Direction.DESCENDING);
        }else{
             firstQuery = firestore
                    .collection(postsCollection)
                    .whereEqualTo("category", category)
                    .orderBy("timeStamp", Query.Direction.DESCENDING);
        }

        firstQuery.addSnapshotListener(activity,
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                        if(querySnapshot!=null){
                            if(!querySnapshot.isEmpty()){
//                                if(isFirstLoaded){
//                                    lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
//
//                                }
                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                                for(DocumentChange doc_change: querySnapshot.getDocumentChanges()){

                                    if(doc_change.getType() == DocumentChange.Type.ADDED){
                                        Log.d(TAG, "All : Load first posts" + category);
                                        String blogPostId = doc_change.getDocument().getId();
                                        BlogPost blogPost = doc_change.getDocument().toObject(BlogPost.class).withId(blogPostId);
                                        getBlogCallback.onCallBack(blogPost);
                                    }

                                }
                                isFirstLoaded = false;

                            }else{
                                Log.d(TAG, "Getting first qeury: empty" + category);
                            }
                        }else{
                            Log.d(TAG, "Query snapshot is null");
                        }
                    }
                }
        );
    }


    public  void loadMorePosts(String category, Activity activity, final GetBlogCallback getBlogCallback){
        Query lastQuery;
        if(category.equals("latest")){
            lastQuery = firestore
                    .collection(postsCollection)
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible);
        }else{
            lastQuery = firestore
                    .collection(postsCollection)
                    .whereEqualTo("category", category)
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible);
        }

        lastQuery.addSnapshotListener(activity,
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                        if(querySnapshot != null){
                            if(!querySnapshot.isEmpty()){

                                lastVisible = querySnapshot.getDocuments().get(querySnapshot.size()  - 1);

                                for(DocumentChange doc_change: querySnapshot.getDocumentChanges()){
                                    if(doc_change.getType()== DocumentChange.Type.ADDED){
                                        Log.d(TAG, "All : Load  more posts");
                                        BlogPost blogPost = doc_change.getDocument().toObject(BlogPost.class);
                                        getBlogCallback.onCallBack(blogPost);
                                    }
                                }

                            }

                        }else{
                            Log.d(TAG, "Querysnapshot is null");
                        }
                    }
                }
        );

    }
}
