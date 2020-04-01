package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class User_page_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="User_page_activity";
    private TextView username;
    private TextView vip_level;
    private Button profile;
    private Button tickets;
    private Button membership;
    private Button settings;
    private Button support;
    private Button logout;
    private AndroidDatabase androidDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page_layout);

        username=findViewById(R.id.username);
        vip_level=findViewById(R.id.vip_level);
        profile=findViewById(R.id.profile);
        tickets=findViewById(R.id.tickets);
        membership=findViewById(R.id.membership);
        settings=findViewById(R.id.settings);
        support=findViewById((R.id.support));
        logout=findViewById(R.id.logout);
        profile.setOnClickListener(this);
        tickets.setOnClickListener(this);
        membership.setOnClickListener(this);
        settings.setOnClickListener(this);
        support.setOnClickListener(this);
        logout.setOnClickListener(this);

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.moveToFirst()){
            username.setText(cursor.getString(cursor.getColumnIndex("Username")));
            String user_vip_level=cursor.getString(cursor.getColumnIndex("Vip_level"));
            Log.d(Tag,user_vip_level);
            if(user_vip_level.equals("null"))
                vip_level.setText("vip0");
            else
                vip_level.setText("vip"+user_vip_level);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("User");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.profile:
                Intent intent = new Intent(User_page_activity.this,Profile_page_activity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                SQLiteDatabase db = androidDatabase.getWritableDatabase();
                Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
                if(cursor.moveToFirst()){
                    ContentValues value = new ContentValues();
                    value.put("Islogin",0);
                    db.update("User",value,"Username=?", new String[]{cursor.getString(cursor.getColumnIndex("Username"))});
                }
                finish();
                break;
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

    private void showToast(String str) {
        Toast.makeText(User_page_activity.this,str,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Tag,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Tag,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Tag,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Tag,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Tag,"onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(Tag,"onRestart");
    }
}
