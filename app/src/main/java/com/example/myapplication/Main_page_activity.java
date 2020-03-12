package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import android.view.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main_page_activity extends AppCompatActivity {
    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    List<String> mPermissionList = new ArrayList<>();
    private void checkPermission() {
        mPermissionList.clear();

        //判断哪些权限未授予
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        /**
         * 判断是否为空
         */
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了

        } else {//请求权限方法
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(Main_page_activity.this, permissions, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private androidx.appcompat.widget.Toolbar toolbar;
    private SearchView mSearchView;
    private AndroidDatabase androidDatabase;
    // private ImageView welcomeImg = null;
    private static final int PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page_layout);
        Fresco.initialize(Main_page_activity.this);
        toolbar=(androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();
        //Log.d("test","ok");
        //db.execSQL("insert into User values(?,?,?,?,?)",new Object[] { null, null, null,null,1 });
        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.getCount()==0)
            Toast.makeText(this,"没有用户登录！",Toast.LENGTH_SHORT).show();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {  menu.clear();
        getMenuInflater().inflate(R.menu.main_page_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        mSearchView = (SearchView) menuItem.getActionView();//加载searchview
        setListener();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {

            case R.id.app_bar_user:
                Log.d("okhttp_error","user clicked");//设置通往用户界面按钮功能
                ;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setListener(){
        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("okhttp_error","word entered");
                return false;
            }

            //当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("okhttp_error","word input");
                return false;
            }
        });
    }

    private int setSpanSize(int position, List<Adapater_common_type> Adapater_common_type) {
        int count;
        if (position%5==0) {
            count = 2;
        } else {
            count = 1;
        }

        return count;
    }

    private void init() {

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);        //设置布局管理器为2列，纵向
        //StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        GridLayoutManager mLayoutManager = new GridLayoutManager(Main_page_activity.this, 1);
       // RequestForMovieInform();
        final List<Adapater_common_type> list = buildData();
        WaterFallAdapter mAdapter = new WaterFallAdapter(this, buildData());
        /*mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return setSpanSize(position,list);
            }
        });*/
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private List<Adapater_common_type> buildData(){
        String[] names = {"kait","bloody","dark moon rising","json","omar"};
        String[] imgUrs = {
                "http://pic.szjal.cn/img/a49210ffdb7a6a7b1c4561295dfcb540.jpg",
                "http://pic.szjal.cn/img/023d29650c1969a6046216744d15ca3b.jpg",
                "http://pic.szjal.cn/img/18e82508237055e6b990143baf1ae8cf.jpg",
                "http://pic.szjal.cn/img/3f4103f3fe93ba41f6ac4fc6c5a64486.jpg",
                "http://pic.szjal.cn/img/53f1886c0494f09d39c9ad146a9f5aaf.jpg"
        };
        List<Adapater_common_type> list = new ArrayList<>();
        Adapter_recycler_banner banner=new Adapter_recycler_banner();
        list.add(banner);
        for(int i=0;i<names.length;i++)
        {
            Movie_card p = new Movie_card();
            p.img_url = imgUrs[i];
            p.name = names[i];
            p.imgHeight = (i % 2)*100 + 400;
            list.add(p);
        }

        return list;
    }

    private List<Adapater_common_type> RequestForMovieInform()
    {
        OkHttpClient client = new OkHttpClient();
        final List<Adapater_common_type> list = new ArrayList<>();
        Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/movie_info").build();

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
                        Log.d("okhttp_error",e.getMessage());
                        Toast error_toast=Toast.makeText(Main_page_activity.this,"Could not connect to server", Toast.LENGTH_LONG);
                        error_toast.setGravity(Gravity.CENTER, 0, 0);
                        error_toast.show();
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
                            Log.d("okhttp_error",res);
                            JSONArray jsonArray = new JSONArray(res);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                // JSON数组里面的具体-JSON对象
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Movie_card movie_card = new Movie_card();
                                movie_card.img_url = jsonObject.getString("img_url");
                                movie_card.name = jsonObject.getString("name");
                                movie_card.release_data=jsonObject.getString("release_data");

                                list.add(movie_card);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });

            }

        });
        return list;
    }

}

