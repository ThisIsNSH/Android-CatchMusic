package com.nsh.catchmusic;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity2 extends AppCompatActivity {

    CircleImageView imageView;
    ScrollView scrollView;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initUI();
        imageView.setTranslationY(-70);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    Float curve = (float) (scrollY / 30) * 0.15f;
                    Float translate = (float) (scrollY / 3) * 2f ;
                    translate = translate<=350 ? translate : 350;
                    imageView.setScaleX(2 + curve);
                    imageView.setTranslationY(-70 - translate);

                    System.out.println(imageView.getScaleX());
                    System.out.println(imageView.getTranslationY());
                    System.out.println("scrollY" + scrollY);
                    System.out.println("oldscrollY" + oldScrollY);

                }
            });
    }

    public void initUI() {
        imageView = findViewById(R.id.top_image);
        scrollView = findViewById(R.id.scroll);
    }
}
