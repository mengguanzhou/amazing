package com.example.barcodeuploadapp;

import sqlite.DbHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
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
	private ListView barcodeList;
	private SQLiteDatabase db;
	private DbHelper dbhelper;
	private SimpleCursorAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_affirm);
		uploadAffirm = (Button) findViewById(R.id.upload_affirm);
		barcodeList = (ListView) findViewById(R.id.barcode_list);
		
		dbhelper = new DbHelper(this, "db_bwl", null, 1);
		db = dbhelper.getReadableDatabase();
		
		Cursor cursor = db.query("tb_barcode", new String[]{"id as _id","barcode","time"}, null, null, null, null,null);
		
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
				Toast.makeText(UploadAffirmActivity.this, barcodes, 0).show();
				
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
						}
					}
				}, new ErrorHook() {
					
					@Override
					public void deal(Context arg0, VolleyError arg1) {
						
					}
				} , BarcodeUploadesponse.class);
			}
		});
		
		barcodeList.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
					final long id){
				final AlertDialog dialog = new AlertDialog.Builder(UploadAffirmActivity.this).create();
				dialog.show();
				Window window = dialog.getWindow();
				window.setContentView(R.layout.photo_choose_dialog);
				Button delete = (Button) findViewById(R.id.delete);
				Button cancle = (Button) findViewById(R.id.cancel);
				delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						db.delete("tb_barcode", "id=?", new String[]{"" + id});
					}
				});
				cancle.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				return true;
			}
		});
		
	}
	
	
	
}
