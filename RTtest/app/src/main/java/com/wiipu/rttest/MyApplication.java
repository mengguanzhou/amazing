package com.wiipu.rttest;

import android.app.Application;

import com.alivc.player.AccessKey;
import com.alivc.player.AccessKeyCallback;
import com.alivc.player.AliVcMediaPlayer;

/**
 * Created by wiipu on 2016/7/15.
 */
public class MyApplication extends Application{

    @Override
    public void onCreate(){
        super.onCreate();
        AliVcMediaPlayer.init(getApplicationContext(), "", new AccessKeyCallback() {
            public AccessKey getAccessToken() {
                return new AccessKey("1WfwzZRF4gsrYGrk", "");
            }
        });
    }
}
