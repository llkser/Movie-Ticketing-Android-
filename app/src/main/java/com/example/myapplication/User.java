package com.example.myapplication;

import android.util.Log;
import android.view.Gravity;
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

public class User {
    private void HTTPrequestToServer() {

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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
        });
    }
}
