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
        dbWrite = db.getWritableDatabase();
        dbRead = db.getReadableDatabase();
    }

    public void clearRecord() {
        dbWrite.delete("record",null,null);
    }

    public boolean addRecord(Record record){
        ContentValues cv = new ContentValues();
//        cv.put("_id",user.get_id());
        cv.put("name",record.getName());
        long flag = 0;
        try{
            flag = dbWrite.insert("record",null,cv);
        }catch (Exception e){
            e.printStackTrace();
        }
//        long flag = dbWrite.insert("record",null,cv);
        return flag == 1;
    }

    public boolean delRecord(int _id){
        int flag = dbWrite.delete("record","_id=?",new String[]{_id+""});
        //TODO del the real file!
        return flag == 1;
    }

    public Cursor getAllRecord(){
//        Cursor cursor = dbRead.query("record", null, null, null, null, null, null);
//        return cursor;
        return dbRead.query("record", null, null, null, null, null, null);
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
