package com.vincent.blogger.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.vincent.blogger.MainActivity;
import com.vincent.blogger.R;

import static java.lang.Thread.sleep;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try{

                            sleep(1000);
                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
        );
        thread.start();
    }
}