package com.nsh.catchmusic.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nsh.catchmusic.R;
import com.nsh.catchmusic.model.Song;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ThisIsNSH on 6/6/2018.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    List<Song> songList;
    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name,album;
        CircleImageView button;

        public MyViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.pic);
            name = itemView.findViewById(R.id.name);
            album = itemView.findViewById(R.id.album);
            button = itemView.findViewById(R.id.play);
        }
    }

    public SongAdapter(List<Song> songList){
        this.songList = songList;
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

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
