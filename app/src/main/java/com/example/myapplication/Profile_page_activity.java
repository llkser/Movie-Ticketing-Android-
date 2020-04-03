package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
            String user_name=cursor.getString(cursor.getColumnIndex("Username"));
            username.setText(user_name+" ");

            OkHttpClient client = new OkHttpClient();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("username", user_name);
            Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/get_profile").post(formBuilder.build()).build();
            final Call call = client.newCall(request);
            call.enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, final IOException e)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            showToast("Can not connect to networks！");
                        }
                    });
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException
                {
                    final String res = response.body().string();
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try {
                                JSONObject res_inform = new JSONObject(res);
                                String gender_text = res_inform.getString("gender");
                                String age_text = res_inform.getString("age");
                                String email_text = res_inform.getString("email");
                                String phonenumber_text = res_inform.getString("phonenumber");
                                String gender_html;
                                if(gender_text.equals("null"))
                                    gender.setText("null");
                                else
                                {
                                    if(gender_text.equals("male"))
                                        gender_html = "<img src='" + R.drawable.male + "'>";
                                    else
                                        gender_html = "<img src='" + R.drawable.female + "'>";
                                    gender.setText(Html.fromHtml(gender_html, new Html.ImageGetter() {
                                        @Override
                                        public Drawable getDrawable(String source) {
                                            int id = Integer.parseInt(source);
                                            Drawable drawable = getResources().getDrawable(id, null);
                                            drawable.setBounds(0, 0, 60 , 60);
                                            return drawable;
                                        }
                                    }, null));
                                }
                                age.setText(age_text+" ");
                                email.setText(email_text+" ");
                                phonenumber.setText(phonenumber_text+" ");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
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
