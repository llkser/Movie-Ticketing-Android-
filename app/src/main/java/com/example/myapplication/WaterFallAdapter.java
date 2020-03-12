package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WaterFallAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Adapater_common_type> mData;

    public WaterFallAdapter(Context context, List<Adapater_common_type> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).get_widget_type();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==0)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.header_for_recyclerview, null);
            return new HeaderHolder(view);
        }
        else if(viewType==1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.card_view, null);
            return new MovieCardViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_banner, null);
            return new Recycler_banner_Holder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieCardViewHolder) {
            MovieCardViewHolder movie_holder = (MovieCardViewHolder) holder;
            Movie_card movie_card = (Movie_card) mData.get(position);
            //savePhoto(movie_card.img_url);
            Uri uri = Uri.parse(movie_card.img_url);
            movie_holder.userAvatar.setImageURI(uri);
            movie_holder.userAvatar.getLayoutParams().height = movie_card.imgHeight;
            movie_holder.userName.setText(movie_card.name);
        }
        else
        {
            Recycler_banner_Holder banner_holder=(Recycler_banner_Holder)holder;

            Adapter_recycler_banner banner = (Adapter_recycler_banner) mData.get(position);
            banner.init();
            banner_holder.pager.isPlaying=true;
            banner_holder.pager.setDatas(banner.urls);
            banner_holder.pager.setOnPagerClickListener(new RecyclerBanner.OnPagerClickListener() {
                @Override
                public void onClick(RecyclerBanner.BannerEntity entity) {
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }


    public class MovieCardViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView userAvatar;
        public TextView userName;
        public MovieCardViewHolder(View itemView) {
            super(itemView);
            userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.movie_img);
            userName = (TextView) itemView.findViewById(R.id.movie_name);
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public class Recycler_banner_Holder extends RecyclerView.ViewHolder {
        RecyclerBanner pager;
        public Recycler_banner_Holder(View itemView) {
            super(itemView);
            pager = (RecyclerBanner) itemView.findViewById(R.id.recycler_banner_pics);
        }
    }

    public void savePhoto(final String picUrl) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                getIamge(picUrl);
            }
        }).start();
    }

        private void getIamge(final String url)
        {
            OkHttpUtils.get().url(url).tag(this)
                    .build()
                    .connTimeOut(20000).readTimeOut(20000).writeTimeOut(20000)
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                        }

                        @Override
                        public void onResponse(Bitmap bitmap, int id) {
                            saveBitmap(bitmap,url);
                        }
                    });
        }

    private void saveBitmap(Bitmap bitmap,String picUrl) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "vgmap");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String[] str = picUrl.split("/");
        String fileName = str[str.length - 1];
        File file = new File(appDir, fileName);
        Log.d("okhttp_error",Environment.getExternalStorageDirectory().toString());
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}