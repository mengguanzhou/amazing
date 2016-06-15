package com.example.barcodeuploadapp;

import java.util.Calendar;

import sqlite.DbHelper;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

@SuppressLint("ResourceAsColor") public class ScanBroadcastReceiver extends BroadcastReceiver {
	
	private DbHelper dbhelper;
	
	

	@Override
	public void onReceive(final Context context, Intent intent) {
		
		System.out.println("now");
		String barcode = intent.getStringExtra("barcode");
		// 是否是http或https开头的
		System.out.println(barcode);
		dbhelper = new DbHelper(context, "db_bwl", null, 1);
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

		Toast.makeText(context, barcode, Toast.LENGTH_SHORT).show();
		
	}
	
}

