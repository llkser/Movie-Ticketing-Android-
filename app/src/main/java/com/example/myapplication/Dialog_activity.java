package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Dialog_activity extends AppCompatActivity {
    public String movie_name ;
    public String id ;
    public String user_name ;
    public int is_generated=0;
    public int timeout=0;
    private String password;
    private String price;
    private String nums;
    private String Balance;
    private static final  int REQUEST = 1;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity);
        getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        Intent intent = getIntent();
        movie_name=intent.getStringExtra("seat_nums");
        id=intent.getStringExtra("id");
        price=intent.getStringExtra("price");
        nums= intent.getStringExtra("nums");
        Balance=intent.getStringExtra("balance");

        AndroidDatabase androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
         Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.moveToFirst())
        {
            user_name = cursor.getString(cursor.getColumnIndex("Username"));
        }

        TextView order_info=(TextView)findViewById(R.id.order_information);
        order_info.setText(nums+" movie tickets");
        TextView order_user=(TextView)findViewById(R.id.order_user_name);
        order_user.setText(user_name);
        TextView accounts=(TextView)findViewById(R.id.Accounts_payable);
        accounts.setText("¥"+price);
        TextView balance=(TextView)findViewById(R.id.balance);
        balance.setText("¥"+Balance);

        Button button=(Button)findViewById(R.id.confirm_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(Balance)<Integer.parseInt(price))
                {
                    AlertDialog dialog=new AlertDialog.Builder(Dialog_activity.this).setTitle("Your balance is not enough")
                            .setMessage("Do you want to recharge?")
                            .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
                else{
                    is_generated = 1;
                    Intent intent = new Intent(Dialog_activity.this, password_Activity.class);
                    Dialog_activity.this.startActivityForResult(intent, REQUEST);
                }

            }
        });
        ImageView img=(ImageView) findViewById(R.id.cancel_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cancel_order();
                finish();
            }
        });
        timer=new Timer();

        /*timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeout=1;
                Log.d("okhttp_error", "mmp");
                finish();
            }
        },60,60);*/


    }
    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                 // TODO Auto-generated method stub
                 super.onActivityResult(requestCode, resultCode, data);
                 if(resultCode==2){
                        if(requestCode==REQUEST){
                                 password= data.getStringExtra("password");
                                 Log.d("okhttp_error", password);
                                 Generate_order();
                             }
                     }
                 else if(resultCode==1)
                     if(requestCode==REQUEST){
                         Cancel_order();
                         finish();
                 }
             }
   public void Generate_order()
   {

       OkHttpClient client = new OkHttpClient();
       FormBody.Builder formBuilder = new FormBody.Builder();

       formBuilder.add("seat_nums", movie_name);
       formBuilder.add("id", id);
       formBuilder.add("user_name", user_name);
       formBuilder.add("password", password);
       Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/generate_order").post(formBuilder.build()).build();

       final Call call = client.newCall(request);
       call.enqueue(new Callback() {
           @Override
           public void onFailure(Call call, final IOException e) {
               runOnUiThread(new Runnable() {

                   @Override

                   public void run() {
                       Log.d("okhttp_error", e.getMessage());
                       Toast error_toast = Toast.makeText(Dialog_activity.this, "Could not connect to server", Toast.LENGTH_LONG);
                       error_toast.setGravity(Gravity.CENTER, 0, 0);
                       error_toast.show();
                       Generate_order();
                   }

               });
           }

           @Override
           public void onResponse(Call call, final Response response) throws IOException {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {

                       try {
                           final String res = response.body().string();
                           JSONObject res_inform = null;
                           res_inform = new JSONObject(res);
                           String result = res_inform.getString("result");
                           String serial_number = res_inform.getString("serial_number");
                           String error_code= res_inform.getString("errorcode");

                           AndroidDatabase androidDatabase = new AndroidDatabase(Dialog_activity.this, "Shield.db", null, 1);
                           final SQLiteDatabase db = androidDatabase.getWritableDatabase();
                           ContentValues values = new ContentValues();
                           values.put("serial_number", serial_number);
                           db.update("Movie", values, "movie_id=?",
                                   new String[]{id});

                           Toast error_toast = Toast.makeText(Dialog_activity.this, result, Toast.LENGTH_LONG);
                           error_toast.setGravity(Gravity.CENTER, 0, 0);
                           error_toast.show();
                           if(error_code.equals("0")||error_code.equals("1")) {
                               Intent intent = new Intent(Dialog_activity.this, Order_page_activity.class);
                               Dialog_activity.this.startActivity(intent);
                               finish();
                           }
                           else
                           {
                               Intent intent = new Intent(Dialog_activity.this, password_Activity.class);
                               Dialog_activity.this.startActivityForResult(intent,REQUEST);
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }

               });
           }
       });
   }

    public void Cancel_order()
    {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("seat_nums", movie_name);
        formBuilder.add("id", id);
        Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/cancel_order").post(formBuilder.build()).build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {
                        Log.d("okhttp_error", e.getMessage());

                        Cancel_order();
                    }

                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {
                    }

                });
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(is_generated==0) {
            Cancel_order();
            finish();
        }
        timer.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(is_generated==0) {
            Cancel_order();
            finish();
        }
        else
        timer.cancel();
    }
}
