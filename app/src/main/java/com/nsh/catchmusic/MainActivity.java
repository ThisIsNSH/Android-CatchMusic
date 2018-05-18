package com.nsh.catchmusic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import okhttp3.internal.Util;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {
    String wer;
    String query;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    int check = 0;
    RequestQueue queue;
    String get_track, get_lyrics, lyrics, get_track_new, get_lyrics_new;
    TextView user_lyrics;
    Button find, btnSpeak;
    TextView dis_lyrics, dis_track;
    long track_id;
    File finalFile;
    JsonObjectRequest lyricsRequest;
    SpeechToText service = new SpeechToText();
    FileInputStream input;
    AssetManager assetManager;
    File nsh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find = findViewById(R.id.find);
        dis_lyrics = findViewById(R.id.lyrics1);
        dis_track = findViewById(R.id.track);
        user_lyrics = findViewById(R.id.lyrics);
        assetManager = this.getAssets();

        btnSpeak = findViewById(R.id.btnSpeak);

        Utils.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Utils.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {

                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "AudioRecording.mp3";
                    Log.i("audio save path ", AudioSavePathInDevice);
                    nsh = new File(AudioSavePathInDevice);
                    MediaRecorderReady();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("recorder", "run: stopeed");
                                mediaRecorder.stop();
                            }
                        }, 10000);


                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    requestPermission();
                }
            }
        });

        service.setUsernameAndPassword("b9a2e7de-0a2f-409d-9702-9c997488118d", "ZnGat8Ts5ppy");

        queue = Volley.newRequestQueue(this);
        get_track = "http://api.musixmatch.com/ws/1.1/track.search?apikey=33a8f0aa146868d25a75c04f9810fd02";
        get_lyrics = "http://api.musixmatch.com/ws/1.1/track.lyrics.get?apikey=33a8f0aa146868d25a75c04f9810fd02&track_id=";
        /*user_lyrics.setOnClickListener(new View.OnClickListener() {
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
        });*/
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hat = convertAudio(view, nsh);
                hat = hat.replace("%HESITATION", "a");
                hat = hat.replace(" ", "%20");
                lyrics = "&q_lyrics=" + hat + "&page_size=1";
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

    public String stt(final File filename) {

        RecognizeOptions options = new RecognizeOptions.Builder()

                .model("en-US_NarrowbandModel")
                .contentType("audio/flac")
                .interimResults(false)
                .build();


        BaseRecognizeCallback callback = new BaseRecognizeCallback() {
            @Override
            public void onTranscription(SpeechResults speechResults) {

                query = speechResults.getResults().get(1).getAlternatives().get(1).getTranscript();
                Log.i("onTranscription: ", speechResults.getResults().get(1).getAlternatives().get(1).getTranscript());
                user_lyrics.setText(query);
            }

            @Override
            public void onDisconnected() {
            }
        };


        try {

            //AssetFileDescriptor fileDescriptor = assetManager.openFd("audio-file.flac");
            //System.out.println(nsh);
            FileInputStream stream = new FileInputStream(filename);
            System.out.println(stream);
            service.recognizeUsingWebSocket
                    (stream, options, callback);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return query;
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
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

    public String convertAudio(View v, File wavFile) {
        /**
         *  Update with a valid audio file!
         *  Supported formats: {@link AndroidAudioConverter.AudioFormat}
         */

        //File wavFile = new File(Environment.getExternalStorageDirectory(), "recorded_audio.wav");
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                finalFile = convertedFile;
                wer = stt(finalFile);
                Toast.makeText(MainActivity.this, "SUCCESS: " + convertedFile.getPath(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception error) {
                Toast.makeText(MainActivity.this, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        Toast.makeText(this, "Converting audio file...", Toast.LENGTH_SHORT).show();
        AndroidAudioConverter.with(this)
                .setFile(wavFile)
                .setFormat(AudioFormat.FLAC)
                .setCallback(callback)
                .convert();

        return wer;
    }

}

