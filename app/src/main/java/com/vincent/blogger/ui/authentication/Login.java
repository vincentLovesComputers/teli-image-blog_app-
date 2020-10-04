package com.vincent.blogger.ui.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.vincent.blogger.ui.LoginSplashScreen;
import com.vincent.blogger.R;

public class Login extends AppCompatActivity {

    TextInputEditText emailView;
    TextInputEditText passwordView;
    TextInputLayout emailViewLayout;
    TextInputLayout passwordViewLayout;


    MaterialButton loginBtn;
    TextView regBtn;

    String email;
    String password;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialiseViews();
        auth = FirebaseAuth.getInstance();

        loginBtnClick();
        regBtnClicked();

    }

    public void loginBtnClick(){
        loginBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(validateFields()){
                            loginBtn.setEnabled(true);
                            loginBtn.setBackgroundColor(getResources().getColor(R.color.orange2));
                            redToSplashScreen();
                        }else{
                            loginBtn.setEnabled(false);
                        }
                    }
                }
        );
    }

    public void regBtnClicked(){
        regBtn.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        regBtn.setTextColor(getResources().getColor(R.color.orange2));
                        redToReg();
                    }
                }
        );
    }

    public void redToReg(){
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        finish();
    }


    public void redToSplashScreen(){
        Intent intent = new Intent(Login.this, LoginSplashScreen.class);
        intent.putExtra(LoginSplashScreen.EMAIL, email);
        intent.putExtra(LoginSplashScreen.PASSWORD, password);
        startActivity(intent);
        finish();
    }

    public void initialiseViews(){
        passwordView =  findViewById(R.id.login_pwd);
        loginBtn =  findViewById(R.id.signIn_btn);
        regBtn =  findViewById(R.id.signUp_signIn_btn);
        emailView = findViewById(R.id.login_email);

        emailViewLayout = findViewById(R.id.login_email_layout);
        passwordViewLayout = findViewById(R.id.login_pwd_layout);
    }

    public boolean validateFields(){
        email = emailView.getText().toString();
        password = passwordView.getText().toString();
        if(!email.matches("") && !password.matches("")){
            Log.d("login", "fields good");
            return true;
        }else{
            if(email.matches("")){
                emailViewLayout.setError("Email cannot be empty");

            }else if(password.matches("")){
                passwordViewLayout.setError("Password cannot be empty");

            }

            return false;

        }
    }


}