package com.example.peyman.listendigital;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peyman.listendigital.Adapters.MediaAdapter;
import com.example.peyman.listendigital.Models.Channel;
import com.example.peyman.listendigital.Models.Media;
import com.example.peyman.listendigital.REST.ApiUtils;
import com.example.peyman.listendigital.REST.Calls;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private List<Media> mediaList;
    public ProgressDialog progressDialog;
    private Calls serverCall;
    public SharedPreferences sharedPreferences;

    private Boolean isFabOpen = false;
    private FloatingActionButton fab;
    private Button btn1,btn2,btn3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private ImageView pageCover;
    private TextView channelTitle, channelDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCollapsingToolbar();
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChannelActivity.this, EnterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sharedPreferences = this.getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(this);
        serverCall = ApiUtils.callAuthServer(token);

        mediaList = new ArrayList<>();
        adapter = new MediaAdapter(this, mediaList);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        pageCover = (ImageView) findViewById(R.id.backdrop);
        channelTitle = (TextView) findViewById(R.id.love_casts);
        channelDescription = (TextView) findViewById(R.id.tv_channel_description
        );

        //get Id from intent
        Bundle extras = getIntent().getExtras();
        Long id = extras.getLong("channelId");
        Log.i("peyman", "channelId is " + id);

        prepareMedia(id);


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
                Intent intent = new Intent(ChannelActivity.this, EnterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn2:
                Log.i("peyman", "btn 2");
                Intent playerIntent = new Intent(ChannelActivity.this, PlayerActivity.class);
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
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Adding few albums for testing
     */
    private void prepareMedia(final Long id) {
        progressDialog.show();
        serverCall.getMedia(id).enqueue(new Callback<ArrayList<Media>>() {
            @Override
            public void onResponse(Call<ArrayList<Media>> call, Response<ArrayList<Media>> response) {
                if (response.isSuccessful()) {
                    //code here
                    Log.i("peyman", "call was successful");
                    ArrayList<Media> allMedia = new ArrayList<Media>();
                    allMedia = response.body();
                    for (int i = 0; i < allMedia.size(); i++) {
                        Media media = response.body().get(i);
                        mediaList.add(media);
                    }
                    adapter.notifyDataSetChanged();

                    serverCall.getChannel(id).enqueue(new Callback<Channel>() {
                        @Override
                        public void onResponse(Call<Channel> call, Response<Channel> response) {
                            Log.i("peyman", "success to load channel");
                            Channel channel = response.body();
                            channelTitle.setText(channel.getTitle());
                            channelDescription.setText(channel.getDescription());
                            try {
                                Picasso.with(ChannelActivity.this).load(channel.getCover()).into(pageCover);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<Channel> call, Throwable t) {
                            Log.i("peyman", "failed to load channel");
                        }
                    });

                    progressDialog.dismiss();

                } else {
                    int responseCode = response.code();
                    Log.i("peyman", "response code is " + responseCode);
                    //show user a message based on responseCode.
                    progressDialog.dismiss();

                    Toast.makeText(ChannelActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Media>> call, Throwable t) {
//                showErrorMessage();
                Log.i("peyman", "failure");
                Toast.makeText(ChannelActivity.this, "unable to contact the server", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}