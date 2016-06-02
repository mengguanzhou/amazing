package Broadcast;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

@SuppressLint("ResourceAsColor") public class ScanBroadcastReceiver extends BroadcastReceiver {
	/**
	 * 商品
	 */
	private final static int TYPE_PRODUCT = 1;
	/**
	 * 团购商品
	 */
	private final static int TYPE_PRODUCT_TEAM = 2;
	/**
	 * 新闻
	 */
	private final static int TYPE_NEWS = 3;
	

	@Override
	public void onReceive(final Context context, Intent intent) {
		String barcode = intent.getStringExtra("barcode");
		// 是否是http或https开头的
		if (barcode.startsWith("http://") || barcode.startsWith("https://")) {
			// 如果是url,启动浏览器
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setData(Uri.parse(barcode));
			context.startActivity(i);
			return;
		} else {
			ObjectMapper mapper = new ObjectMapper();
			try {
				final ScanCodeData data = mapper.readValue(barcode,
					ScanCodeData.class);
				//获取二维码类型
				switch(data.getType()){
				//处理二维码
				case 0:
				//获取二维码信息
				long id = data.getId();
				
				}
				return;
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}

