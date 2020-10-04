package com.vincent.blogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vincent.blogger.ui.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class PostSplashScreenActivity extends AppCompatActivity {

    public static final String TAG = "PostSplash";

    public static final String IMAGES_LIST = "imagesList";
    public static final String DESCRIPTION = "description";
    public static final String CATEGORY = "category";

    private ArrayList<Uri> getImagesList;
    private String getDescription;
    private String getCategory;

    private TextView updateTxt;
    private Uri mobileImageChosen;

    private StorageReference storageRef;
    private FirebaseFirestore firestore;
    private String current_user_id;

    private ArrayList<String> dbImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_splash_screen);
        initViews();
        initFirebase();
        getIntentData();
        saveToDb();
    }

    public void saveToDb(){
        updateTxt.setText("Uploading Data");
        final String documentRandomVal = UUID.randomUUID().toString();
        for(int i=0; i< getImagesList.size(); i++){

            mobileImageChosen = getImagesList.get(i);
            final String randomVal = UUID.randomUUID().toString();

            //upload image to storage
            StorageReference imgPath = storageRef.child("blog_images").child(current_user_id).child(randomVal + ".jpg");

            imgPath.putFile(mobileImageChosen).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if(taskSnapshot.getMetadata() !=null){
                                if(taskSnapshot.getMetadata().getReference() !=null){

                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(
                                            new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    if(uri !=null){
                                                        saveToFirestore(uri.toString(), getDescription, getCategory, documentRandomVal);
                                                    }else{
                                                        Log.d(TAG, "Uri is null");
                                                        Toast.makeText(PostSplashScreenActivity.this, "Uri is null", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                    ).addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(PostSplashScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(PostSplashScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }


    private void saveToFirestore(String downloadedUri, String description, String category, final String docRandVal){
        dbImages.add(downloadedUri);

        final Map<String, Object> data = new HashMap<>();
        data.put("description", description);
        data.put("category", category);
        data.put("image", dbImages);
        data.put("user_id", current_user_id);
        data.put("cover_image", dbImages.get(0));
        data.put("timeStamp", FieldValue.serverTimestamp());

        firestore
                .collection("Posts")
                .document(docRandVal)
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    firestore
                                            .collection("Posts")
                                            .document(docRandVal)
                                            .update(data)
                                            .addOnCompleteListener(
                                                    new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Log.d(TAG, "SaveData: Service data successful");
                                                                try {
                                                                    redToHome();
                                                                } catch (InterruptedException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }else{
                                                                String error = task.getException().getMessage();
                                                                Log.d(TAG, "SaveData: Error saving to firestore: " + error);
                                                                updateTxt.setTextColor(getResources().getColor(R.color.red1));
                                                                updateTxt.setText(error);
                                                            }
                                                        }
                                                    }
                                            );
                                }
                                else{
                                    firestore
                                            .collection("Posts")
                                            .document(docRandVal)
                                            .set(data)
                                            .addOnCompleteListener(
                                                    new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Log.d(TAG, "SaveData: Service data successful");
                                                                try {
                                                                    redToHome();
                                                                } catch (InterruptedException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }else{
                                                                String error = task.getException().getMessage();
                                                                updateTxt.setTextColor(getResources().getColor(R.color.red1));
                                                                updateTxt.setText(error);
                                                                Log.d(TAG, "SaveData: Error saving to firestore: " + error);
                                                            }
                                                        }
                                                    }
                                            );
                                }
                            }
                        }
                );

    }

    private void getIntentData(){
        Intent intent = getIntent();
        getImagesList = intent.getParcelableArrayListExtra("imagesList");
        getCategory = intent.getStringExtra("category");
        getDescription = intent.getStringExtra("description");
    }

    private void initViews(){
        updateTxt = findViewById(R.id.post_loading_update_txt);
    }

    private void initFirebase(){
        storageRef = FirebaseStorage.getInstance().getReference();
        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();
    }

    private void redToHome() throws InterruptedException {
        updateTxt.setTextColor(getResources().getColor(R.color.success));
        updateTxt.setText("Uploaded successfully");
        sleep(1000);
        Intent intent = new Intent(PostSplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
    }
}