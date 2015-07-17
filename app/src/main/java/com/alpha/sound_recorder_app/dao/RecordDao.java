package com.alpha.sound_recorder_app.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alpha.sound_recorder_app.model.Record;
import com.alpha.sound_recorder_app.util.Global;

import java.io.File;

/**
 * Created by huangshihe on 2015/7/16.
 */
public class RecordDao {
    private Db db;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;

    public RecordDao(Context context){
        this.db = new Db(context);
        dbWrite = db.getWritableDatabase();
        dbRead = db.getReadableDatabase();
    }

    public void clearRecord() {
        dbWrite.delete("record",null,null);
    }

    public boolean addRecord(Record record){

        Cursor cursor = getAllRecord();
        int _id = 0;
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            _id = cursor.getInt(cursor.getColumnIndex("_id"));
        }
        ContentValues cv = new ContentValues();
        cv.put("_id",_id + 1);
        cv.put("name",record.getName());
        long flag = 0;
        try{
            flag = dbWrite.insert("record",null,cv);
        }catch (Exception e){
            e.printStackTrace();
        }
//        long flag = dbWrite.insert("record",null,cv);
        return flag == (_id + 1);
    }

    public boolean delRecord(int _id){
        Cursor cursor = dbRead.query("record",null,"_id=?",new String[]{""+_id},null,null,null);

        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            File file = new File(Global.PATH + name);
            file.delete();
        }
        int flag = dbWrite.delete("record","_id=?",new String[]{_id+""});
        return flag == 1;
    }

    public Cursor getAllRecord(){
        return dbRead.query("record", null, null, null, null, null, "_id desc");
    }

    public void close(){
        if(dbWrite != null && dbWrite.isOpen()){
            dbWrite.close();
        }
        if(dbRead != null && dbRead.isOpen()){
            dbRead.close();
        }
//        if(!cursor.isClosed()){
//            cursor.close();
//        }

        if(db != null){
            db.close();
        }
    }

}
