package com.vincent.blogger.ui.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vincent.blogger.R;
import com.vincent.blogger.ui.Setup;

public class Register extends AppCompatActivity {

    public static final String TAG = "Register";

    TextInputLayout emailViewLayout;
    TextInputLayout pwdViewLayout;
    TextInputLayout confPwdViewLayout;


    TextInputEditText emailView;
    TextInputEditText pwdView;
    TextInputEditText confPwdView;

    MaterialButton signUpBtn;
    MaterialButton signInSignUpBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeViews();

       submitReg();
       signIn();
    }

    public void signIn(){

        signInSignUpBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signInSignUpBtn.setBackgroundColor(getResources().getColor(R.color.white));


                        redToSignIn();
                    }
                }
        );

    }

    public void submitReg(){
        signUpBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signUpBtn.setBackgroundColor(getResources().getColor(R.color.orange2));
                        String email = emailView.getText().toString();
                        String pwd = pwdView.getText().toString();
                        String confPwd = confPwdView.getText().toString();


                        if(validateInputs(email, pwd, confPwd)){
                            redToProfileSetup(email, pwd);
                        }else{
                            Log.d(TAG, "Fields invalid");
                        }
                    }
                }
        );
    }

    public void redToProfileSetup(String email, String password){
        Intent intent = new Intent(Register.this, Setup.class);
        intent.putExtra(Setup.EMAIL, email);
        intent.putExtra(Setup.PASSWORD, password);
        intent.putExtra(Setup.REASON_VISITING_PAGE, "new_user");
        startActivity(intent);
        finish();
    }


    public void initializeViews(){
        emailViewLayout =  findViewById(R.id.reg_email_layout);
        pwdViewLayout =  findViewById(R.id.reg_pwd_layout);
        confPwdViewLayout = findViewById(R.id.reg_conf_pwd_layout);
        pwdView = findViewById(R.id.reg_pwd);
        emailView = findViewById(R.id.reg_email);
        confPwdView = findViewById(R.id.reg_conf_pwd);

        signInSignUpBtn = findViewById(R.id.signIn_signUp_btn);
        signUpBtn = findViewById(R.id.signUp_btn);

    }

    public boolean validateInputs(String email, String pwd, String confPwd){

        if(!email.isEmpty() && !pwd.isEmpty() && !confPwd.isEmpty()){
            if(pwd.equals(confPwd)){
                return true;
            }else{
                confPwdViewLayout.setError("Passwords don't match");
                pwdViewLayout.setError("Passwords don't match");
                return false;
            }

        }else{
            emailViewLayout.setError("Enter your email");
            pwdViewLayout.setError("Password field cannot be empty");

            return false;
        }
    }

    public void redToSignIn(){
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }



}