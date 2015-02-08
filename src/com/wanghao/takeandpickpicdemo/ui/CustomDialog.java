package com.wanghao.takeandpickpicdemo.ui;

import com.wanghao.takeandpickpicdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;


/**
* 
*
*/
public class CustomDialog extends Activity{

    private TextView mexif_name;
    private TextView mexif_size;
    private TextView mexif_date;
    private String exif_name;
    private String exif_size;
    private String exif_date;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setBlurEffect();
		init();
	}

    private void init() {
        setContentView(R.layout.custom_dialog);
        
        Bundle bundle = getIntent().getExtras();
        exif_name = bundle.getString("name");
        exif_size = bundle.getString("size_length")+"¡Á"+bundle.getString("size_width");
        exif_date = bundle.getString("date");
        if(TextUtils.isEmpty(exif_date)){
        	exif_date="ÎÞ¼ÇÂ¼";
        }

        this.mexif_name = (TextView) findViewById(R.id.exif_name);
        this.mexif_name.setText(exif_name);
        
        this.mexif_size = (TextView) findViewById(R.id.exif_size);
        this.mexif_size.setText(exif_size);
        
        this.mexif_date = (TextView) findViewById(R.id.exif_date);
        this.mexif_date.setText(exif_date);
        
    }


}