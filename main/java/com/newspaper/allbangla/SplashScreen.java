package com.newspaper.allbangla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashScreen extends AppCompatActivity {

    boolean goingwithPer= false;
    ImageView imageViewSplash;
    ImageView txtAppName;
    RelativeLayout relativeLayout;
    Thread SplashThread;
    boolean holdit=false;
    private int waited=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


//        checkSystemWritePermission();

    }



    private  void splashtask(){
        imageViewSplash = (ImageView) findViewById(R.id.imageViewSplash);
        txtAppName =  findViewById(R.id.txtAppName);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);

//        MediaPlayer playr = MediaPlayer.create(this, R.raw.bujo_nai_beparta);
//        playr.start();

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waited= 3100;
            }
        });
        startAnimations();
    }

    private void startAnimations() {

        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        Animation translate = AnimationUtils.loadAnimation(this, R.anim.translate);

        rotate.reset();
        translate.reset();
        relativeLayout.clearAnimation();

//        imageViewSplash.startAnimation(rotate);
        txtAppName.startAnimation(translate);
        SplashThread = new Thread(){
            @Override
            public void run() {
                super.run();
                waited = 0;
                while (waited < 3500) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    waited += 100;
                }
//                SplashScreen.this.finish();
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        };
        SplashThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (goingwithPer){

        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!checkSystemWritePermission()){
////            splashtask();
//        }else {
            splashtask();
//        }
    }

}
