package com.alpha.sound_recorder_app.model;

/**
 * Created by huangshihe on 2015/7/19.
 */
public interface BaseRecord {
    public String getRecordTime();

    public void startRecord();

    public void stopRecord();

    public void onPause();

    public String getName();

}
