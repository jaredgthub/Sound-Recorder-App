package com.alpha.sound_recorder_app.model;

/**
 * Created by huangshihe on 2015/7/15.
 */
public class User {
    private int _id;
    private int userid;

    private String username;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        if(username == null || "".equals(username.trim())){
            return "unknow";
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "{\"userid\":\""+getUserid() +"\",\"username\":\""+getUsername()+"\"}";
    }
}
