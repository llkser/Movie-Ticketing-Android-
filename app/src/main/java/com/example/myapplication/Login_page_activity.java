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


public class Login_page_activity extends AppCompatActivity implements View.OnClickListener{
    public static final String Tag="Main_activity";

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_page_layout);

        Button button1=(Button)findViewById(R.id.log_in);

        Button button2=(Button)findViewById(R.id.register);

        button1.setOnClickListener(this);

        button2.setOnClickListener(this);



    }
    public void show_user_name_warn(String warning)

    {

        TextView warn=(TextView) findViewById(R.id.user_name_warning);

        warn.setText(warning);

    }

    public void show_password_warn(String warning)

    {

        TextView warn=(TextView) findViewById(R.id.Password_warning);

        warn.setText(warning);

    }
    public void showWarnSweetDialog(String warning)

    {

        TextView warn=(TextView) findViewById(R.id.warning);

        warn.setText(warning);

    }


    public class User{
        String name;
        String url;
        String password;
        String phone_number;
        String gender;
        String mode;
        ProgressDialog loading_dialog;

        public void  setProgessDialog(ProgressDialog dialog)
        {
            this.loading_dialog=dialog;
        }
    }
    @Override
    public void onClick(View v)

    {

        EditText user_name=(EditText) findViewById(R.id.User_name);

        EditText pass_word=(EditText) findViewById(R.id.Pass_word);

        String userName = user_name.getText().toString();

        String passWord = pass_word.getText().toString();

        if(userName.equals("")||passWord.equals(""))

        {

            showWarnSweetDialog("账号密码不能为空");

            return;

        }
        ProgressDialog loading_dialog=new ProgressDialog(Login_page_activity.this);
        loading_dialog.setTitle("waiting for connection");
        loading_dialog.setMessage("loading");
        loading_dialog.show();
        switch (v.getId())

        {

            case R.id.log_in:
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



    private void registeNameWordToServer(final User user_info){

        OkHttpClient client = new OkHttpClient();

        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("username", user_info.name);

        formBuilder.add("password", user_info.password);

        Request request = new Request.Builder().url(user_info.url).post(formBuilder.build()).build();

        final Call call = client.newCall(request);
        //showWarnSweetDialog("等待服务器响应");
        call.enqueue(new Callback()

        {

            @Override

            public void onFailure(Call call, final IOException e)

            {

                runOnUiThread(new Runnable()

                {

                    @Override

                    public void run()

                    {
                        user_info.loading_dialog.dismiss();
                        Log.d("okhttp_error",e.getMessage());
                        Toast error_toast=Toast.makeText(Login_page_activity.this,"Could not connect to server", Toast.LENGTH_LONG);
                        error_toast.setGravity(Gravity.CENTER, 0, 0);
                        error_toast.show();
                        //showWarnSweetDialog("服务器错误");

                    }

                });

            }



            @Override

            public void onResponse(Call call, final Response response) throws IOException

            {
                final String res = response.body().string();
                user_info.loading_dialog.dismiss();
                runOnUiThread(new Runnable()

                {

                    @Override

                    public void run()

                    {
                        try {
                            Log.d("okhttp_error",res);
                            JSONObject res_inform = new JSONObject(res);
                            String message = res_inform.getString("message");
                            String error_code = res_inform.getString("error_code");
                            Toast error_toast=Toast.makeText(Login_page_activity.this,message, Toast.LENGTH_LONG);
                            error_toast.setGravity(Gravity.CENTER, 0, 0);
                            error_toast.show();
                            /*
                            if(user_info.mode=="log_in") {
                                if (error_code.equals("0")) {

                                    show_user_name_warn(message);
                                    showWarnSweetDialog("");
                                } else if (error_code.equals("1")) {

                                    show_password_warn(message);
                                    showWarnSweetDialog("");
                                }else if (error_code.equals("2")) {

                                    showWarnSweetDialog(message);
                                }

                            }
                            else if(user_info.mode=="register")
                            {
                                if (error_code.equals("0")) {

                                    show_user_name_warn(message);
                                    showWarnSweetDialog("");
                                }
                                else if (error_code.equals("2")) {

                                    showWarnSweetDialog(message);
                                }

                            }*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });

            }

        });



    }



    @Override

    protected void onStart()

    {

        super.onStart();

        Log.d(Tag,"onStart");

    }

    @Override

    protected void onResume()

    {

        super.onResume();

        Log.d(Tag,"onResume");

    }

    @Override

    protected void onPause()

    {

        super.onPause();

        Log.d(Tag,"onPause");

    }

    @Override

    protected void onStop()

    {

        super.onStop();

        Log.d(Tag,"onStop");

    }

    @Override

    protected void onDestroy()

    {

        super.onDestroy();

        Log.d(Tag,"onDestroy");

    }

    @Override

    protected void onRestart()

    {

        super.onRestart();

        Log.d(Tag,"onRestart");

    }
}

