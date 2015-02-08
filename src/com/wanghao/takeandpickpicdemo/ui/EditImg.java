package com.wanghao.takeandpickpicdemo.ui;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.edmodo.cropper.CropImageView;
import com.wanghao.takeandpickpicdemo.R;
import com.wanghao.takeandpickpicdemo.dao.MyApp;
import com.wanghao.takeandpickpicdemo.utils.ImageTools;

public class EditImg extends Activity{

	private CropImageView cropImageView;
	private ImageView icon_close;
	private ImageView icon_ok;
	private ImageView icon_rotate_left;
	private ImageView icon_rotate_right;
	private OnClickListener imgHandleListener;
	private static final int NEGATIVE_NINETY_DEGREES = -90;
	private static final int PLUS_NINETY_DEGREES = 90;
	private String uri;
	private MyApp myApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_img);
		
		myApp = (MyApp)getApplication();
		cropImageView = (CropImageView)findViewById(R.id.rotateImageView);
		icon_close = (ImageView)findViewById(R.id.icon_close);
		icon_ok = (ImageView)findViewById(R.id.icon_ok);
		icon_rotate_left = (ImageView)findViewById(R.id.icon_rotate_left);
		icon_rotate_right = (ImageView)findViewById(R.id.icon_rotate_right);
		
		uri = getIntent().getStringExtra("uri");
		Bitmap	handledBitmap = null;
		//获取长宽
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(uri,options);
		//精简图片，节省内存
		int height = options.outHeight;
		int width = options.outWidth;
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float swidth = dm.widthPixels;
		float sheight = dm.heightPixels;
		if(height<width){
			options.inSampleSize = (int)(width/swidth);
		}else{
			options.inSampleSize = (int)(height/sheight);
		}
		options.inJustDecodeBounds = false;
		handledBitmap = BitmapFactory.decodeFile(uri,options);
		cropImageView.setImageBitmap(handledBitmap);
		
		imgHandleListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch(v.getId()){
					case R.id.icon_close:
						finish();
						break;
					case R.id.icon_ok:
						myApp.setPicChanged(true);
						Bitmap croppedImage = cropImageView.getCroppedImage();
						String photoName = uri.substring(uri.lastIndexOf("/")+1, uri.length());
						String path = uri.substring(0, uri.lastIndexOf("/"));
						ImageTools.savePhotoToSDCard(path, photoName, croppedImage);
						finish();
						break;
					case R.id.icon_rotate_left:
						cropImageView.rotateImage(NEGATIVE_NINETY_DEGREES);
						break;
					case R.id.icon_rotate_right:
						cropImageView.rotateImage(PLUS_NINETY_DEGREES);
						break;
				
				}
			}
		};
		icon_close.setOnClickListener(imgHandleListener);
		icon_ok.setOnClickListener(imgHandleListener);
		icon_rotate_left.setOnClickListener(imgHandleListener);
		icon_rotate_right.setOnClickListener(imgHandleListener);
	
	}

}
