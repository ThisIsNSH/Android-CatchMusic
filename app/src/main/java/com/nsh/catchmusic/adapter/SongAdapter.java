package com.nsh.catchmusic.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nsh.catchmusic.R;
import com.nsh.catchmusic.model.Song;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

/**
 * Created by ThisIsNSH on 6/6/2018.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    List<Song> songList;
    Activity mContext;

    public SongAdapter(List<Song> songList, Activity mContext) {
        this.songList = songList;
        this.mContext = mContext;
    }

    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @NonNull
    @Override
    public SongAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singer_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.MyViewHolder holder, int position) {
        final Song song = songList.get(position);
        holder.name.setText(song.getName());
        holder.album.setText(song.getAlbum());
        new AAsyncTask(song, holder).execute();

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideo(song.getName());
            }
        });


    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void getVideo(String param) {
        param = param.replace(" ", "%20");
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&q=" + param + "&type=video&key=" + mContext.getString(R.string.google);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String id = response.getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");
                    watchYoutubeVideo(mContext, id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, album;
        CardView button;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pic);
            name = itemView.findViewById(R.id.name);
            album = itemView.findViewById(R.id.album);
            button = itemView.findViewById(R.id.card);
        }
    }

    public class AAsyncTask extends AsyncTask<Void, Void, Element> {
        Song song;
        SongAdapter.MyViewHolder holder;

        AAsyncTask(Song song, SongAdapter.MyViewHolder holder) {
            this.song = song;
            this.holder = holder;
        }

        @Override
        protected Element doInBackground(Void... voids) {
            Element content = new Element("hello");
            try {
                Document document = Jsoup.connect(song.getUrl()).get();
                content = document.select(".banner-album-image-desktop").first();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }

        @Override
        protected void onPostExecute(Element content) {
            Picasso.get().load(content.select("img").first().absUrl("src")).into(holder.imageView);
            super.onPostExecute(content);
        }
    }
}
