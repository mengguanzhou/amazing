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
	 * unbind service ʱʹ�õ�һ���ص�����
	 */
	private ServiceConnection connection;
	/**
	 * ��̬��ȫ�ֶ���
	 */
	private static MyApplication application;

	/**
	 * @return ����һ��application
	 */
	public MyApplication getInstance() {
		return application;
	}

	public static void init() {

	}

	@Override
	public void onCreate() {
		super.onCreate();

		// ��ʼ�������������
		NetworkManager.getInstance().init(getApplicationContext());
		// ��ʼ�������������
		Constants.URL = "http://112.124.3.197:8011/app/method/app_bound.php";
		Constants.APPKEY = "888";
		Constants.SECRET = "753159842564855248546518489789";
		// ��ʼ��Log��ӡĬ�ϵ�TAG
		LogUtil.TAG = "";
		// ��ʼ��Log��ӡ�ĵȼ�
		LogUtil.LEVEL = LogType.DEBUG;
		// bindʱʹ�õĻص�����
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
