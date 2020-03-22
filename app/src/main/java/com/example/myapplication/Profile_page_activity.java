package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class Profile_page_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="Profile_page_activity";
    private TextView username;
    private TextView gender;
    private TextView age;
    private TextView email;
    private TextView phonenumber;
    private AndroidDatabase androidDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page_layout);

        username=findViewById(R.id.profile_username);
        gender=findViewById(R.id.profile_gender);
        age=findViewById(R.id.profile_age);
        email=findViewById(R.id.profile_email);
        phonenumber=findViewById(R.id.profile_phonenumber);
        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});

        if(cursor.moveToFirst())
        {
            username.setText(cursor.getString(cursor.getColumnIndex("Username"))+" ");
            gender.setText(cursor.getString(cursor.getColumnIndex("Gender"))+" ");
            age.setText(cursor.getString(cursor.getColumnIndex("Age"))+" ");
            email.setText(cursor.getString(cursor.getColumnIndex("Email"))+" ");
            phonenumber.setText(cursor.getString(cursor.getColumnIndex("Phonenumber"))+" ");
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Profile");
    }

    @Override
    public void onClick(View v) { }

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
        Toast.makeText(Profile_page_activity.this,str,Toast.LENGTH_SHORT).show();
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
