package com.alpha.sound_recorder_app.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;

import com.alpha.sound_recorder_app.model.BaseRecord;
import com.alpha.sound_recorder_app.util.Global;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Created by huangshihe on 2015/7/16.
 */
public class RecordDao {
    private Db db;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    Format simpleFormat = new SimpleDateFormat("yyyy-MM-dd");

    public RecordDao(Context context){
        this.db = new Db(context);
        dbWrite = db.getWritableDatabase();
        dbRead = db.getReadableDatabase();
    }

    public void clearRecord() {
        dbWrite.delete("record",null,null);
    }

    public boolean addRecord(BaseRecord record){
        //获得文件的时间和大小
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(Global.PATH + record.getName());
        String length = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//毫秒，String
        record.setLength(Long.parseLong(length));
//        String length = Global.millisecondToDate(recordLen);

        Cursor cursor = getAllRecord();
        int _id = 0;
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            _id = cursor.getInt(cursor.getColumnIndex("_id"));
        }
        ContentValues cv = new ContentValues();
        cv.put("_id",_id + 1);
        cv.put("name", record.getName());
        cv.put("createTime", simpleFormat.format(record.getCreateTime()));
        cv.put("length",record.getLength());

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
        Cursor cursor = dbRead.query("record", null, "_id=?", new String[]{"" + _id}, null, null, null);

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

    public Cursor findRecordByName(String name){
        return dbRead.query("record",null,"name like ?",new String[]{"%"+name+"%"},null,null,"_id desc");
    }

    public boolean updateRecordFileName(int _id,String name){
        ContentValues cv = new ContentValues();
        cv.put("name",name);
        return dbWrite.update("record",cv,"_id=?",new String[]{""+_id}) == 1;
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
