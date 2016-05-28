package com.example.barcodeuploadapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.wiipu.network.NetworkManager;
import com.wiipu.network.utils.Constants;
import com.wiipu.network.utils.LogType;
import com.wiipu.network.utils.LogUtil;

public class MyApplication extends Application {
	static {
		application = new MyApplication();
	}
	/**
	 * unbind service 时使用的一个回调对象
	 */
	private ServiceConnection connection;
	/**
	 * 静态的全局对象
	 */
	private static MyApplication application;

	/**
	 * @return 返回一个application
	 */
	public MyApplication getInstance() {
		return application;
	}

	public static void init() {

	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 初始化网络请求队列
		NetworkManager.getInstance().init(getApplicationContext());
		// 初始化网络请求参数
		Constants.URL = "http://112.124.3.197:8011/app/method/app_bound.php";
		Constants.APPKEY = "888";
		Constants.SECRET = "753159842564855248546518489789";
		// 初始化Log打印默认的TAG
		LogUtil.TAG = "";
		// 初始化Log打印的等级
		LogUtil.LEVEL = LogType.DEBUG;
		// bind时使用的回调对象
		connection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {

			}
		};

		bindService(new Intent(this, ListenNetWorkStateService.class),
				connection, BIND_AUTO_CREATE);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		unbindService(connection);
	}

}
