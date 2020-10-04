package com.vincent.blogger.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vincent.blogger.interfaces.SetBlogLikesInterface;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SetLikesService {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String postsCollection = "Posts";
    private String likesCollection = "Likes";
    private String currentUser = auth.getUid();
    private Context mContext;

    public SetLikesService(Context context){
        this.mContext = context;
    }

    public void setLikesCollection(final String desc, final String blogPostId, final SetBlogLikesInterface blogLikesInterface){
        //set likes
        firestore
                .collection(postsCollection + "/" + blogPostId +  "/" + likesCollection)
                .document(currentUser)
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot !=null){
                                    if(!documentSnapshot.exists()){
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("timeStamp", FieldValue.serverTimestamp());
                                        data.put("liked_user_id", currentUser);
                                        data.put("description_of_post", desc);
                                        data.put("is_viewed", false);

                                        firestore
                                                .collection("Posts/" + blogPostId +  "/Likes")
                                                .document(currentUser)
                                                .set(data)
                                                .addOnSuccessListener(
                                                        new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                blogLikesInterface.setLikes(aVoid);
                                                            }
                                                        }
                                                ).addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                        );
                                    }else{
                                        firestore
                                                .collection(postsCollection + "/" + blogPostId +  "/" + likesCollection)
                                                .document(currentUser)
                                                .delete()
                                                .addOnSuccessListener(
                                                        new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                blogLikesInterface.deleteLike(aVoid);
                                                            }
                                                        }
                                                ).addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                        );
                                    }
                                }else{
                                    Log.d(TAG, "Document is null");
                                }
                            }
                        }
                ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }
}
