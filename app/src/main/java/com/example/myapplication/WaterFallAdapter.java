package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
    public List<Adapater_common_type> mData;
    Recycler_banner_Holder banner_holder;
    RecyclerBanner pager;
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
            final Movie_card movie_card = (Movie_card) mData.get(position);
            //savePhoto(movie_card.img_url);
            Uri uri = Uri.parse(movie_card.img_url);
            Log.d("okhttp_error", movie_card.img_url);
            movie_holder.Movie_img.setImageURI(uri);
            movie_holder.Movie_img.getLayoutParams().height = 400;
            movie_holder.Movie_name.setText(movie_card.name);
            movie_holder.movie_length.setText("length :"+movie_card.length);
            movie_holder.movie_actors.setText("Actors :"+movie_card.actors);
            movie_holder.movie_release_data.setText("Premiere_date :"+movie_card.release_data);
            movie_holder.movie_score.setText(movie_card.score+"/10");
            movie_holder.movie_type.setText(movie_card.special_effect);

            movie_holder.Movie_info.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    String movie_name=movie_card.name;
                    Intent intent=new Intent(mContext,MovieInfo_page_activity.class);
                    intent.putExtra("movie_name",movie_name);
                    mContext.startActivity(intent);
                }
            });

        }
        else
        {
            banner_holder=(Recycler_banner_Holder)holder;

            Adapter_recycler_banner banner = (Adapter_recycler_banner) mData.get(position);
            banner.init();
            pager.isPlaying=true;
            pager.setDatas(banner.urls);
            pager.setOnPagerClickListener(new RecyclerBanner.OnPagerClickListener() {
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
        public SimpleDraweeView Movie_img;
        public TextView Movie_name;
        public TextView movie_release_data;
        public TextView movie_length;
        public TextView movie_score;
        public TextView movie_type;
        public TextView movie_actors;

        public LinearLayout Movie_info;

        public MovieCardViewHolder(View itemView) {
            super(itemView);
            Movie_img = (SimpleDraweeView) itemView.findViewById(R.id.movie_img);
            Movie_name = (TextView) itemView.findViewById(R.id.movie_name);
            movie_release_data=(TextView) itemView.findViewById(R.id.movie_release_date);
            movie_length=(TextView) itemView.findViewById(R.id.movie_length);
            movie_score=(TextView) itemView.findViewById(R.id.movie_score);
            movie_type=(TextView) itemView.findViewById(R.id.movie_type);
            movie_actors=(TextView) itemView.findViewById(R.id.movie_actor);
            Movie_info=(LinearLayout)itemView.findViewById(R.id.movie_info_card);
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public class Recycler_banner_Holder extends RecyclerView.ViewHolder {
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

    public void removeList(int position){
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void removeAll() {
        int i = mData.size();

        for (int position = 0; position < i; position++)
        {

           // pager.setVisibility();
            mData.remove(0);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }
}