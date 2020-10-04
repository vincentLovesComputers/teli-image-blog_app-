package com.vincent.blogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vincent.blogger.interfaces.NotificationsInterface;
import com.vincent.blogger.interfaces.UpdateNotificationsInterface;
import com.vincent.blogger.models.Notification;
import com.vincent.blogger.models.User;
import com.vincent.blogger.services.NotificationsService;
import com.vincent.blogger.services.UpdateNotificatonsService;
import com.vincent.blogger.ui.Post;
import com.vincent.blogger.ui.Setup;
import com.vincent.blogger.ui.authentication.Login;
import com.vincent.blogger.ui.fragments.AccountFragment;
import com.vincent.blogger.ui.fragments.HomeFragment;
import com.vincent.blogger.ui.fragments.NotificationFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private Toolbar toolbar;

    private ImageView homeView;
    private CircleImageView profView;
    private ImageView notificationView;
    private TextView notificationCountView;
    private MaterialCardView notificationsCard;

    private ProgressBar progressBar;

    private Fragment homeFragment;
    private Fragment notiFragment;
    private Fragment accnFragment;

    private BottomNavigationView btmNav;

    Context context;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String currentUser;

    private int notificationCount;
    private NotificationsService notificationService;
    private UpdateNotificatonsService updateNotificatonsService;

    private int lastNotificationsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationService = new NotificationsService(this);
        updateNotificatonsService =new UpdateNotificatonsService(this);

        auth = FirebaseAuth.getInstance();
        firestore =FirebaseFirestore.getInstance();
        currentUser = auth.getUid();

        if(auth.getUid() == null){
            redToLogin();
        }else{
            initVeiws();
            setupToolbar();
            initFragments();
            getUserData();
            replaceFragment(homeFragment);
            bottomNavItemsClicked();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getUid() == null){
            redToLogin();
        }

    }

    private void getUserData(){
        progressBar.setVisibility(View.VISIBLE);
        firestore
        .collection("Users")
        .document(currentUser)
        .get()
        .addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       if(documentSnapshot != null){
                           User user = documentSnapshot.toObject(User.class);

                           if(user != null){
                               RequestOptions reqOpt = new RequestOptions();
                               reqOpt = reqOpt.placeholder(getResources().getDrawable(R.drawable.person_icon));
                               Glide.with(MainActivity.this).setDefaultRequestOptions(reqOpt).load(user.getImage()).into(profView);
                           }
                       }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );

        notificationService.getNotificationsInfo(new NotificationsInterface() {
            @Override
            public void getNotifications(Notification notification) {
            }

            @Override
            public void getCountOfLikes(int count) {
                notificationCount = count;
                if(notificationCount!=0){
                    notificationsCard.setVisibility(View.VISIBLE);
                }
                lastNotificationsCount = lastNotificationsCount + notificationCount;
                notificationCountView.setText(String.valueOf(notificationCount));
            }

        });
    }

    private void bottomNavItemsClicked(){

        homeView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSupportActionBar().setTitle("");
                        replaceFragment(homeFragment);
                    }
                }
        );

        profView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSupportActionBar().setTitle("Profile");
                        replaceFragment(accnFragment);
                    }
                }
        );

        notificationView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateNotificatonsService.updateNotifications(new UpdateNotificationsInterface() {
                            @Override
                            public void updateNotifications(int count) {
                                notificationsCard.setVisibility(View.INVISIBLE);
                                notificationCount = count;
                                lastNotificationsCount = lastNotificationsCount + notificationCount;
                                notificationCountView.setText(String.valueOf(notificationCount));
                            }
                        });

                        getSupportActionBar().setTitle("Notifications");
                        replaceFragment(notiFragment);
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                logoutUser();
                return true;

            case R.id.add_post:
                goToPost();
                return true;

            default:
                return false;
        }
    }

    private void logoutUser(){
        auth.signOut();
        goToLogin();
    }

    //method to setup toolbar
    private void setupToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

    }


    private void initVeiws(){
        toolbar = findViewById(R.id.toolbar);
        btmNav =  findViewById(R.id.btmNav);
        notificationView = findViewById(R.id.notification_icon);
        profView = findViewById(R.id.btm_nav_prof);
        homeView = findViewById(R.id.btm_nav_home);
        notificationCountView = findViewById(R.id.notification_counter);
        notificationsCard = findViewById(R.id.notification_card);
        progressBar = findViewById(R.id.main_progressBar);

    }

    private void initFragments(){
        notiFragment = new NotificationFragment();
        accnFragment = new AccountFragment();
        homeFragment = new HomeFragment(MainActivity.this);
    }

    public void goToLogin(){
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }

    //method to replace fragments according to bottom nav icon clicked
    public void replaceFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment);
        ft.commitNow();

    }


    public void redToLogin(){
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }

    private void goToPost(){
        Intent intent = new Intent(this, Post.class);
        startActivity(intent);
    }

    private void goToSetup(){
        Intent intent = new Intent(this, Setup.class);
        startActivity(intent);
    }


}