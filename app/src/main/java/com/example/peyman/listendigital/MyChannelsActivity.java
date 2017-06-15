package com.example.peyman.listendigital;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.peyman.listendigital.Adapters.MyChannelsAdapter;
import com.example.peyman.listendigital.Models.Channel;
import com.example.peyman.listendigital.REST.ApiUtils;
import com.example.peyman.listendigital.REST.Calls;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyChannelsActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private MyChannelsAdapter adapter;
    private List<Channel> channelList;
    public ProgressDialog progressDialog;
    private Calls serverCall;
    Integer userId;
    public SharedPreferences sharedPreferences;

    private Boolean isFabOpen = false;
    private FloatingActionButton fab;
    private Button btn1,btn2,btn3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_channels);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyChannelsActivity.this, EnterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sharedPreferences = this.getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        progressDialog = new ProgressDialog(this);
        serverCall = ApiUtils.callAuthServer(token);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        channelList = new ArrayList<>();
        adapter = new MyChannelsAdapter(this, channelList, new MyChannelsAdapter.PostItemListener() {
            @Override
            public void onPostClick(long id) {
                //redirect to channel with medias
//                Toast.makeText(EnterActivity.this, "Post id is" + id, Toast.LENGTH_SHORT).show();
                Intent channelDetails = new Intent(MyChannelsActivity.this, ChannelActivity.class);
//                channelDetails.putExtra("channelId", id);
                startActivity(channelDetails);
                finish();
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        //get userId from the device.
        userId = 1;
        prepareMyChannels(userId);



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
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:

                animateFAB();
                break;
            case R.id.btn1:
                Log.i("peyman", "btn 1");
                Intent intent = new Intent(MyChannelsActivity.this, EnterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn2:
                Log.i("peyman", "btn 2");
                Intent playerIntent = new Intent(MyChannelsActivity.this, PlayerActivity.class);
                startActivity(playerIntent);
                finish();
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

    /**
     * Adding few albums for testing
     */
    private void prepareMyChannels(Integer userId) {
        progressDialog.show();
        serverCall.getPopular().enqueue(new Callback<ArrayList<Channel>>() {
            @Override
            public void onResponse(Call<ArrayList<Channel>> call, Response<ArrayList<Channel>> response) {
                if (response.isSuccessful()) {
                    //code here
                    Log.i("peyman", "call was successful");
                    ArrayList<Channel> allChannels = new ArrayList<Channel>();
                    allChannels = response.body();
                    for (int i = 0; i < allChannels.size(); i++) {
                        Channel channel = response.body().get(i);
                        channelList.add(channel);
                    }
                    adapter.notifyDataSetChanged();

                    progressDialog.dismiss();

                } else {
                    int responseCode = response.code();
                    Log.i("peyman", "response code is " + responseCode);
                    //show user a message based on responseCode.

                    progressDialog.dismiss();

                    Toast.makeText(MyChannelsActivity.this, "failed", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Channel>> call, Throwable t) {
//                showErrorMessage();
                Log.i("peyman", "failure");
                Toast.makeText(MyChannelsActivity.this, "unable to contact the server", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
