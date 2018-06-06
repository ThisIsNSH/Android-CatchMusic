package com.nsh.catchmusic;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nsh.catchmusic.adapter.SongAdapter;
import com.nsh.catchmusic.model.Song;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity2 extends AppCompatActivity {

    RecyclerView rec_singer, rec_album;
    TextView name, album, singer;
    ImageView imageView;
    CircleImageView button;
    SongAdapter singerAdapter,albumAdapter;
    List<Song> singerList,albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initUI();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        singerList = new ArrayList<>();
        albumList = new ArrayList<>();
        singerAdapter = new SongAdapter(singerList);
        albumAdapter = new SongAdapter(albumList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager()
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(feedAdapter);

    }

    public void initUI() {
        rec_album = findViewById(R.id.rec_m_album);
        rec_singer = findViewById(R.id.rec_m_singer);
        name = findViewById(R.id.name);
        album = findViewById(R.id.album);
        singer = findViewById(R.id.singer);
        imageView = findViewById(R.id.pic);
        button = findViewById(R.id.play);
    }
}
