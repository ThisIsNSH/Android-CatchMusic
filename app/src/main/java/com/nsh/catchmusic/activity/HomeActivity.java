package com.nsh.catchmusic.activity;

import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jgabrielfreitas.core.BlurImageView;
import com.nsh.catchmusic.OnSwipeTouchListener;
import com.nsh.catchmusic.R;
import com.nsh.catchmusic.adapter.Song1Adapter;
import com.nsh.catchmusic.adapter.SongAdapter;
import com.nsh.catchmusic.model.Song;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    RecyclerView rec_singer, rec_album;
    TextView name, album, singer;
    ImageView imageView;
    FrameLayout button;
    Song1Adapter singerAdapter;
    SongAdapter albumAdapter;
    List<Song> singerList, albumList;
    CardView card;
    String track_name, track_pic, track_artist, track_album, track_lyrics, get_album, get_artist;
    Long album_id, artist_id;
    RelativeLayout holder;
    LinearLayout holder1;
    int check = 0;
    BlurImageView blurImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initUI();
        card.setOnTouchListener(new OnSwipeTouchListener(HomeActivity.this) {

            public void onSwipeLeft() {
                if (check == 0) {
                    Animation animate = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.translate_left);
                    holder.startAnimation(animate);
                    ObjectAnimator animation = ObjectAnimator.ofFloat(holder, "rotationY", 0.0f, 10f);
                    animation.setDuration(300);
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    animation.start();
                    check = 1;
                }
            }

            public void onSwipeRight() {
                if (check == 1) {
                    Animation animate = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.translate_right);
                    holder.startAnimation(animate);
                    ObjectAnimator animation = ObjectAnimator.ofFloat(holder, "rotationY", 10f, 0f);
                    animation.setDuration(300);
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    animation.start();
                    check = 0;
                }
            }


        });
    }


    public void prepareUI() {
        Bundle bundle = getIntent().getExtras();
        track_name = bundle.getString("name", "Not found");
        track_pic = bundle.getString("url", "Not found");
        track_album = bundle.getString("album", "Not found");
        track_artist = bundle.getString("singer", "Not found");
        track_lyrics = bundle.getString("lyrics", "Not found");
        album_id = bundle.getLong("album_id", 0);
        artist_id = bundle.getLong("artist_id", 0);
        name.setText(track_name);
        album.setText(track_album);
        singer.setText(track_artist);
        new AAsyncTask().execute();

        get_album = "http://api.musixmatch.com/ws/1.1/album.tracks.get?album_id=" + album_id + "&page=1&page_size=5&apikey=" + getString(R.string.api_key);
        get_artist = "http://api.musixmatch.com/ws/1.1/artist.albums.get?artist_id=" + artist_id + "&s_release_date=desc&g_album_name=1&apikey=" + getString(R.string.api_key) + "&page=1&page_size=5";

        singerList = new ArrayList<>();
        albumList = new ArrayList<>();

        singerAdapter = new Song1Adapter(singerList, HomeActivity.this);
        albumAdapter = new SongAdapter(albumList, HomeActivity.this);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        rec_singer.setLayoutManager(llm);
        rec_singer.setAdapter(singerAdapter);
        rec_singer.setItemAnimator(new DefaultItemAnimator());
        rec_singer.setFocusable(false);

        LinearLayoutManager llm1 = new LinearLayoutManager(getApplicationContext());
        llm1.setOrientation(LinearLayoutManager.HORIZONTAL);
        rec_album.setLayoutManager(llm1);
        rec_album.setAdapter(albumAdapter);
        rec_album.setItemAnimator(new DefaultItemAnimator());
        rec_album.setFocusable(false);

        getArtistSong(get_artist);
        getAlbumSong(get_album);
    }

    public void initUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        blurImageView = findViewById(R.id.BlurImageView);
        holder1 = findViewById(R.id.holder1);
        holder = findViewById(R.id.holder);
        rec_album = findViewById(R.id.rec_m_album);
        rec_singer = findViewById(R.id.rec_m_singer);
        name = findViewById(R.id.name);
        album = findViewById(R.id.album);
        singer = findViewById(R.id.singer);
        imageView = findViewById(R.id.pic);
        card = findViewById(R.id.card);
        button = findViewById(R.id.play);
        prepareUI();

    }

    public void getArtistSong(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response1 = new JSONObject(response);
                    JSONObject myResponse = response1.getJSONObject("message").getJSONObject("body");
                    JSONArray tsmresponse = (JSONArray) myResponse.get("album_list");
                    for (int i = 0; i < tsmresponse.length(); i++) {
                        Song song = new Song(tsmresponse.getJSONObject(i).getJSONObject("album").getString("album_name"), tsmresponse.getJSONObject(i).getJSONObject("album").getString("album_edit_url"), tsmresponse.getJSONObject(i).getJSONObject("album").getString("album_release_type"));
                        singerList.add(song);
                        singerAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    return;
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void getAlbumSong(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject myResponse = response.getJSONObject("message").getJSONObject("body");
                    JSONArray tsmresponse = (JSONArray) myResponse.get("track_list");
                    for (int i = 0; i < tsmresponse.length(); i++) {
                        Song song = new Song(tsmresponse.getJSONObject(i).getJSONObject("track").getString("track_name"), tsmresponse.getJSONObject(i).getJSONObject("track").getString("track_share_url"), tsmresponse.getJSONObject(i).getJSONObject("track").getString("artist_name"));
                        albumList.add(song);
                        albumAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    return;
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    public class AAsyncTask extends AsyncTask<Void, Void, Element> {

        @Override
        protected Element doInBackground(Void... voids) {
            Element content = new Element("hello");
            try {
                Document document = Jsoup.connect(track_pic).get();
                content = document.select(".banner-album-image-desktop").first();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }

        @Override
        protected void onPostExecute(Element content) {
            Picasso.get().load(content.select("img").first().absUrl("src")).into(imageView);
            Picasso.get().load(content.select("img").first().absUrl("src")).into(blurImageView);
            super.onPostExecute(content);
        }
    }

}
