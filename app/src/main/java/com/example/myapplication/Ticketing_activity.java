package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Ticketing_activity extends AppCompatActivity {
    public static List<Adapater_common_type> seat_info_list = new ArrayList<Adapater_common_type>();
    public static List<Integer> seat_nums = new ArrayList<Integer>();
    public static String price;
    public static TextView priceText;
    public String id;
    FragmentRecyclerAdapter mAdapter;
    FragmentRecyclerAdapter mAdapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketing_activity);
        Intent intent = getIntent();
        final String movie_name = intent.getStringExtra("movie_name");
        price = intent.getStringExtra("movie_price");
        id=intent.getStringExtra("movie_id");
        String serial_number=intent.getStringExtra("serial_number");
        Log.d("test2", movie_name);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        priceText = (TextView) findViewById(R.id.price);

        RecyclerView mRecyclerView;

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_for_ticketing);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);

        List<Adapater_common_type> seat_list = new ArrayList<Adapater_common_type>();
        for (int i = 0; i < 40; i++) {
            boolean selected=false;
            if (serial_number.charAt(i)=='1')
                selected=true;
            Adapter_seat seat = new Adapter_seat(i, selected, false);
            seat_list.add(seat);
        }

        mAdapter = new FragmentRecyclerAdapter(Ticketing_activity.this, seat_list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        RecyclerView mRecyclerView2;
        mRecyclerView2 = (RecyclerView) findViewById(R.id.seat_info_recycler_view);
        StaggeredGridLayoutManager mLayoutManager2 = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);

        mAdapter2 = new FragmentRecyclerAdapter(Ticketing_activity.this, seat_info_list);
        mRecyclerView2.setLayoutManager(mLayoutManager2);
        mRecyclerView2.setAdapter(mAdapter2);
        mAdapter.getAdapter(mAdapter2);

        Button button=(Button)findViewById(R.id.buy_tickets_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request_seat();
            }
        });
    }

    public void  Request_seat(){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        int i=0;
        String nums="";
        for (Integer num:seat_nums)
        {
            if(i==0) {
                nums += num;
                i=1;
            }
            else
                nums+="."+num;
        }
        formBuilder.add("seat_nums", nums);
        formBuilder.add("id", id);
        Request request = new Request.Builder().url("http://192.168.1.103:5000/appnet/select_seat").post(formBuilder.build()).build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {
                        Log.d("okhttp_error", e.getMessage());
                        Toast error_toast = Toast.makeText(Ticketing_activity.this, "Could not connect to server", Toast.LENGTH_LONG);
                        error_toast.setGravity(Gravity.CENTER, 0, 0);
                        error_toast.show();
                    }

                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                seat_info_list.clear();
                seat_nums.clear();
                mAdapter.notifyDataSetChanged();
                mAdapter2.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        seat_info_list.clear();
        seat_nums.clear();
        mAdapter.notifyDataSetChanged();
        mAdapter2.notifyDataSetChanged();
    }

}
