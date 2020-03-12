package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
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

    public static final String Tag="Main_activity";
    private EditText inputAccount;
    private EditText inputPassword;
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page_layout);
        loginButton=findViewById(R.id.login);
        registerButton=findViewById(R.id.register);
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
            case R.id.login:
                if(account.equals("")||passWord.equals(""))
                {
                    showToast("账号或密码不能为空！");
                    return;
                }


                show_user_name_warn("");
                show_password_warn("");
                User user_login_info=new User();
                user_login_info.name=userName;
                user_login_info.password=passWord;
                user_login_info.url="http://nightmaremlp.pythonanywhere.com/appnet/login";
                user_login_info.mode= "log_in";
                user_login_info.setProgessDialog(loading_dialog);
                registeNameWordToServer(user_login_info);
                break;
            case R.id.register:
                show_user_name_warn("");
                show_password_warn("");
                User user_register_info=new User();
                user_register_info.name=userName;
                user_register_info.password=passWord;
                user_register_info.url="http://nightmaremlp.pythonanywhere.com/appnet/register";
                user_register_info.mode= "register";
                user_register_info.setProgessDialog(loading_dialog);
                registeNameWordToServer(user_register_info);
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

