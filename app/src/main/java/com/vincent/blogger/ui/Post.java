package com.vincent.blogger.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vincent.blogger.MainActivity;
import com.vincent.blogger.PostSplashScreenActivity;
import com.vincent.blogger.R;
import com.vincent.blogger.SpacesItemDecorator;
import com.vincent.blogger.adapters.ImagesAdapter;
import com.vincent.blogger.models.Categories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import pl.droidsonroids.gif.GifImageView;


public class Post extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "Post";

    private Toolbar toolbar;
    private EditText descriptionView;
    private TextInputLayout descriptionLayout;
    private MaterialButton postButton;


    Uri postImageUri;

    private StorageReference storageRef;
    private FirebaseFirestore firestore;
    private String current_user_id;


    private TextInputLayout categoryTextInputLayout;
    private Spinner dropdownSpinner;

    private Categories[] categoriesList;
    private String categorySelected;
    private ArrayList<Uri> imageList;

    private ImagesAdapter imagesAdapter;
    private RecyclerView recyclerView;

    private Uri mobileImageChosen;

    private ArrayList<String> dbImages = new ArrayList<>();
    private ArrayList<String> categoriesItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imageList = new ArrayList<>();

        initFirebase();
        initViews();
        setupToolbar();
        setUpSpinnerItems();

        postButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postButton.setBackgroundColor(getResources().getColor(R.color.lightPurple));
                        String description = descriptionView.getText().toString();
                        String category = categorySelected;
                        submitPost(description, category);
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_post_ic_camera:
                setImages();

            default:
                return false;

        }
    }


    //method to override device onActivity to get crop result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                imageList.add(postImageUri);
                imagesAdapter.notifyDataSetChanged();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categorySelected = categoriesItems.get(position);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void setUpSpinnerItems(){
        dropdownSpinner.setOnItemSelectedListener(this);

        categoriesList = Categories.categoriesList;

        for(int i=1; i<categoriesList.length; i++){
            String title = categoriesList[i].getTitle();
            categoriesItems.add(title);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                Post.this,
                R.layout.post_dropdown_menu,
                categoriesItems
        );

        adapter.setDropDownViewResource(R.layout.post_dropdown_menu);

        dropdownSpinner.setAdapter(adapter);
    }


    private void setImages(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ){
                Log.d(TAG, "Permssion not given");
                Log.d(TAG, "Asking for permission");

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1
                );

                imagePicker();

            }else{

                imagePicker();
            }

        }else{
            imagePicker();
        }
    }


    private void imagePicker(){
        Log.d(TAG, "Image picker method");

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(Post.this);
    }

    private void submitPost(String desc, String cat){

        Log.d(TAG, desc);
        if(postImageUri!=null && !desc.matches("") && !cat.matches("")){
            redToSplashScreen(imageList, desc, cat);
        }else{
            if(desc.matches("")){
                descriptionLayout.setError("Description field cannot be empty");
            }else if(cat.matches("")){
                categoryTextInputLayout.setError("Choose a category");
            }else if(postImageUri==null ){
                Toast.makeText(this, "Pick an image", Toast.LENGTH_SHORT).show();
            }
        }

    }



    private void redToSplashScreen(ArrayList<Uri> imagesList, String desc, String cat){
        Intent intent = new Intent(Post.this, PostSplashScreenActivity.class);
        intent.putParcelableArrayListExtra(PostSplashScreenActivity.IMAGES_LIST, imagesList);
        intent.putExtra(PostSplashScreenActivity.DESCRIPTION, desc);
        intent.putExtra(PostSplashScreenActivity.CATEGORY, cat);
        startActivity(intent);
    }


    private void initViews(){
        toolbar =  findViewById(R.id.toolbar);
        descriptionView =  findViewById(R.id.post_description);
        dropdownSpinner =  findViewById(R.id.post_dropdown_spinner);
        descriptionLayout = findViewById(R.id.post_description_layout);
        postButton = findViewById(R.id.post_blog);
        initRecyclerAndAdapter();

    }


    public void initRecyclerAndAdapter(){
        imagesAdapter = new ImagesAdapter(this, imageList);
        recyclerView = findViewById(R.id.post_images_recycler_view);
        GridLayoutManager gm = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gm);
        recyclerView.setAdapter(imagesAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecorator(10));

    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void initFirebase(){
        storageRef = FirebaseStorage.getInstance().getReference();
        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();
    }



}