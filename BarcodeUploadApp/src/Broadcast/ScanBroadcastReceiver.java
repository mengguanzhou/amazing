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
	 * ��Ʒ
	 */
	private final static int TYPE_PRODUCT = 1;
	/**
	 * �Ź���Ʒ
	 */
	private final static int TYPE_PRODUCT_TEAM = 2;
	/**
	 * ����
	 */
	private final static int TYPE_NEWS = 3;
	

	@Override
	public void onReceive(final Context context, Intent intent) {
		String barcode = intent.getStringExtra("barcode");
		// �Ƿ���http��https��ͷ��
		if (barcode.startsWith("http://") || barcode.startsWith("https://")) {
			// �����url,���������
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
				//��ȡ��ά������
				switch(data.getType()){
				//�����ά��
				case 0:
				//��ȡ��ά����Ϣ
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

