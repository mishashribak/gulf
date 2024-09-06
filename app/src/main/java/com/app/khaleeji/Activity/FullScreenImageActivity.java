package com.app.khaleeji.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.app.khaleeji.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity {
   private ImageView imgFullScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        initView(getIntent().getStringExtra("imageUrl"));
    }

    private void initView(String imageUrl) {
        imgFullScreen =findViewById(R.id.img_full_screen);
        Picasso.with(this).load(imageUrl).placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder).into(imgFullScreen, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("", "onSuccess: ");
            }

            @Override
            public void onError() {
                Log.d("", "onError: ");
            }
        });
    }
}
