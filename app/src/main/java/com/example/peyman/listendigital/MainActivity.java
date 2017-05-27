package com.example.peyman.listendigital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.peyman.listendigital.Adapters.ChannelAdapter;
import com.example.peyman.listendigital.Models.Channel;
import com.example.peyman.listendigital.REST.ApiUtils;
import com.example.peyman.listendigital.REST.Calls;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private ChannelAdapter adapter;
    private List<Channel> channelList;
    private Calls serverCall;

    public ProgressDialog progressDialog;

    private Boolean isFabOpen = false;
    private FloatingActionButton fab;
    private Button btn1,btn2,btn3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private ImageView pageCover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCollapsingToolbar();
        progressDialog = new ProgressDialog(this);
        serverCall = ApiUtils.callServer();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        pageCover = (ImageView) findViewById(R.id.backdrop);

        channelList = new ArrayList<>();
        adapter = new ChannelAdapter(this, channelList, new ChannelAdapter.PostItemListener() {

            @Override
            public void onPostClick(long id) {
                Toast.makeText(MainActivity.this, "Post id is" + id, Toast.LENGTH_SHORT).show();
            }
        });

        prepareAlbums();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

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
                Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
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

    private void prepareAlbums() {
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
                    try {
                        Picasso.with(MainActivity.this).load(channelList.get(0).getCover()).into(pageCover);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();

                } else {
                    int responseCode = response.code();
                    Log.i("peyman", "response code is " + responseCode);
                    //show user a message based on responseCode.

                    progressDialog.dismiss();

                    Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Channel>> call, Throwable t) {
//                showErrorMessage();
                Log.i("peyman", "failure");
                Toast.makeText(MainActivity.this, "unable to contact the server", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}