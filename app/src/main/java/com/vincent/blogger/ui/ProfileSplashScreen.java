package com.vincent.blogger.ui;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vincent.blogger.MainActivity;
import com.vincent.blogger.R;
import com.vincent.blogger.ui.authentication.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileSplashScreen extends AppCompatActivity {


    public static final String TAG = "SignUpSplash";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";
    public static final String BIO = "bio";
    public static final String MAIN_IMAGE_URI = "mainImageUri";
    public static final String IS_CHANGED = "isChanged";
    public static final String CHOSEN_INTERESTS = "chosenInterests";
    public static final String REASON_VISITING_PAGE = "reasonVisitingPage";



    private String email;
    private String pwd;
    private String name;
    private String bio;
    private boolean isChanged;
    private Uri mainImageUri;
    private HashMap<String, String> chosenInterestsMap;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String currentUser;
    private StorageReference storageRef;

    private TextView loadingUpdate;
    private String getReasonVisitingPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_splash_screen);
        initFirebase();
        getDataFromIntent();
        initViews();

        if(getReasonVisitingPage.matches("new_user")){
            signUpUser();
        }else if(getReasonVisitingPage.matches("edit")){
            updateUserData();
        }

    }

    private void updateUserData(){
        currentUser = auth.getUid();
        loadingUpdate.setText("Updating data");
        if(currentUser != null){
                if(mainImageUri != null){
                    if(isChanged){
                        saveImage();
                    }else{
                        saveFirestore(null, name);
                    }
                }else{
                    Toast.makeText(this, "image uri null", Toast.LENGTH_SHORT).show();
                }
        }else{
            Toast.makeText(this, "current user null", Toast.LENGTH_SHORT).show();
        }

    }

    private void signUpUser(){
        auth.createUserWithEmailAndPassword(email, pwd).addOnSuccessListener(
                new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        loadingUpdate.setTextColor(getResources().getColor(R.color.colorPrimary));
                        loadingUpdate.setText("Preparing account");
                        currentUser = auth.getUid();
                        if(currentUser != null){
                            if(mainImageUri!=null){
                                saveImage();
                            }else{

                                saveFirestore(null, name);
                            }

                        }


                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingUpdate.setTextColor(getResources().getColor(R.color.red1));
                        loadingUpdate.setText("Error creating account");
                        redToSignUp();
                    }
                }
        );

    }

    private void saveImage(){
        loadingUpdate.setText("Saving profile image");

        String randomVal = UUID.randomUUID().toString();

        StorageReference imagePath =  storageRef.child("profile_img").child(randomVal + ".jpg");
        imagePath.putFile(mainImageUri).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if(taskSnapshot.getMetadata() != null){
                            if(taskSnapshot.getMetadata().getReference() != null){
                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                        new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if(task.isSuccessful()){
                                                    loadingUpdate.setText(task.getResult().toString());

                                                    saveFirestore(task, name);
                                                }else{
                                                    Toast.makeText(ProfileSplashScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    private void saveFirestore(Task<Uri> task, String name){

        Uri imageUri;
        if(task!=null){
            imageUri = task.getResult();
        }else{
            imageUri = mainImageUri;
        }

        if(imageUri == null){
            Log.d(TAG, "Image uri is null");
            Toast.makeText(this, "Error updating. Try again", Toast.LENGTH_SHORT).show();
        }else{
            commitToDb(name, imageUri);
        }
    }

    private void commitToDb(String name, Uri imageUri){
        Map<String, Object> data = new HashMap<>();
        data.put("user_name", name);
        data.put("image", imageUri.toString());
        data.put("user_id", currentUser);
        data.put("bio", bio);
        data.put("chosen_interests", chosenInterestsMap);
        loadingUpdate.setText("Saving user data");
        if(getReasonVisitingPage.matches("new_user")){
            data.put("timeStamp", FieldValue.serverTimestamp());
            firestore.collection("Users").document(currentUser).set(data).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ProfileSplashScreen.this, "profile updated", Toast.LENGTH_SHORT).show();
                                redToHome();
                            }else{

                                String error = task.getException().getMessage();
                                Toast.makeText(ProfileSplashScreen.this, error, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
            );
        }else if(getReasonVisitingPage.matches("edit")){
            firestore.collection("Users").document(currentUser).update(data).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ProfileSplashScreen.this, "profile updated", Toast.LENGTH_SHORT).show();
                                redToHome();
                            }else{

                                String error = task.getException().getMessage();
                                Toast.makeText(ProfileSplashScreen.this, error, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
            );
        }
    }

    public void redToSignUp(){
        Intent intent = new Intent(ProfileSplashScreen.this, Register.class);
        startActivity(intent);
        finish();
    }


    private void redToHome(){
        Intent intent = new Intent(ProfileSplashScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void initFirebase(){
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        storageRef  = FirebaseStorage.getInstance().getReference();
        }

    public void initViews()
    {
        loadingUpdate = findViewById(R.id.loading_update_txt);
    }

    private void getDataFromIntent(){
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        pwd = intent.getStringExtra("password");
        name = intent.getStringExtra("name");
        isChanged = intent.getBooleanExtra("isChanged", false);
        mainImageUri = intent.getParcelableExtra("mainImageUri");
        chosenInterestsMap = (HashMap<String, String>)intent.getSerializableExtra("chosenInterests");
        bio = intent.getStringExtra("bio");
        getReasonVisitingPage = intent.getStringExtra("reasonVisitingPage");

    }


}