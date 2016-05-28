package com.example.barcodeuploadapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * @author zy
 * 
 *         �?要权�?<uses-permission
 *         android:name="android.permission.ACCESS_NETWORK_STATE"/>
 *         �?要在配置文件中配置service
 * 
 */
public class ListenNetWorkStateService extends Service {
	private final static String TAG = "ListenStateService";
	private final static String CHANGE_MESSAGE = "android.net.conn.CONNECTIVITY_CHANGE";
	private final static String NO_NETWORK = "当前没有活动网络";
	private final static String CONNECTED_NETWORK = "已连�?";
	private final static String DISCONNECTED_NETWORK = "已断�?";

	private ConnectivityManager manager;
	private NetworkInfo info;

	private BroadcastReceiver mReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e(TAG, "receive a broadcast");
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				// 网络状�?�发生改�?
				manager = (ConnectivityManager) context
						.getSystemService(CONNECTIVITY_SERVICE);
				info = manager.getActiveNetworkInfo();

				if (info != null && info.isAvailable()) {
					// 网络名称
					String name = info.getTypeName();
					State state = info.getState();

					Log.e(TAG, "【网络名称�??:" + name + "【状态�??" + state.toString());
					Toast.makeText(
							context,
							(name.equals("mobile") ? "移动数据" : name)
									+ " "
									+ (state.toString().equals("CONNECTED") ? CONNECTED_NETWORK
											: DISCONNECTED_NETWORK),
							Toast.LENGTH_SHORT).show();
				} else if (info == null) {
					// 当前没有活动网络
					Toast.makeText(context, NO_NETWORK, Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "service created");
		IntentFilter filter = new IntentFilter(CHANGE_MESSAGE);
		registerReceiver(mReciever, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReciever);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

}
