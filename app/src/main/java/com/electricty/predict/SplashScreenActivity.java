package com.electricty.predict;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ProgressBar;

public class SplashScreenActivity extends AppCompatActivity {


    ProgressBar progressBar;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        bundle = getIntent().getExtras();


        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.getProgressDrawable().setColorFilter(
                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);

        Thread thread = new Thread() {

            public void run() {

                try {
                    for (int i = 0; i < 100; i++) {
                        progressBar.setProgress(i);
                        sleep(20);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (bundle != null) {
                        String string = bundle.getString("data");
                        if (string == null) {
                            startActivity(new Intent(SplashScreenActivity.this, UserWelcomeActivity.class));
                            finish();
                        } else if (string.contains("http")) {
                            Intent intent = new Intent(SplashScreenActivity.this, UserWelcomeActivity.class);
                            intent.putExtra("data", bundle.getString("data"));
                            intent.putExtra("title", bundle.getString("title"));
                            intent.putExtra("body", bundle.getString("body"));
                            startActivity(intent);
                        }
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this, UserWelcomeActivity.class));
                    }
                }
            }
        };
        thread.start();




     }


}
