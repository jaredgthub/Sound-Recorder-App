package com.alpha.sound_recorder_app.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by huangshihe on 2015/7/15.
 */
public class Db extends SQLiteOpenHelper{

    public Db(Context context) {
//    public Db(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
        super(context, "alphaDb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user(" +
//                "_id int NOT NULL PRIMARY KEY," +
                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "username varchar(32) DEFAULT 'visitor'," +
                "password varchar(32)) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
