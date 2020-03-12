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

    private Context mContext;

    public AndroidDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name,factory,version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}
}
