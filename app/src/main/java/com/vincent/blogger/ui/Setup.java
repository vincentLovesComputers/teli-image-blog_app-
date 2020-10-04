package com.vincent.blogger.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vincent.blogger.R;
import com.vincent.blogger.SpacesItemDecorator;
import com.vincent.blogger.adapters.InterestsAdapter;
import com.vincent.blogger.models.Interests;
import com.vincent.blogger.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setup extends AppCompatActivity {

    private static final String TAG = "Setup";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String REASON_VISITING_PAGE = "reason";

    private TextInputEditText nameView;
    private TextInputLayout nameViewLayout;
    private CircleImageView profImage;
    private RecyclerView recyclerView;
    private InterestsAdapter interestsAdapter;
    private TextInputLayout bioLayoutView;
    private TextInputEditText bioView;

    private Button saveBtn;
    private ProgressBar progressBar;
    private Uri mainImageUri = null;

    private String user_id;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private String newImageUri;
    boolean isChanged = false;
    private String email;
    private String password;
    private String reasonVisitingPage;
    private List<Interests> interestsList;
    public HashMap<String, String> chosenInterests =new HashMap<>(); ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        interestsList = new ArrayList<>();
        initFirebase();
        initViews();
        initRecyclerAndAdapter();
        getIntentData();
        if(reasonVisitingPage.matches("edit")){
            getUserData();
        }
        profileSetup();
        setUpInterests();
        saveProfile();
    }


    //method to override device onActivity to get crop result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                profImage.setImageURI(mainImageUri);
                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void getUserData(){
        progressBar.setVisibility(View.VISIBLE);
        firestore
                .collection("Users")
                .document(user_id)
                .get()
                .addOnSuccessListener(Setup.this,
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot !=null){
                                    if(documentSnapshot.exists()){
                                        User user = documentSnapshot.toObject(User.class);
                                        if(user!=null){
                                            nameView.setText(user.getUser_name());
                                            bioView.setText(user.getBio());
                                            newImageUri = user.getImage();
                                            mainImageUri = Uri.parse(newImageUri);
                                            RequestOptions reqOpt = new RequestOptions();
                                            reqOpt.placeholder(R.drawable.profile_icon);
                                            Glide.with(Setup.this).setDefaultRequestOptions(reqOpt).load(newImageUri).into(profImage);
                                        }else{
                                            Log.d(TAG, "User does not exist");
                                        }
                                    }else{
                                        Log.d(TAG, "Document does not exist");
                                    }
                                }else{
                                    Log.d(TAG, "Document is null");
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Setup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );

    }


    private void setUpInterests(){
        progressBar.setVisibility(View.VISIBLE);
        firestore
                .collection("Interests")
                .get()
                .addOnSuccessListener(Setup.this,
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                if(querySnapshot !=null){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    if(!querySnapshot.getDocuments().isEmpty()){

                                        for(DocumentChange doc_changed: querySnapshot.getDocumentChanges()){
                                            if(doc_changed.getType() == DocumentChange.Type.ADDED){
                                                Interests interests = doc_changed.getDocument().toObject(Interests.class);
                                                interestsList.add(interests);
                                                interestsAdapter.notifyDataSetChanged();
                                            }
                                        }


                                    }else{
                                        Log.d(TAG, "query SNAPSHOT IS empty");
                                    }
                                }else{
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Log.d(TAG, "query SNAPSHOT IS null");
                                }
                            }
                        }
                );

    }


    private void initRecyclerAndAdapter(){

        interestsAdapter = new InterestsAdapter(this, interestsList, this);
        recyclerView = findViewById(R.id.setup_recycler);
        GridLayoutManager gm = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gm);
        recyclerView.setAdapter(interestsAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecorator(10));
    }

    private void getIntentData(){
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        reasonVisitingPage = intent.getStringExtra("reason");
    }


    private  void initFirebase(){

        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        user_id = FirebaseAuth.getInstance().getUid();
    }

    private void saveProfile() {
        saveBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = nameView.getText().toString();
                        String bio = bioView.getText().toString();

                        if(!name.matches("") && !chosenInterests.isEmpty() && !bio.isEmpty()){
                            redToSplashScreen(name,bio, mainImageUri);
                        }else{
                            if(name.matches("")){
                                nameViewLayout.setError("Enter a username");
                            }
                            if(bio.matches("")){
                                bioLayoutView.setError("Enter bio(Keep it simple)");
                            }
                            if(chosenInterests.isEmpty()){
                                Toast.makeText(Setup.this, "choose at-least 1 interest", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }
        );
    }

    private void redToSplashScreen(String name, String bio, Uri imageUri) {
        Intent intent = new Intent(Setup.this, ProfileSplashScreen.class);
        if(reasonVisitingPage.matches("new_user")){
            intent.putExtra(ProfileSplashScreen.EMAIL, email);
            intent.putExtra(ProfileSplashScreen.PASSWORD, password);
        }

        intent.putExtra(ProfileSplashScreen.NAME, name);
        intent.putExtra(ProfileSplashScreen.BIO, bio);
        intent.putExtra(ProfileSplashScreen.MAIN_IMAGE_URI, imageUri);
        intent.putExtra(ProfileSplashScreen.IS_CHANGED, isChanged);
        intent.putExtra(ProfileSplashScreen.CHOSEN_INTERESTS, chosenInterests);
        intent.putExtra(ProfileSplashScreen.REASON_VISITING_PAGE, reasonVisitingPage);
        startActivity(intent);
    }

    private void profileSetup() {
        profImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(Setup.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                //permission not granted
                                //request permission
                                ActivityCompat.requestPermissions(Setup.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                if (ContextCompat.checkSelfPermission(Setup.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    imagePicker();

                                } else {
                                    imagePicker();
                                    Log.d(TAG, "User denied request to pick picture");
                                }
                            } else {
                                //permission already given
                                imagePicker();
                            }
                        } else {
                            imagePicker();

                        }
                    }
                }
        );
    }

    private void imagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Setup.this);
    }

    private void initViews() {
        nameView = findViewById(R.id.setup_prof_name);
        saveBtn = findViewById(R.id.setup_save_btn);
        profImage = findViewById(R.id.profile_pic);
        progressBar = findViewById(R.id.setup_progress_bar);
        nameViewLayout = findViewById(R.id.setup_prof_name_layout);
        bioLayoutView = findViewById(R.id.setup_prof_bio_layout);
        bioView = findViewById(R.id.setup_prof_bio);

    }


}
