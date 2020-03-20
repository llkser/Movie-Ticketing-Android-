package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AndroidDatabase extends SQLiteOpenHelper {
    public static final String CREATE_USER_TABLE="create table User (" +
            "id integer primary key autoincrement, " +
            "Username text, " +
            "Password text, " +
            "Email text, " +
            "PasswordIsRemembered integer, " +
            "Islogin integer default 0) ";

    public static final String CREATE_MOVIR_INFO_TABLE="create table Movie (" +
            "movie_id integer primary key , " +
            "movie_name text COLLATE NOCASE, " +
            "movie_type text, " +
            "introduction text, " +
            "movie_length text,"+
            "special_effect text,"+
            "comments text,"+
            "country text,"+
            "actors text,"+
            "director text,"+
            "premiere_date text,"+
            "date text,"+
            "start_time text,"+
            "finish_time text,"+
            "scene text,"+
            "projection_hall text,"+
            "price text,"+
            "cinemas text,"+
            "img_url text,"+
            "score text)";


    private Context mContext;

    public AndroidDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name,factory,version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOVIR_INFO_TABLE);
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}
}
