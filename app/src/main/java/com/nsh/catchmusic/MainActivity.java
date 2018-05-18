package com.nsh.catchmusic;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    int check = 0;
    RequestQueue queue;
    String get_track, get_lyrics, lyrics, get_track_new, get_lyrics_new;
    EditText user_lyrics;
    Button find, btnSpeak;
    TextView dis_lyrics, dis_track;
    long track_id;
    JsonObjectRequest lyricsRequest;
    SpeechToText service = new SpeechToText();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find = findViewById(R.id.find);
        dis_lyrics = findViewById(R.id.lyrics1);
        dis_track = findViewById(R.id.track);
        user_lyrics = findViewById(R.id.lyrics);
        AssetManager assetManager = this.getAssets();

        btnSpeak = findViewById(R.id.btnSpeak);
        service.setUsernameAndPassword("b9a2e7de-0a2f-409d-9702-9c997488118d", "ZnGat8Ts5ppy");
        RecognizeOptions options = new RecognizeOptions.Builder()
                .model("en-US_BroadbandModel").contentType("audio/flac")
                .interimResults(false)
                .build();

        BaseRecognizeCallback callback = new BaseRecognizeCallback() {
            @Override
            public void onTranscription(SpeechResults speechResults) {
                System.out.println(speechResults);
            }

            @Override
            public void onDisconnected() {
            }
        };

        try {

            AssetFileDescriptor fileDescriptor = assetManager.openFd("audio-file.flac");
            FileInputStream stream = fileDescriptor.createInputStream();
            service.recognizeUsingWebSocket
                    (stream, options, callback);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }


        queue = Volley.newRequestQueue(this);
        get_track = "http://api.musixmatch.com/ws/1.1/track.search?apikey=33a8f0aa146868d25a75c04f9810fd02";
        get_lyrics = "http://api.musixmatch.com/ws/1.1/track.lyrics.get?apikey=33a8f0aa146868d25a75c04f9810fd02&track_id=";
        user_lyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_lyrics.setFocusableInTouchMode(true);
                user_lyrics.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String catched_lyrics = user_lyrics.getText().toString();
                        catched_lyrics = catched_lyrics.replace(" ", "%20");
                        lyrics = "&q_lyrics=" + catched_lyrics + "&page_size=1";
                    }
                });
            }
        });
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_track_new = get_track + lyrics;
                Log.i("track url", get_track_new);
                final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, get_track_new, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject myResponse = response.getJSONObject("message").getJSONObject("body");
                            JSONArray tsmresponse = (JSONArray) myResponse.get("track_list");
                            String track_name;
                            for (int i = 0; i < tsmresponse.length(); i++) {
                                track_name = (tsmresponse.getJSONObject(i).getJSONObject("track").getString("track_name"));
                                track_id = (tsmresponse.getJSONObject(i).getJSONObject("track").getLong("track_id"));
                                Log.i("track id", track_id + "");
                                check = 1;
                                getLyrics();
                                dis_track.setText(track_name);
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
        });
    }

    public void getLyrics() {
        {
            get_lyrics_new = get_lyrics + Long.toString(track_id);
            Log.i("lyrics url", get_lyrics_new);
            lyricsRequest = new JsonObjectRequest(Request.Method.GET, get_lyrics_new, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject myResponse = response.getJSONObject("message").getJSONObject("body").getJSONObject("lyrics");
                        dis_lyrics.setText(myResponse.getString("lyrics_body"));
                        queue.add(lyricsRequest);
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
        }
    }
}
