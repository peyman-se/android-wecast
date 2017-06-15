package com.example.peyman.listendigital.Adapters;

/**
 * Created by Peyman on 11/27/16.
 */
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peyman.listendigital.Models.Channel;
import com.example.peyman.listendigital.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyChannelsAdapter extends RecyclerView.Adapter<MyChannelsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Channel> channelList;
    private PostItemListener mItemListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title, count;
        public ImageView thumbnail, overflow;
        PostItemListener mItemListener;

        public MyViewHolder(View view, PostItemListener postItemListener) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            this.mItemListener = postItemListener;
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            Channel channel = channelList.get(getAdapterPosition());
            this.mItemListener.onPostClick(channel.getId());

            notifyDataSetChanged();
        }
    }


    public MyChannelsAdapter(Context mContext, List<Channel> channelList, PostItemListener mItemListener) {
        this.mContext = mContext;
        this.channelList = channelList;
        this.mItemListener = mItemListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_channels_card, parent, false);

        return new MyViewHolder(itemView, this.mItemListener);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Channel channel = channelList.get(position);
        holder.title.setText(channel.getTitle());
        holder.count.setText(channel.getSubscribersCount() + " members");

        // loading channel cover using Glide library
        Log.i("peyman", channel.getCover());
        Picasso.with(mContext).load(channel.getCover()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public interface PostItemListener {
        void onPostClick(long id);
    }
}
