package com.example.readandwritedoc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.FieldsDocumentPart;
import org.apache.poi.hwpf.usermodel.Field;
import org.apache.poi.hwpf.usermodel.Fields;
import org.apache.poi.hwpf.usermodel.Range;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Resources;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Map<String, String> map=new HashMap<String, String>();
		map.put("title", "要不要换个网名的博客");
		map.put("blog_name", "l1976135784");
		map.put("domain_name", "http://blog.csdn.net/laijunpeng");
		map.put("description", "做一天程序撞一天钟，平常的学习笔记，IT博客。");
		this.writeDoc(map);
	}

	public void writeDoc(Map<String, String> map) {
		try {
			//读取word模板
			Resources res = super.getResources();  
			InputStream in = res.openRawResource(R.raw.ljp);
			HWPFDocument hdt = new HWPFDocument(in);
			Fields fields = hdt.getFields();
			Iterator<Field> it = fields.getFields(FieldsDocumentPart.MAIN).iterator();
			while(it.hasNext()){
				System.out.println(it.next().getType());
			}
			
			//读取word文本内容
			Range range = hdt.getRange();
			System.out.println(range.text());
			//替换文本内容
			for (Map.Entry<String,String> entry:map.entrySet()) {
				range.replaceText(entry.getKey(),entry.getValue());
			}    
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			String fileName = ""+System.currentTimeMillis();
			fileName += ".doc";
			FileOutputStream out = new FileOutputStream("/sdcard/"+fileName,true);
			Toast.makeText(this, "成功保存到sdcard目录下", Toast.LENGTH_LONG).show();
			hdt.write(ostream);
            //输出字节流
			out.write(ostream.toByteArray());
			out.close();
			ostream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
