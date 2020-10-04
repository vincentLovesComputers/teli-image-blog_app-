package com.vincent.blogger.ui;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vincent.blogger.R;
import com.vincent.blogger.SpacesItemDecorator;
import com.vincent.blogger.adapters.ExploreImagesAdapter;
import com.vincent.blogger.models.User;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class ExploreBlogActivity extends AppCompatActivity {
    public static final String TAG = "ExploreBlog";


    public static final String DESCRIPTION =  "description";
    public static final String DATE =  "date";
    public static final String COVER_IMAGE =  "coverImage";
    public static final String IMAGES =  "images";
    public static final String USER_ID =  "userId";

    private RecyclerView recyclerView;
    private ExploreImagesAdapter imagesAdapter;
    private TextView descriptionView;
    private ImageView coverImageView;
    private CircleImageView profilePicView;
    private TextView userNameView;
    private TextView dateView;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    private String description;
    private String date;
    private String coverImage;
    private ArrayList<String> images;
    private String profilePic;
    private String userName;
    private String userId;

    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_blog);
        initFirebase();
        initViews();
        setUpToolbar();
        getIntentData();
        initRecyclerAndAdpater();
        setViews();
    }

    private void setViews(){
        setUserData(userId);
        descriptionView.setText(description);
        dateView.setText(date);
        userNameView.setText(userName);

        RequestOptions reqOpt = new RequestOptions();
        reqOpt = reqOpt.placeholder(getDrawable(R.drawable.add_image_icon));
        Glide.with(this).setDefaultRequestOptions(reqOpt).load(coverImage).into(coverImageView);

    }

    private void setUserData(String userId){
        progressBar.setVisibility(View.VISIBLE);
        firestore
                .collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot!=null){
                                    User user = documentSnapshot.toObject(User.class);

                                    userName = user.getUser_name();
                                    profilePic = user.getImage();
                                    userNameView.setText(userName);
                                    RequestOptions reqOpt = new RequestOptions();
                                    reqOpt = reqOpt.placeholder(getDrawable(R.drawable.profile_icon));
                                    Glide.with(ExploreBlogActivity.this).setDefaultRequestOptions(reqOpt).load(profilePic).into(profilePicView);
                                }
                            }
                        }
                ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ExploreBlogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void initViews(){
        descriptionView = findViewById(R.id.explore_description);
        coverImageView = findViewById(R.id.explore_cover_image);
        profilePicView = findViewById(R.id.explore_user_prof_img);
        userNameView= findViewById(R.id.explore_user_name);
        dateView = findViewById(R.id.explore_date);
        toolbar = findViewById(R.id.explore_toolbar);
        progressBar = findViewById(R.id.explore_prog_bar);
    }


    private void initRecyclerAndAdpater(){
        imagesAdapter = new ExploreImagesAdapter(this, images);
        recyclerView = findViewById(R.id.explore_images_recyler);
        GridLayoutManager gm = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gm);
        recyclerView.setAdapter(imagesAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecorator(10));
        imagesAdapter.notifyDataSetChanged();

    }

    private void setUpToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Explore");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getIntentData(){
        Intent intent = getIntent();
        description = intent.getStringExtra("description");
        date = intent.getStringExtra("date");
        profilePic = intent.getStringExtra("profilePic");
        userName = intent.getStringExtra("userName");
        images = intent.getStringArrayListExtra("images");
        coverImage = intent.getStringExtra("coverImage");
        userId = intent.getStringExtra("userId");

    }

    private void initFirebase(){
        firestore = FirebaseFirestore.getInstance();
    }

}