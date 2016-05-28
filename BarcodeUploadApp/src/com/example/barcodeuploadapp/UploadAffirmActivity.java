package com.example.barcodeuploadapp;

import java.util.Map;

import sqlite.DbHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
							Toast.makeText(UploadAffirmActivity.this, "�ϴ��ɹ�", 0).show();
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
		
		this.registerForContextMenu(barcodeList);
		/*barcodeList.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
					long id){
				final AlertDialog dialog = new AlertDialog.Builder
						(UploadAffirmActivity.this).create();
				dialog.show();
				Window window = dialog.getWindow();
				window.setContentView(R.layout.dialog_delete);
				View delete =  window.findViewById(R.id.dialog_delete);
				View cancle =  window.findViewById(R.id.dialog_cancle);
				delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						db.delete("tb_barcode", "id=?", new String[]{});
						dialog.dismiss();
						Toast.makeText(getApplicationContext(), "ɾ���ɹ�", 0).show();
						db.close();
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
		});*/
		
	}
	
	public void onCreateContextMenu(ContextMenu menu,View view,ContextMenuInfo menuInfo){
		menu.add(0,3,0,"ɾ��������");
		menu.add(0,4,0,"ȡ��");
	}
	
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
		switch(item.getItemId()){
		case 3:
			dbhelper = new DbHelper(this, "db_bwl", null, 1);
			db = dbhelper.getWritableDatabase();
			int status = db.delete("tb_barcode", "id=?", new String[]{""+menuInfo.id});
			if(status!=-1){
				//删除后更新listview
				Cursor cursor = db.query("tb_barcode", new String[]{"id as _id","barcode","time"}, null, null, null, null,null);
				adapter.changeCursor(cursor);
				Toast.makeText(this, "ɾ���ɹ�", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "��Ǹ������ʧ��", Toast.LENGTH_LONG).show();
			}
			break;
		case 4:
			break;
			
		}
		return true;
	}
	
}