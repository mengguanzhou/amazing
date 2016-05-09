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
        
        String str = readWord("/sdcard/������.doc");
        text.setText(str.trim().replace("\r", ""));
    }
    
    public String readWord(String file){
    	// ������������ȡdoc�ļ�
		FileInputStream in;
		String text = null;
		try {
			in = new FileInputStream(new File(file));
			WordExtractor extractor = null;
			// ����WordExtractor
			extractor = new WordExtractor();
			// ��doc�ļ�������ȡ
			text = extractor.extractText(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
    }
}