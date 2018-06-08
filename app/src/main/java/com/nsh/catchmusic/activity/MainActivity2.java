package com.nsh.catchmusic.activity;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nsh.catchmusic.OnSwipeTouchListener;
import com.nsh.catchmusic.R;
import com.nsh.catchmusic.adapter.SongAdapter;
import com.nsh.catchmusic.model.Song;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    RecyclerView rec_singer, rec_album;
    TextView name, album, singer;
    ImageView imageView;
    FrameLayout button;
    SongAdapter singerAdapter, albumAdapter;
    List<Song> singerList, albumList;
    CardView card;
    String track_name, track_pic, track_artist, track_album, track_lyrics, get_album, get_artist;
    Long album_id, artist_id;
    RelativeLayout holder;
    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initUI();

        final String pic_t = this.getString(R.string.t1);
        final String name_t = this.getString(R.string.t2);
        final String album_t = this.getString(R.string.t3);
        final String singer_t = this.getString(R.string.t4);
        final String button_t = this.getString(R.string.t5);
        final String card_t = this.getString(R.string.t6);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        MainActivity2.this,
                        Pair.create((View) name, name_t),
                        Pair.create((View) album, album_t),
                        Pair.create((View) singer, singer_t),
                        Pair.create((View) button, button_t),
                        Pair.create((View) imageView, pic_t));
                MainActivity2.this.startActivity(new Intent(MainActivity2.this, SongActivity.class), options.toBundle());

            }
        });

        card.setOnTouchListener(new OnSwipeTouchListener(MainActivity2.this) {
            public void onSwipeLeft() {
                if (check == 0) {
                    Animation animate = AnimationUtils.loadAnimation(MainActivity2.this, R.anim.translate_left);
                    holder.startAnimation(animate);
                    ObjectAnimator animation = ObjectAnimator.ofFloat(holder, "rotationY", 0.0f, 10f);
                    animation.setDuration(100);
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    animation.start();
                    check = 1;
                }
            }

            public void onSwipeRight() {
                if (check == 1) {
                    Animation animate = AnimationUtils.loadAnimation(MainActivity2.this, R.anim.translate_right);
                    holder.startAnimation(animate);
                    ObjectAnimator animation = ObjectAnimator.ofFloat(holder, "rotationY", 10f, 0f);
                    animation.setDuration(100);
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
        Picasso.get().load(track_pic).into(imageView);
        get_album = "http://api.musixmatch.com/ws/1.1/album.tracks.get?album_id=" + album_id + "&page=1&page_size=5&apikey=x";
        get_artist = "http://api.musixmatch.com/ws/1.1/artist.albums.get?artist_id=" + artist_id + "&s_release_date=desc&g_album_name=1&apikey=x&page=1&page_size=5";

        singerList = new ArrayList<>();
        albumList = new ArrayList<>();

        singerAdapter = new SongAdapter(singerList);
        albumAdapter = new SongAdapter(albumList);

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
        System.out.println(getArtistSong(get_artist));
        System.out.println((get_artist));
    }

    public void initUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

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

    public List<Song> getArtistSong(String url) {
        final List<Song> nsh = new ArrayList<>();
        System.out.println("1helllllllllllllo");

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response1 = new JSONObject(response);
                    JSONObject myResponse = response1.getJSONObject("message").getJSONObject("body");
                    JSONArray tsmresponse = (JSONArray) myResponse.get("album_list");
                    for (int i = 0; i < tsmresponse.length(); i++) {
                        Song song = new Song(tsmresponse.getJSONObject(i).getString("album_name"), tsmresponse.getJSONObject(i).getString("album_coverart_100x100"), tsmresponse.getJSONObject(i).getString("album_release_type"));
                        nsh.add(song);
                        singerAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    return;
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("3helllllllllllllo");

            }
        });
        queue.add(jsonObjectRequest);
        return nsh;
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
                        Song song = new Song(tsmresponse.getJSONObject(i).getString("track_name"), tsmresponse.getJSONObject(i).getString("album_coverart_100x100"), tsmresponse.getJSONObject(i).getString("artist_name"));
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

}
