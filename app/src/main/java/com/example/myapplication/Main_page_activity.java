package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;



public class Main_page_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page_layout);
    }
    private void setIamge()
    {
        String url = "https://img-my.csdn.net/uploads/201407/26/1406383291_8239.jpg";
             OkHttpUtils.get().url(url).tag(this)
            .build()
            .connTimeOut(20000).readTimeOut(20000).writeTimeOut(20000)
            .execute(new BitmapCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                }

                @Override
                public void onResponse(Bitmap bitmap, int id) {
                    //imageView.setImageBitmap(bitmap);
                }
            });
    }
}
