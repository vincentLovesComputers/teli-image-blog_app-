package com.vincent.blogger.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vincent.blogger.interfaces.NotificationsInterface;
import com.vincent.blogger.models.Notification;

import static android.content.ContentValues.TAG;

public class NotificationsService {

    private String userCollection = "Users";
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String currentUser = auth.getUid();
    private Context mContext;
    private int count = 0;


    public NotificationsService(Context context){
        this.mContext = context;
    }

    public void getNotificationsInfo(final NotificationsInterface notificationsInterface){
        //get notifications
        firestore
                .collection("Posts")
                .whereEqualTo("user_id", currentUser)
                .orderBy("timeStamp",Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                if(querySnapshot!=null){

                                    for(DocumentChange documentChange: querySnapshot.getDocumentChanges()){
                                        documentChange.getDocument()
                                                .getReference()
                                                .collection("Likes")
                                                .orderBy("timeStamp", Query.Direction.DESCENDING)
                                                .addSnapshotListener(
                                                        new EventListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onEvent(@androidx.annotation.Nullable QuerySnapshot query_snap, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                                                                if(query_snap != null){
                                                                    if(!query_snap.isEmpty()){

                                                                        for(DocumentChange doc_change: query_snap.getDocumentChanges()){
                                                                            if(doc_change.getType() == DocumentChange.Type.ADDED){
                                                                                Notification notification = doc_change.getDocument().toObject(Notification.class);
                                                                                notificationsInterface.getNotifications(notification);

                                                                                if(!notification.getIs_viewed()){
                                                                                    //notification is viewed
                                                                                    count = count + 1;
                                                                                }
                                                                            }
                                                                        }


                                                                    }else{
                                                                        Log.d(TAG, "Likes collections: empty");
                                                                    }
                                                                    notificationsInterface.getCountOfLikes(count);
                                                                }else{
                                                                    Log.d(TAG, "Likes collections: null");
                                                                }
                                                            }
                                                        }
                                                );
                                    }

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
