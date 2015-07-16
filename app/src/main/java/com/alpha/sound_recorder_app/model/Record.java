package com.alpha.sound_recorder_app.model;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by huangshihe on 2015/7/15.
 */
public class Record {
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        if(name == null || "".equals(name.trim())){
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int days = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minutes = cal.get(Calendar.MINUTE);
            int seconds = cal.get(Calendar.SECOND);
            int MI = cal.get(Calendar.MILLISECOND);
            name = "" + year + month + days + hour + minutes + seconds + MI + ".3gp";
        }
        return name;
    }

    public void setName(String name) {
        if(name == null || "".equals(name.trim())){
            getName();
        }else{
            this.name = name;
        }
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public int getType() {
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
    private int maxTime;
    private int type;
    private File recordFile;

    //标记是否已经上传
//    private int state;

}
