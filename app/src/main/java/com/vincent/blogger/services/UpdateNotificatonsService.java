package com.vincent.blogger.services;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vincent.blogger.interfaces.NotificationsInterface;
import com.vincent.blogger.interfaces.UpdateNotificationsInterface;

public class UpdateNotificatonsService {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String currentUser = auth.getUid();
    private Context mContext;
    private int count = 0;


    public UpdateNotificatonsService(Context context){
        this.mContext = context;
    }

    public void updateNotifications(final UpdateNotificationsInterface updateNotificationsInterface){
        firestore
                .collection("Posts")
                .whereEqualTo("user_id", currentUser)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                if (querySnapshot != null) {

                                    for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {
                                        documentChange.getDocument()
                                                .getReference()
                                                .collection("Likes")
                                                .addSnapshotListener(
                                                        new EventListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                                                                if(querySnapshot != null){
                                                                    for(DocumentChange doc_change: querySnapshot.getDocumentChanges()){
                                                                        doc_change
                                                                                .getDocument()
                                                                                .getReference()
                                                                                .update("is_viewed", true)
                                                                                .addOnCompleteListener(
                                                                                        new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if(task.isSuccessful()){
                                                                                                    count = 0;
                                                                                                    updateNotificationsInterface.updateNotifications(count);
                                                                                                }else{
                                                                                                    Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                );
                                                                    }
                                                                }
                                                            }
                                                        }
                                                );

                                    }
                                }
                            }
                        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }
}
