package com.example.barcodeuploadapp;

import java.util.Calendar;

import sqlite.DbHelper;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wiipu.shopping.barcode.view.ViewfinderView;

public class MainActivity extends Activity implements OnClickListener{

	private Button upload,capture;
	private int barcode_request = 0;
	private DbHelper dbhelper;
	private Button add;
	private EditText edit;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upload = (Button) findViewById(R.id.button_upload);
        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(this);
        upload.setOnClickListener(this);
        dbhelper = new DbHelper(this, "db_bwl", null, 1);
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);
        edit = (EditText) findViewById(R.id.edittext);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button_capture:
			CaptureActivity.startActivityForResult(this, ViewfinderView.LOGISTICS, barcode_request);
			break;
		case R.id.button_upload:
			startActivity(new Intent(MainActivity.this,UploadAffirmActivity.class));
			break;
		case R.id.add:
			String barcode = new String();
			barcode = edit.getText().toString();

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

			value.put("barcode", barcode);
			value.put("time", time);
			db.insert("tb_barcode", null, value);

			Toast.makeText(this, barcode, Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			if(requestCode == barcode_request){
				String barcode = new String();
				barcode = data.getStringExtra("barcode");

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

				value.put("barcode", barcode);
				value.put("time", time);
				db.insert("tb_barcode", null, value);

				Toast.makeText(this, barcode, Toast.LENGTH_SHORT).show();
				
			}
		}
	} 

}
