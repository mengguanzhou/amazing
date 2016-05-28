package Util;

import android.R;
import android.app.Activity;

import com.readystatesoftware.systembartint.SystemBarTintManager;


public class StatusBarUtil {
	
	/**
	 * ÉèÖÃ»ÒÉ«³Á½şÊ½×´Ì¬À¸
	 * @param activity
	 */
	public static final void setTransparentStatusBar(Activity activity){
		SystemBarTintManager tintManager = new SystemBarTintManager(activity);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintColor(activity.getResources().getColor(R.color.darker_gray));
	}

}