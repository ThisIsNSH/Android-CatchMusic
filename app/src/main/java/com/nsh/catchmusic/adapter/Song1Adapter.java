package com.nsh.catchmusic.adapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nsh.catchmusic.R;
import com.nsh.catchmusic.model.Song;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

/**
 * Created by ThisIsNSH on 6/6/2018.
 */

public class Song1Adapter extends RecyclerView.Adapter<Song1Adapter.MyViewHolder> {

    List<Song> songList;
    Activity mContext;

    public Song1Adapter(List<Song> songList, Activity mContext) {
        this.songList = songList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Song1Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singer_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Song1Adapter.MyViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.name.setText(song.getName());
        holder.album.setText(song.getAlbum());
        new AAsyncTask(song,holder).execute();

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, album;
        FrameLayout button;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pic);
            name = itemView.findViewById(R.id.name);
            album = itemView.findViewById(R.id.album);
            button = itemView.findViewById(R.id.play);
        }
    }

    public class AAsyncTask extends AsyncTask<Void, Void, Element> {
        Song song;
        Song1Adapter.MyViewHolder holder;

        AAsyncTask (Song song,Song1Adapter.MyViewHolder holder){
        this.song = song;
        this.holder = holder;
        }
        @Override
        protected Element doInBackground(Void... voids) {
            Element content = new Element("hello");
            try {
                Document document = Jsoup.connect(song.getUrl()).get();
                content = document.select(".mxm-album-banner__coverart").first();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }

        @Override
        protected void onPostExecute(Element content) {
            System.out.println(content.select("img").first().absUrl("src"));
            Picasso.get().load(content.select("img").first().absUrl("src")).into(holder.imageView);
            super.onPostExecute(content);
        }
    }
}
