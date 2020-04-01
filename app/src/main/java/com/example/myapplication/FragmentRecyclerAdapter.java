package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;

public class FragmentRecyclerAdapter extends RecyclerView.Adapter{
    private Context mContext;
    public List<Adapater_common_type> mData;
    WaterFallAdapter.Recycler_banner_Holder banner_holder;
    RecyclerBanner pager;
    public FragmentRecyclerAdapter(Context context, List<Adapater_common_type> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).get_widget_type();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_widget ,null);
            return new FragmentRecyclerAdapter.Scene_Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FragmentRecyclerAdapter.Scene_Holder) {
            FragmentRecyclerAdapter.Scene_Holder scene_holder = (FragmentRecyclerAdapter.Scene_Holder) holder;
            final Adapter_Scene movie_scene = ( Adapter_Scene) mData.get(position);
            scene_holder.finish.setText(movie_scene.finish);
            scene_holder.start.setText("Start :"+movie_scene.start);
            scene_holder.type.setText(movie_scene.type);
            scene_holder.finish.setText(movie_scene.finish);
            scene_holder.price.setText("¥"+movie_scene.price);
            scene_holder.hall.setText("Projection hall :"+movie_scene.hall);
            scene_holder.purchase.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    String movie_name=movie_scene.name;
                    Intent intent=new Intent(mContext,MovieInfo_page_activity.class);
                    intent.putExtra("movie_name",movie_name);
                    mContext.startActivity(intent);
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


    public class Scene_Holder extends RecyclerView.ViewHolder {
        Button purchase;
        TextView hall;
        TextView start;
        TextView finish;
        TextView type;
        TextView price;
        public Scene_Holder(View itemView) {
            super(itemView);
            purchase = (Button) itemView.findViewById(R.id.select_seat);
            hall=(TextView )itemView.findViewById(R.id.movie_hall);
            start=(TextView )itemView.findViewById(R.id.start_time);
            finish=(TextView )itemView.findViewById(R.id.finish_time);
            type=(TextView )itemView.findViewById(R.id.movie_type);
            price=(TextView )itemView.findViewById(R.id.price);
        }
    }

}
