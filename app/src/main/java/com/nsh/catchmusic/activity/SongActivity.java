package com.nsh.catchmusic.activity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.nsh.catchmusic.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class SongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        new AAsyncTask().execute();
    }

    public class AAsyncTask extends AsyncTask<Void, Void, Element> {

        @Override
        protected Element doInBackground(Void... voids) {
            Element content = new Element("hello");
            try {
                Document document = Jsoup.connect("https://www.musixmatch.com/lyrics/Camila-Cabello-feat-Young-Thug/Havana?utm_source=application&utm_campaign=api&utm_medium=nshworld%3A1409617724499").get();
                content = document.select(".banner-album-image-desktop").first();

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }

        @Override
        protected void onPostExecute(Element content) {
            System.out.println(content.select("img").first().absUrl("src"));
            System.out.println(content.getElementsByTag("img"));
            super.onPostExecute(content);
        }
    }


}
