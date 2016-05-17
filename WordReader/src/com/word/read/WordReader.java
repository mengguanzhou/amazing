package com.word.read;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.textmining.text.extraction.WordExtractor;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class WordReader extends Activity {
    /** Called when the activity is first created. */
	
	private TextView text;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        text = (TextView) findViewById(R.id.text);
        
        String str = readWord("/sdcard/宝晶宫.doc");
        text.setText(str.trim().replace("\r", ""));
    }
    
    public String readWord(String file){
    	// 创建输入流读取doc文件
		FileInputStream in;
		String text = null;
		try {
			in = new FileInputStream(new File(file));
			WordExtractor extractor = null;
			// 创建WordExtractor
			extractor = new WordExtractor();
			// 对doc文件进行提取
			text = extractor.extractText(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
    }
}