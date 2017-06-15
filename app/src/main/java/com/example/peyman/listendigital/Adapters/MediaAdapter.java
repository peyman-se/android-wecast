package com.example.peyman.listendigital.Adapters;

/**
 * Created by Peyman on 11/27/16.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peyman.listendigital.Models.Media;
import com.example.peyman.listendigital.R;
import com.example.peyman.listendigital.REST.ApiUtils;
import com.example.peyman.listendigital.REST.Calls;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<Media> mediaList;
    public Calls serverCall;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_channel_name, tv_media_name, tv_media_description,tv_like_count, tv_comment_count;
        public ImageView iv_media_cover;
        public ImageButton ib_like, ib_comment;

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
        }
    }


    public MediaAdapter(Context mContext, List<Media> mediaList) {
        this.mContext = mContext;
        this.mediaList = mediaList;
        this.serverCall = ApiUtils.callAuthServer(this.token);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_latest_media_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Media media = mediaList.get(position);
        holder.tv_channel_name.setText(media.getChannel().getTitle());
        holder.tv_media_name.setText(media.getTitle());
        Picasso.with(mContext).load(media.getCover()).placeholder(R.drawable.loading).into(holder.iv_media_cover);

        if (media.getIsLiked()) {
            Picasso.with(mContext).load(R.drawable.like).into(holder.ib_like);
        }

        holder.tv_media_description.setText(media.getBody());
        //display like and comments count and set actions respectly.
        holder.tv_like_count.setText(media.getLikesCount());
        holder.tv_comment_count.setText(media.getCommentsCount());
        holder.ib_like.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //call retrofit call for like or dislike it

    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }
}
