package com.nsh.catchmusic;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.google.auth.oauth2.ClientId.fromStream;

public class MainActivity extends AppCompatActivity {
    JSONObject jsonRequest;
    int check = 0;
    RequestQueue queue;
    String api_key, get_track, get_lyrics, lyrics, get_track_new, get_lyrics_new;

    Button find, btnSpeak;
    TextView user_lyrics, dis_lyrics, dis_track;
    long track_id;

    JsonObjectRequest lyricsRequest;
    AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        shot();
        find = findViewById(R.id.find);
        dis_lyrics = findViewById(R.id.lyrics1);
        dis_track = findViewById(R.id.track);
        user_lyrics = findViewById(R.id.lyrics);

        assetManager = this.getAssets();

        btnSpeak = findViewById(R.id.btnSpeak);
        api_key = getString(R.string.api_key);

        queue = Volley.newRequestQueue(this);
        get_track = "http://api.musixmatch.com/ws/1.1/track.search?apikey=" + api_key;
        get_lyrics = "http://api.musixmatch.com/ws/1.1/track.lyrics.get?apikey=" + api_key + "&track_id=";
//        new GoogleAsyncTask().execute();
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTrack();
            }
        });
    }

    public void getTrack() {

        String kk = "d d down";
        lyrics = "&q_lyrics=" + kk.replace(" ", "%20") + "&page_size=1";
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

    public void shot() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        String url = "https://speech.googleapis.com/v1/speech:recognize?key=" + getString(R.string.google);
        try {
            jsonRequest = new JSONObject(

            "{\n" +
                    "  \"config\": {\n" +
                    "  \"sampleRateHertz\": 16000,\n" +
                    "  \"languageCode\": \"en-US\",\n" +
                    "  },\n" +
                    "  \"audio\": {\n" +
                    "    \"uri\": \"gs://catchmusic/audio-file\"" +
                    "  }\n" +
                    "}"

            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                try {
                    String name = response.getString("name");
                    final String url1 = "https://speech.googleapis.com/v1/operations/" + name + "?key=" + getString(R.string.google);
                    shot1(url1);

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

    public void shot1(String url1) {

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        final JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url1, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {

                System.out.println(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        jsonObjectRequest1.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 3, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest1);
    }


}


