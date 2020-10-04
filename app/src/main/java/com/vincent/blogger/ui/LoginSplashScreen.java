package com.vincent.blogger.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vincent.blogger.MainActivity;
import com.vincent.blogger.R;
import com.vincent.blogger.ui.authentication.Login;

public class LoginSplashScreen extends AppCompatActivity {

    private static final String TAG = "LoginSplashScreen";

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    private String email;
    private String password;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_splash_screen);
        initFirebase();
        getIntentData();

        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        loginUser();
                    }
                }
        );

        thread.start();

    }

    public void loginUser(){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, String.valueOf(task.getResult()));

                        if(task.isSuccessful()){
                            redToHome();

                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(LoginSplashScreen.this, error, Toast.LENGTH_SHORT).show();
                            redToLogin();
                        }
                    }
                });
    }

    public void redToLogin(){
        Intent intent = new Intent(LoginSplashScreen.this, Login.class);
        startActivity(intent);
        finish();

    }

    public void redToHome(){
        Intent intent = new Intent(LoginSplashScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void getIntentData(){
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
    }

    public void initFirebase(){
        auth = FirebaseAuth.getInstance();
    }
}