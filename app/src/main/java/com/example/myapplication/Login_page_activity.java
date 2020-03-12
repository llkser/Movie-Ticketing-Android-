package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import android.view.Gravity;
import android.view.View;

import android.widget.Button;

import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;



import okhttp3.Call;

import okhttp3.OkHttpClient;

import okhttp3.Request;

import okhttp3.Response;

import okhttp3.*;

public class Login_page_activity extends AppCompatActivity implements View.OnClickListener {

    public static final String Tag="Login_page_activity";
    private EditText inputAccount;
    private EditText inputPassword;
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page_layout);
        loginButton=findViewById(R.id.loginButton);
        registerButton=findViewById(R.id.registerButton);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        inputAccount=findViewById(R.id.account_input_text);
        inputPassword=findViewById(R.id.password_input_text);
    }

    @Override
    public void onClick(View v) {
        String account = inputAccount.getText().toString();
        String passWord = inputPassword.getText().toString();

        switch (v.getId())
        {
            case R.id.loginButton:
                if(account.equals("")||passWord.equals(""))
                {
                    showToast("账号或密码不能为空！");
                    return;
                }
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("username", account);
                formBuilder.add("password", passWord);
                Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/login").post(formBuilder.build()).build();
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
                                showToast("无法连接网络！");
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
                                    String message = res_inform.getString("message");
                                    String error_code = res_inform.getString("error_code");
                                    showToast(message);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                    }
                });
                break;
            case R.id.registerButton:
                Intent intent = new Intent(Login_page_activity.this,Register_page_activity.class);
                startActivity(intent);
                break;
        }
    }

    private void showToast(String str) {
        Toast.makeText(Login_page_activity.this,str,Toast.LENGTH_SHORT).show();
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

