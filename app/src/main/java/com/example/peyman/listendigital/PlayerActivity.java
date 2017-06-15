package com.example.peyman.listendigital;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import co.mobiwise.library.InteractivePlayerView;
import co.mobiwise.library.OnActionClickedListener;

public class PlayerActivity extends AppCompatActivity implements OnActionClickedListener,View.OnClickListener {

    private FloatingActionButton fab;
    private Boolean isFabOpen = false;
    private Button btn1,btn2,btn3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        final InteractivePlayerView mInteractivePlayerView = (InteractivePlayerView) findViewById(R.id.interactivePlayerView);
        mInteractivePlayerView.setMax(100);//total seconds
        mInteractivePlayerView.setProgress(70); //starts at
        mInteractivePlayerView.setOnActionClickedListener(this);

        final ImageView imageView = (ImageView) findViewById(R.id.control);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mInteractivePlayerView.isPlaying()) {
                    mInteractivePlayerView.start();
                    imageView.setBackgroundResource(R.drawable.ic_action_pause);
                } else {
                    mInteractivePlayerView.stop();
                    imageView.setBackgroundResource(R.drawable.ic_action_play);
                }
            }
        });

        fab = (FloatingActionButton)findViewById(R.id.fab);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);
        btn1 = (Button) findViewById(R.id.btn1);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn1.setOnClickListener(this);
    }

    @Override
    public void onActionClicked(int id) {
        switch (id) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:

                animateFAB();
                break;
            case R.id.btn1:
                Log.i("peyman", "btn 1");
                Intent intent = new Intent(PlayerActivity.this, EnterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn2:

                Log.i("peyman", "btn 2");
                break;
            case R.id.btn3:
                Log.i("peyman", "action related to button 1");
                break;
        }
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            btn2.startAnimation(fab_close);
            btn3.startAnimation(fab_close);
            btn1.startAnimation(fab_close);
            btn2.setClickable(false);
            btn3.setClickable(false);
            btn1.setClickable(false);
            isFabOpen = false;
            Log.i("peyman", "close");

        } else {

            fab.startAnimation(rotate_forward);
            btn2.startAnimation(fab_open);
            btn3.startAnimation(fab_open);
            btn1.startAnimation(fab_open);
            btn2.setClickable(true);
            btn3.setClickable(true);
            btn1.setClickable(true);
            isFabOpen = true;
            Log.d("peyman","open");

        }
    }
}
