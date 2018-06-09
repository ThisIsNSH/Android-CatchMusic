package com.nsh.catchmusic.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nsh.catchmusic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListenActivity extends AppCompatActivity {

    CircleImageView listen;
    RequestQueue queue;
    String api_key, get_track, get_lyrics, get_track_new, get_lyrics_new, lyrics;
    long track_id,artist_id,album_id;
    String song, track_name, track_lyrics, track_album, track_singer, track_pic;
    JsonObjectRequest lyricsRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        initUI();

        queue = Volley.newRequestQueue(this);
        get_track = "http://api.musixmatch.com/ws/1.1/track.search?apikey="+getString(R.string.api_key)+"&s_track_rating=desc&s_artist_rating=desc&page_size=1";
        get_lyrics = "http://api.musixmatch.com/ws/1.1/track.lyrics.get?apikey="+getString(R.string.api_key);
        song = "Took you like a shot Thought that I could chase you with a cold evening";

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTrack(song);
            }
        });

    }

    public void initUI() {
        listen = findViewById(R.id.listen);
    }

    public void getTrack(String kk) {
        lyrics = "&q_lyrics=" + kk.replace(" ", "%20");
        get_track_new = get_track + lyrics;
        Log.i("track url", get_track_new);
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, get_track_new, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject myResponse = response.getJSONObject("message").getJSONObject("body");
                    JSONArray tsmresponse = (JSONArray) myResponse.get("track_list");
                    for (int i = 0; i < tsmresponse.length(); i++) {
                        track_name = (tsmresponse.getJSONObject(i).getJSONObject("track").getString("track_name"));
                        track_id = (tsmresponse.getJSONObject(i).getJSONObject("track").getLong("track_id"));
                        artist_id = (tsmresponse.getJSONObject(i).getJSONObject("track").getLong("artist_id"));
                        album_id = (tsmresponse.getJSONObject(i).getJSONObject("track").getLong("album_id"));
                        track_album = (tsmresponse.getJSONObject(i).getJSONObject("track").getString("album_name"));
                        track_singer = (tsmresponse.getJSONObject(i).getJSONObject("track").getString("artist_name"));
                        track_pic = (tsmresponse.getJSONObject(i).getJSONObject("track").getString("track_share_url"));
                        getLyrics();
                    }
                } catch (JSONException e) {
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(jsonRequest);
    }

    public void getLyrics() {
        {
            get_lyrics = get_lyrics + "&track_id=";
            get_lyrics_new = get_lyrics + Long.toString(track_id);
            Log.i("lyrics url", get_lyrics_new);
            lyricsRequest = new JsonObjectRequest(Request.Method.GET, get_lyrics_new, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject myResponse = response.getJSONObject("message").getJSONObject("body").getJSONObject("lyrics");
                        track_lyrics = (myResponse.getString("lyrics_body"));
                        Intent intent = new Intent(ListenActivity.this, HomeActivity.class);
                        intent.putExtra("name", track_name);
                        intent.putExtra("singer", track_singer);
                        intent.putExtra("album", track_album);
                        intent.putExtra("url", track_pic);
                        intent.putExtra("lyrics", track_lyrics);
                        intent.putExtra("artist_id",artist_id);
                        intent.putExtra("album_id",album_id);
                        startActivity(intent);

                    } catch (JSONException e) {
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            queue.add(lyricsRequest);
            return;
        }
    }

}
