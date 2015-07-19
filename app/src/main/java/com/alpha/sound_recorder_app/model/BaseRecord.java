package com.alpha.sound_recorder_app.model;

import java.io.File;
import java.util.Date;

/**
 * Created by huangshihe on 2015/7/19.
 */
public interface BaseRecord {
    public String getRecordTime();

    public void startRecord();

    public void stopRecord();

    public void onPause();

    public String getName();

    public void setName(String name);

    public void setRecordFile(File recordFile);

    public void setCreateTime(Date createTime);
    public Date getCreateTime();

    public long getLength();
    public void setLength(long length);

}
