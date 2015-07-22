package com.alpha.sound_recorder_app.model;

import com.alpha.sound_recorder_app.util.Global;

import java.io.File;
import java.util.Date;

/**
 * Created by huangshihe on 2015/7/22.
 */
public class Record {
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getType() {
        if(getName() == null || "".equals(getName().trim())){
            //default return awr;
            return Global.TYPE_AWR;
        }else{
            String suffix = getName().substring(getName().lastIndexOf("."),getName().length());
            switch (suffix){
                case "awr":type = Global.TYPE_AWR;break;
                case "wav":type = Global.TYPE_WAV;break;
                default:type = Global.TYPE_AWR;
            }
        }
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public File getRecordFile() {
        return recordFile;
    }

    public void setRecordFile(File recordFile) {
        this.recordFile = recordFile;
    }

    private int _id;
    private String name;
    private Date createTime;
    private long length;
    private int type;
    private File recordFile;
}
