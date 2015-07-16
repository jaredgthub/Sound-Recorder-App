package com.alpha.sound_recorder_app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alpha.sound_recorder_app.model.Record;

/**
 * Created by huangshihe on 2015/7/16.
 */
public class RecordDao {
    private Db db;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private Cursor cursor;

    public RecordDao(Db db){
        this.db = db;
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();

    }

    public void clearRecord(){
        dbWrite.delete("user",null,null);
    }

    public boolean addRecord(Record record){
        ContentValues cv = new ContentValues();
//        cv.put("_id",user.get_id());
        cv.put("name",record.getName());
        long flag = dbWrite.insert("record",null,cv);
        return flag == 1;
    }

    public boolean delRecord(int _id){
        int flag = dbWrite.delete("record","_id=?",new String[]{_id+""});
        //TODO del the real file!
        return flag == 1;
    }

    public Cursor getAllRecord(){
        return dbRead.query("record",null,null,null,null,null,null);
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
