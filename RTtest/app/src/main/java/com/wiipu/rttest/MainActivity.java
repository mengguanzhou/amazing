package com.wiipu.rttest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    private SurfaceView mSFV = null;
    private AliVcMediaPlayer mPlayer = null;
    private Button btnStart = null;
    private EditText edtUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("0");
        super.onCreate(savedInstanceState);
        System.out.println("1");

        setContentView(R.layout.activity_main);
        mSFV = (SurfaceView) findViewById(R.id.media_player);
        btnStart = (Button) findViewById(R.id.btn_start);
        edtUrl = (EditText) findViewById(R.id.edt_url);

        System.out.println("2");
        mPlayer =  new AliVcMediaPlayer(getApplicationContext(), mSFV);
        System.out.println("0");
        mPlayer.setPreparedListener(new MediaPlayer.MediaPlayerPreparedListener() {
            @Override
            public void onPrepared() {

            }
        });
        mPlayer.setErrorListener(new MediaPlayer.MediaPlayerErrorListener() {
            @Override
            public void onError(int i, int i1) {

            }
        });
        mPlayer.setInfoListener(new MediaPlayer.MediaPlayerInfoListener() {
            @Override
            public void onInfo(int i, int i1) {

            }
        });
        mPlayer.setSeekCompleteListener(new MediaPlayer.MediaPlayerSeekCompleteListener() {
            @Override
            public void onSeekCompleted() {

            }
        });
        mPlayer.setCompletedListener(new MediaPlayer.MediaPlayerCompletedListener() {
            @Override
            public void onCompleted() {

            }
        });
        mPlayer.setVideoSizeChangeListener(new MediaPlayer.MediaPlayerVideoSizeChangeListener() {
            @Override
            public void onVideoSizeChange(int i, int i1) {

            }
        });
        mPlayer.setBufferingUpdateListener(new MediaPlayer.MediaPlayerBufferingUpdateListener() {
            @Override
            public void onBufferingUpdateListener(int i) {

            }
        });

        System.out.println("3");
        mPlayer.setDefaultDecoder(0);

        System.out.println("4");
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.prepareAndPlay(edtUrl.getText().toString());
            }
        });
    }
}
