package com.alpha.sound_recorder_app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alpha.sound_recorder_app.model.User;

/**
 * Created by huangshihe on 2015/7/15.
 */
public class UserDao {
    private Db db;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private Cursor cursor;

    public UserDao(Db db){
        this.db = db;
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();
    }

    public boolean insertTest(){
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("_id",1);
        cv.put("username","huangshihe");
        cv.put("password", "123456");
        long flag = dbWrite.insert("user", null, cv);
//        dbWrite.close();
        return flag == 1;
    }

    public void queryTest(){
//        dbRead.query("user",new String[]{"id,username"},"username=?",new String[]{"huangshihe"},"groupby","having","orderBy");
        cursor = dbRead.query("user",null,null,null,null,null,null);
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            System.out.println(String.format("query result::id=%d,username=%s,password=%s",id,username,password));
        }
//        cursor.close();
//        dbRead.close();
    }

    public Cursor getAllUser(){
        return dbRead.query("user",null,null,null,null,null,null);
    }

    public boolean addUser(User user){
        ContentValues cv = new ContentValues();
//        cv.put("_id",user.get_id());
        cv.put("username",user.getUsername());
        long flag = dbWrite.insert("user",null,cv);
        return flag == 1;
    }

    public boolean delUser(int _id){
        int flag = dbWrite.delete("user","_id=?",new String[]{_id+""});
        return flag == 1;
    }

    public void close(){
        if(dbWrite.isOpen()){
            dbWrite.close();
        }
        if(dbRead.isOpen()){
            dbRead.close();
        }
        if(!cursor.isClosed()){
            cursor.close();
        }
    }
}
