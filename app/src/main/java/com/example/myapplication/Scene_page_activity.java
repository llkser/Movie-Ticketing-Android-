package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.drawee.view.SimpleDraweeView;

public class Scene_page_activity extends AppCompatActivity {
    private AndroidDatabase androidDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_page_activity);

        Intent intent = getIntent();
        final String movie_name = intent.getStringExtra("movie_name");
        Log.d("test", movie_name);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);

        final SQLiteDatabase db = androidDatabase.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from Movie WHERE instr(upper(movie_name), upper(?)) > 0 ", new String[]{movie_name});

        SimpleDraweeView Movie_img;
        Movie_img = (SimpleDraweeView) findViewById(R.id.movie_post);

        if (cursor.moveToFirst()) {
            do {
                Uri url = Uri.parse(cursor.getString(cursor.getColumnIndex("img_url")));
                Movie_img.setImageURI(url);
            }
            while (cursor.moveToNext());
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
