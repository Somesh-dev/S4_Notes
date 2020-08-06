package com.teams4.blog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //declare the Views and Variables
    private ImageView img;
    private TextView tv;
    private ActivityOptions options;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //required
        super.onCreate(savedInstanceState);
        //setup UI
        initializeUI();
        //initiate animation
        initializeAnimation();
        //start the Handler for delayed time gap
        nextActivity();

    }

    private void nextActivity() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent register = new Intent(MainActivity.this, LoginActivity.class);
                sharedScreen();
                startActivity(register, options.toBundle());
                finish();
            }
        }, 4000);

    }

    private void initializeAnimation() {

        Animation aniEntry = AnimationUtils
                .loadAnimation(getApplicationContext(), R.anim.entry);
        img.startAnimation(aniEntry);
    }

    private void initializeUI() {
        //set the layout
        setContentView(R.layout.activity_main);

        //initialize the Views
        img = findViewById(R.id.Hlogo);
        tv = findViewById(R.id.textView);
    }

    private void sharedScreen() {
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(img, "logo_image");
        pairs[1] = new Pair<View, String>(tv, "logo_text");
        options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
    }

}
