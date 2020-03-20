package com.example.myapplication;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class MovieInfo_page_activity extends AppCompatActivity {
    private AndroidDatabase androidDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movieinfo_page_layout);
        Intent intent = getIntent();
        final String movie_name = intent.getStringExtra("movie_name");
        Log.d("test", movie_name);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");


        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        final SQLiteDatabase db = androidDatabase.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from Movie WHERE instr(upper(movie_name), upper(?)) > 0 group by movie_name   --case-insensitive", new String[]{movie_name});

        if (cursor.moveToFirst()) {

            SimpleDraweeView Movie_img;
            SimpleDraweeView Movie_img2;
            TextView Movie_name;
            TextView movie_release_data;
            TextView movie_length;
            TextView movie_score;
            TextView movie_type;
            TextView movie_actors;
            TextView movie_director;
            TextView introduction;
            Button purchase_button;

            Movie_img = (SimpleDraweeView) findViewById(R.id.movieinfo_img);
            Movie_img2 = (SimpleDraweeView) findViewById(R.id.post_img);
            Movie_name = (TextView) findViewById(R.id.movie_name);
            movie_release_data = (TextView) findViewById(R.id.movie_release_date);
            movie_length = (TextView) findViewById(R.id.movie_length);
            movie_score = (TextView) findViewById(R.id.movie_score);
            movie_type = (TextView) findViewById(R.id.movie_type);
            movie_actors = (TextView) findViewById(R.id.movie_actor);
            movie_director = (TextView) findViewById(R.id.movie_director);
            purchase_button = (Button) findViewById(R.id.purchase_button);
            introduction=(TextView) findViewById(R.id.introduction);

            Uri url = Uri.parse( cursor.getString(cursor.getColumnIndex("img_url")));
            Movie_img.setImageURI(url);

            Uri url2 = Uri.parse(cursor.getString(cursor.getColumnIndex("img_url")));
            Movie_img2.setImageURI(url2);

            Movie_name.setText(cursor.getString(cursor.getColumnIndex("movie_name")));
            movie_release_data.setText("premiere date : "+ cursor.getString(cursor.getColumnIndex("premiere_date")));
            movie_length.setText("Length : " + cursor.getString(cursor.getColumnIndex("movie_length")));
            movie_score.setText(cursor.getString(cursor.getColumnIndex("score")) + "/XX");
            movie_type.setText(cursor.getString(cursor.getColumnIndex("country")) + "/" + cursor.getString(cursor.getColumnIndex("movie_type")));
            movie_actors.setText("Actors : " + cursor.getString(cursor.getColumnIndex("actors")));
            movie_director.setText("Director : " + cursor.getString(cursor.getColumnIndex("director")));
            introduction.setText(cursor.getString(cursor.getColumnIndex("introduction")));

            purchase_button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view)
                {
                    Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
                    if(cursor.getCount()==0)
                    {
                        Intent intent = new Intent(MovieInfo_page_activity.this,Login_page_activity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent=new Intent(MovieInfo_page_activity.this,Scene_page_activity.class);
                        intent.putExtra("movie_name",movie_name);
                        MovieInfo_page_activity.this.startActivity(intent);
                    }
                }
            });

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
