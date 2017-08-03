package com.example.android.track.Model.LitePal_Entity;

import org.litepal.crud.DataSupport;

/**
 * Created by thor on 2017/8/3.
 */

public class Portrait extends DataSupport {
    private int user_id;
    private byte[] portrait;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public byte[] getPortrait() {
        return portrait;
    }

    public void setPortrait(byte[] portrait) {
        this.portrait = portrait;
    }
}
