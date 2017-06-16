package com.example.peyman.listendigital.Adapters;

/**
 * Created by Peyman on 11/27/16.
 */

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peyman.listendigital.Models.Media;
import com.example.peyman.listendigital.R;
import com.example.peyman.listendigital.REST.ApiUtils;
import com.example.peyman.listendigital.REST.Calls;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder> implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener{

    private Context mContext;
    private List<Media> mediaList;
    public Calls serverCall;
    private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class
    public SeekBar seekBarProgress;

    private final Handler handler = new Handler();


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_channel_name, tv_media_name, tv_media_description,tv_like_count, tv_comment_count;
        public ImageView iv_media_cover;
        public ImageButton ib_like, ib_comment;
        public MediaPlayer mediaPlayer;

        public MyViewHolder(View view) {
            super(view);
            tv_channel_name = (TextView) view.findViewById(R.id.tv_channel_name);
            tv_media_name = (TextView) view.findViewById(R.id.tv_media_name);
            tv_media_description = (TextView) view.findViewById(R.id.tv_media_description);

            iv_media_cover = (ImageView) view.findViewById(R.id.iv_media_cover);
            ib_like = (ImageButton) view.findViewById(R.id.ib_like);
            tv_like_count = (TextView) view.findViewById(R.id.tv_like_count);
            ib_comment = (ImageButton) view.findViewById(R.id.ib_comment);
            tv_comment_count = (TextView) view.findViewById(R.id.tv_comment_count);

            seekBarProgress = (SeekBar) view.findViewById(R.id.sb_media);

            mediaPlayer = new MediaPlayer();
        }
    }


    public MediaAdapter(Context mContext, List<Media> mediaList) {
        this.mContext = mContext;
        this.mediaList = mediaList;
        this.serverCall = ApiUtils.callAuthServer(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_latest_media_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Media media = mediaList.get(position);
        holder.tv_channel_name.setText(media.getChannel().getTitle());
        holder.tv_media_name.setText(media.getTitle());
        Picasso.with(mContext).load(media.getCover()).placeholder(R.drawable.loading).into(holder.iv_media_cover);

        if (media.getIsLiked()) {
            Picasso.with(mContext).load(R.drawable.like).into(holder.ib_like);
        }
        holder.mediaPlayer.setOnBufferingUpdateListener(this);
        holder.mediaPlayer.setOnCompletionListener(this);

        holder.tv_media_description.setText(media.getBody());
        //display like and comments count and set actions respectly.
        holder.tv_like_count.setText(media.getLikesCount().toString());
        holder.tv_comment_count.setText(media.getCommentsCount().toString());
        holder.ib_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (media.getIsLiked()) {
                    serverCall.disLikeMedia(Long.valueOf(media.getId())).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Picasso.with(mContext).load(R.drawable.dislike).into(holder.ib_like);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.i("peyman", "failed to dislike media");
                        }
                    });
                } else {
                    serverCall.likeMedia(Long.valueOf(media.getId())).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Picasso.with(mContext).load(R.drawable.like).into(holder.ib_like);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.i("peyman", "failed to like media");
                        }
                    });
                }

            }
        });

        holder.iv_media_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "http://10.0.2.2:8000" + media.getMedia();
                //call api to get the url with retrofit

                try {
                    holder.mediaPlayer.setDataSource(link); // setup song from https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
                    holder.mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mediaFileLengthInMilliseconds = holder.mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

                if(!holder.mediaPlayer.isPlaying()){
                    holder.mediaPlayer.start();
                }else {
                    holder.mediaPlayer.stop();
                    holder.mediaPlayer.reset();
                }

                primarySeekBarProgressUpdater();
            }
            /** Method which updates the SeekBar primary progress by current song playing position*/
            private void primarySeekBarProgressUpdater() {
                seekBarProgress.setProgress((int)(((float)holder.mediaPlayer.getCurrentPosition()/mediaFileLengthInMilliseconds)*100)); // This math construction give a percentage of "was playing"/"song length"
                if (holder.mediaPlayer.isPlaying()) {
                    Runnable notification = new Runnable() {
                        public void run() {
                            primarySeekBarProgressUpdater();
                        }
                    };
                    handler.postDelayed(notification,1000);
                }
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        /** MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
        Toast.makeText(mContext, "on completion", Toast.LENGTH_SHORT).show();
        mp.start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
        seekBarProgress.setSecondaryProgress(percent);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }
}
