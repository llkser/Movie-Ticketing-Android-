package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class Membership_page_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="Membership_activity";
    private TextView username;
    private TextView vip_level;
    private TextView balance;
    private TextView progress_label;
    private TextView membership_message;
    private ProgressBar membership_progress;
    private Button topup_button;
    private AndroidDatabase androidDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.membership_page_layout);

        username=findViewById(R.id.membership_username);
        vip_level=findViewById(R.id.membership_vip_level);
        balance=findViewById(R.id.membership_balance);
        membership_progress=findViewById(R.id.membership_progressbar);
        progress_label=findViewById(R.id.membership_progresslabel);
        membership_message=findViewById(R.id.membership_message);
        topup_button=findViewById(R.id.membership_topup);
        topup_button.setOnClickListener(this);

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.moveToFirst()){
            final String user_name=cursor.getString(cursor.getColumnIndex("Username"));

            OkHttpClient client = new OkHttpClient();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("username", user_name);
            Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/get_vip_level").post(formBuilder.build()).build();
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
                                String vip_level_text = res_inform.getString("vip_level");
                                String username_html;
                                if(gender_text.equals("null"))
                                    username.setText(user_name);
                                else{
                                    if(gender_text.equals("male"))
                                        username_html = user_name + "  " + "<img src='" + R.drawable.male + "'>";
                                    else
                                        username_html = user_name + "  " + "<img src='" + R.drawable.female + "'>";
                                    username.setText(Html.fromHtml(username_html, new Html.ImageGetter() {
                                        @Override
                                        public Drawable getDrawable(String source) {
                                            int id = Integer.parseInt(source);
                                            Drawable drawable = getResources().getDrawable(id, null);
                                            drawable.setBounds(0, 0, 60 , 60);
                                            return drawable;
                                        }
                                    }, null));
                                }
                                if(!vip_level_text.equals("null"))
                                {
                                    String vip_html="<img src='" + R.drawable.icon_vip + "'>";
                                    vip_level.setText(Html.fromHtml(vip_html, new Html.ImageGetter() {
                                        @Override
                                        public Drawable getDrawable(String source) {
                                            int id = Integer.parseInt(source);
                                            Drawable drawable = getResources().getDrawable(id, null);
                                            drawable.setBounds(0, 0, 120 , 80);
                                            return drawable;
                                        }
                                    }, null));
                                }
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
        actionBar.setTitle("Membership");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.membership_topup:
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
        Toast.makeText(Membership_page_activity.this,str,Toast.LENGTH_SHORT).show();
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
