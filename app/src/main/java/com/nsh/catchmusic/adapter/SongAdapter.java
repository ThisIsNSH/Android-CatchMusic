package com.nsh.catchmusic.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nsh.catchmusic.R;
import com.nsh.catchmusic.activity.MainActivity2;
import com.nsh.catchmusic.activity.SongActivity;
import com.nsh.catchmusic.model.Song;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ThisIsNSH on 6/6/2018.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    List<Song> songList;
    Activity mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name,album;
        FrameLayout button;

        public MyViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.pic);
            name = itemView.findViewById(R.id.name);
            album = itemView.findViewById(R.id.album);
            button = itemView.findViewById(R.id.play);
        }
    }

    public SongAdapter(List<Song> songList, Activity mContext){
        this.songList = songList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SongAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singer_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.MyViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.name.setText(song.getName());
        holder.album.setText(song.getAlbum());
        Picasso.get().load(song.getUrl()).into(holder.imageView);

        final String pic_t = mContext.getString(R.string.t1);
        final String name_t = mContext.getString(R.string.t2);
        final String button_t = mContext.getString(R.string.t5);

        final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                mContext,
                Pair.create((View) holder.name, name_t),
                Pair.create((View) holder.button, button_t),
                Pair.create((View) holder.imageView, pic_t));

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, SongActivity.class), options.toBundle());
            }
        });


    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
