package com.example.barcodeuploadapp;

import sqlite.DbHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.wiipu.network.NetworkManager;
import com.wiipu.network.beans.ErrorHook;
import com.wiipu.network.beans.JsonReceive;
import com.wiipu.network.beans.ResponseHook;

public class UploadAffirmActivity extends Activity{
	private Button uploadAffirm;
	private Button clear;
	private ListView barcodeList;
	private SQLiteDatabase db;
	private DbHelper dbhelper;
	private SimpleCursorAdapter adapter;
	private Cursor cursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_affirm);
		uploadAffirm = (Button) findViewById(R.id.upload_affirm);
		clear = (Button) findViewById(R.id.button_clear);
		barcodeList = (ListView) findViewById(R.id.barcode_list);
		
		dbhelper = new DbHelper(this, "db_bwl", null, 1);
		db = dbhelper.getReadableDatabase();
		
		cursor = db.query("tb_barcode", new String[]{"id as _id","barcode","time"}, null, null, null, null,null);
		
		adapter = new SimpleCursorAdapter(this, R.layout.item_barcode_list, cursor, 
				new String[]{"barcode","time"}, 
				new int[]{R.id.barcode,R.id.time});
		barcodeList.setAdapter(adapter);
		
		this.registerForContextMenu(barcodeList);
		
		uploadAffirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Cursor cursor_barcode = db.query("tb_barcode", new String[]{"barcode"}, null, null, null, null,null);
				String barcodes = new String();
				StringBuilder builder = new StringBuilder();
				boolean first = true;
				while(cursor_barcode.moveToNext()){
					if(first){
					first = false;
					String s = cursor_barcode.getString(0);
					builder.append(s);
					}
					else{
						String s = cursor_barcode.getString(0);
						builder.append("," + s);
					}
				}
				barcodes = builder.toString();
				
				BarcodeUploadRequest request = new BarcodeUploadRequest();
				request.setBarcode(barcodes);
				NetworkManager.getInstance().post("upload_barcode", request, new ResponseHook() {
					
					@Override
					public void deal(Context arg0, JsonReceive arg1) {
						BarcodeUploadesponse response = new BarcodeUploadesponse();
						response = (BarcodeUploadesponse) arg1.getResponse();
						if(response != null){
							Toast.makeText(UploadAffirmActivity.this, "上传成功", 0).show();
							db.execSQL("DELETE FROM tb_barcode");
							cursor.requery();
							adapter.notifyDataSetChanged();
						}
					}
				}, new ErrorHook() {
					
					@Override
					public void deal(Context arg0, VolleyError arg1) {
						
					}
				} , BarcodeUploadesponse.class);
			}
		});
		
		clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(UploadAffirmActivity.this);
				dialog.setTitle("确认清空");
				dialog.setMessage("您确认要清空列表吗？！");
				dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

						db.execSQL("DELETE FROM tb_barcode");
						cursor.requery();
						adapter.notifyDataSetChanged();
					}
				});
				dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				
				dialog.show();
			}
			});
		
		this.registerForContextMenu(barcodeList);
		
		
	}
	
	public void onCreateContextMenu(ContextMenu menu,View view,ContextMenuInfo menuInfo){
		menu.add(0,3,0,"删除该条码");
		menu.add(0,4,0,"取消");
	}
	
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
		switch(item.getItemId()){
		case 3:
			dbhelper = new DbHelper(this, "db_bwl", null, 1);
			db = dbhelper.getWritableDatabase();
			int status = db.delete("tb_barcode", "id=?", new String[]{""+menuInfo.id});
			if(status!=-1){
				//鍒犻櫎鍚庢洿鏂發istview
				Cursor cursor = db.query("tb_barcode", new String[]{"id as _id","barcode","time"}, null, null, null, null,null);
				adapter.changeCursor(cursor);
				Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "抱歉，操作失败", Toast.LENGTH_LONG).show();
			}
			break;
		case 4:
			break;
			
		}
		return true;
	}
	
}
