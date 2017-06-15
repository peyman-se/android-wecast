package com.example.peyman.listendigital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.peyman.listendigital.Adapters.MediaAdapter;
import com.example.peyman.listendigital.Models.Media;
import com.example.peyman.listendigital.REST.ApiUtils;
import com.example.peyman.listendigital.REST.Calls;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LatestCastActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private List<Media> latestMediaList;
    public ProgressDialog progressDialog;
    private Calls serverCall;
    Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_cast);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LatestCastActivity.this, EnterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        serverCall = ApiUtils.callAuthServer(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        latestMediaList = new ArrayList<>();
        adapter = new MediaAdapter(this, latestMediaList);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        //get userId from the device.
        userId = 1;
        prepareLatestMedia(userId);



//        fab = (FloatingActionButton)findViewById(R.id.fab);
//        btn2 = (Button)findViewById(R.id.btn2);
//        btn3 = (Button)findViewById(R.id.btn3);
//        btn1 = (Button) findViewById(R.id.btn1);
//        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
//        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
//        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
//        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
//        fab.setOnClickListener(this);
//        btn2.setOnClickListener(this);
//        btn3.setOnClickListener(this);
//        btn1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            //
        }
    }

    /**
     * Adding few albums for testing
     */
    private void prepareLatestMedia(Integer userId) {
        progressDialog.show();
        serverCall.getLatestSubscribedMedia().enqueue(new Callback<ArrayList<Media>>() {
            @Override
            public void onResponse(Call<ArrayList<Media>> call, Response<ArrayList<Media>> response) {
                ArrayList<Media> myLatestMedia = new ArrayList<Media>();
                myLatestMedia = response.body();
                for (int i = 0; i < myLatestMedia.size(); i++) {
                    Media media = response.body().get(i);
                    latestMediaList.add(media);
                }
                adapter.notifyDataSetChanged();

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<Media>> call, Throwable t) {
                //show user a message based on responseCode.
                Log.i("peyman", "failure");
                progressDialog.dismiss();
                Toast.makeText(LatestCastActivity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
