package com.vincent.blogger.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vincent.blogger.interfaces.UsersNotificationInterface;
import com.vincent.blogger.models.Notification;
import com.vincent.blogger.models.User;

import static android.content.ContentValues.TAG;

public class UsersNotificationService {

    private String userCollection = "Users";
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String currentUser = auth.getUid();
    private Context mContext;


    public UsersNotificationService(Context context){
        this.mContext = context;
    }


    public void getUserData(String userId, final UsersNotificationInterface notificationInterface){
        firestore
                .collection(userCollection)
                .document(userId)
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot !=null){
                                    User user = documentSnapshot.toObject(User.class);
                                    notificationInterface.getUser(user);
                                }else{
                                    Log.d(TAG, "Get user notification service: Document is null");
                                }
                            }
                        }
                ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext , e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }



}
