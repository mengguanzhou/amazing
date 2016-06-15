package com.example.barcodeuploadapp;


import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import sqlite.DbHelper;
import Util.StatusBarUtil;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.wiipu.shopping.barcode.camera.CameraManager;
import com.wiipu.shopping.barcode.decoding.CaptureActivityHandler;
import com.wiipu.shopping.barcode.decoding.InactivityTimer;
import com.wiipu.shopping.barcode.view.ViewfinderView;

/**
 * 使用startActivityForResult()启动该Activity，
 * 在onActivityResult()中，使用intent.getStringExtra("type")获得二维码类型
 * 使用intent.getStringExtra("barcode")获得二维码内容
 *
 */
public class CaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private boolean continueScan = false;
	//扫描的类型，二维码或者是条形码
	private int type = 2;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		StatusBarUtil.setTransparentStatusBar(this);
		// 初始化 CameraManager
		CameraManager.init(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		if(getIntent()!=null){
			//默认是二维码
			viewfinderView.setType(getIntent().getIntExtra("type", 2));
			type = getIntent().getIntExtra("type", 2);
			System.out.println("type1:"+type);
		}
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}
    /**
     * @param context
     * @param type 表示是扫描二维码还是条形码，如果是二维码，type=ViewfindView.BARCODE_2D 如果是条形码 type=ViewfindView.BARCODE
     */
    public static void startActivity(Context context,int type){
    	Intent intent=new Intent(context,CaptureActivity.class);
    	intent.putExtra("type",type);
    	context.startActivity(intent);
    	
    }
    /**
     * @param context
     * @param type 表示是扫描二维码还是条形码，如果是二维码，type=ViewfindView.BARCODE_2D 如果是条形码 type=ViewfindView.BARCODE
     */
    public static void startActivityForResult(Context context,int type,int requestCode){
    	Intent intent=new Intent(context,CaptureActivity.class);
    	intent.putExtra("type",type);
    	System.out.println("type2:"+type);
    	((Activity)context).startActivityForResult(intent, requestCode);	
    }
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(Result obj, Bitmap barcode) {
		System.out.println("obj  "+obj);
		System.out.println("barcode  "+obj.getText());
		inactivityTimer.onActivity();
		viewfinderView.drawResultBitmap(barcode);
		playBeepSoundAndVibrate();
		
		Intent data = new Intent();
		data.putExtra("type", obj.getBarcodeFormat().toString());
		data.putExtra("barcode", obj.getText());
		System.out.println(type);
		if(type != 2){

			DbHelper dbhelper = new DbHelper(this, "db_bwl", null, 1);
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			ContentValues value = new ContentValues();
			
			Calendar c = Calendar.getInstance();
			int day = c.get(Calendar.DAY_OF_MONTH);
			int month = c.get(Calendar.MONTH);
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			String hourOfDay = null;
			String minuteOfHour = null;
			if(hour < 10)
				hourOfDay = "0" + hour;
			else 
				hourOfDay = hour + "";
			if(minute < 10)
				minuteOfHour = "0" + minute;
			else 
				minuteOfHour = minute + "";
				
			String time = hourOfDay + ":" + minuteOfHour + " " + day + "/" + month;

			value.put("barcode", obj.getText());
			value.put("time", time);
			db.insert("tb_barcode", null, value);

			Toast.makeText(this, obj.getText(), Toast.LENGTH_SHORT).show();
			
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			if (hasSurface) {
				initCamera(surfaceHolder);
			} else {
				surfaceHolder.addCallback(this);
				surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}
			
			new Handler().postDelayed(new Runnable(){    
				public void run() {    
					//execute the task 

					if (handler != null)

						handler.restartPreviewAndDecode();
				}    
			}, 1500);   
		}
		else{
			//物流扫码\
			setResult(RESULT_OK, data);	
			

			finish();
		}
		
		
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}