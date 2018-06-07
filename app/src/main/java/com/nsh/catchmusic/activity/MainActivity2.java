package com.nsh.catchmusic.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nsh.catchmusic.R;
import com.nsh.catchmusic.adapter.SongAdapter;
import com.nsh.catchmusic.model.Song;
import com.squareup.picasso.Picasso;

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
    String track_name, track_pic, track_artist, track_album, track_lyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initUI();

        Bundle bundle = getIntent().getExtras();
        track_name = bundle.getString("name","Not found");
        track_pic = bundle.getString("url","Not found");
        track_album = bundle.getString("album","Not found");
        track_artist = bundle.getString("singer","Not found");
        track_lyrics = bundle.getString("lyrics","Not found");

        System.out.println(track_name);
        prepareUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        new LoadAsyncTask().execute();
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
    }

    public void prepareUI(){
        name.setText(track_name);
        album.setText(track_album);
        singer.setText(track_artist);
        Picasso.get().load(track_pic).into(imageView);

    }
    public void preparedata() {
        Song song = new Song("Name", "url", "Singer");
        singerList.add(song);
        singerList.add(song);
        singerList.add(song);
        singerList.add(song);
        albumList.add(song);
        albumList.add(song);
        albumList.add(song);
        albumList.add(song);
        albumAdapter.notifyDataSetChanged();
        singerAdapter.notifyDataSetChanged();
    }

    public void initUI() {
        rec_album = findViewById(R.id.rec_m_album);
        rec_singer = findViewById(R.id.rec_m_singer);
        name = findViewById(R.id.name);
        album = findViewById(R.id.album);
        singer = findViewById(R.id.singer);
        imageView = findViewById(R.id.pic);
        card = findViewById(R.id.card);
        button = findViewById(R.id.play);
    }

    public class LoadAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

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
            preparedata();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
