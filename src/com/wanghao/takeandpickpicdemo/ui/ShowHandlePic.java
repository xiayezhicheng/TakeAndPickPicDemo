package com.wanghao.takeandpickpicdemo.ui;

import java.io.File;
import java.io.IOException;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wanghao.takeandpickpicdemo.R;
import com.wanghao.takeandpickpicdemo.dao.MyApp;
import com.wanghao.takeandpickpicdemo.dao.PicsDataHelper;
import com.wanghao.takeandpickpicdemo.ui.MainActivity.PictureChangeHandler;


public class ShowHandlePic extends Activity {

	private static final int PIC_CHANGED = 0x0010;  
	
	private ImageView showedimage;
    private PhotoViewAttacher mAttacher;
    private RelativeLayout handle_bar;
    private ImageView img_edit;
    private ImageView img_info;
    private ImageView img_delete;
    private String uri;
    private Bundle bundle = null;
    private ProgressDialog pd;
    private MyApp myApp; 
    private PicsDataHelper picsDataHelper;
    private PictureChangeHandler pictureChangeHandler;
    public boolean isPicChanged;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		myApp = (MyApp)getApplication();
		picsDataHelper = new PicsDataHelper(MyApp.getContext());
		init();
	}
	
    @Override
	protected void onRestart() {
		super.onRestart();
		init();
	}

	private void init() {
        setContentView(R.layout.showhandle);
        
        pictureChangeHandler = myApp.getHandler();
        bundle = new Bundle();
        handle_bar = (RelativeLayout)findViewById(R.id.handle_bar);
        img_edit = (ImageView)findViewById(R.id.img_edit);
        img_info = (ImageView)findViewById(R.id.img_info);
        img_delete = (ImageView)findViewById(R.id.img_delete);
        
        showedimage = (ImageView)findViewById(R.id.showedimage); 
        uri = getIntent().getStringExtra("uri");
        showedimage.setImageURI(Uri.parse(uri));
        
        mAttacher = new PhotoViewAttacher(showedimage);
        mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
			
			@Override
			public void onPhotoTap(View view, float x, float y) {
		 		if(handle_bar.getVisibility()==View.VISIBLE){
        			handle_bar.setVisibility(View.GONE);
        		}else{
        			handle_bar.setVisibility(View.VISIBLE);
        		}
			}
		});
        
        OnClickListener handlePicListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.img_edit:
					Intent intent_edit = new Intent(ShowHandlePic.this,EditImg.class);
					intent_edit.putExtra("uri", uri);
					startActivity(intent_edit);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					break;
				case R.id.img_info:
					Intent intent_info = new Intent(ShowHandlePic.this, CustomDialog.class);
					bundle = getPicExif(uri);
					intent_info.putExtras(bundle);
					startActivity(intent_info);
					break;
				case R.id.img_delete:
					File file = new File(uri);
					file.delete();
					picsDataHelper.deletePic(uri);
					pictureChangeHandler.sendEmptyMessage(PIC_CHANGED);
					pd = ProgressDialog.show(ShowHandlePic.this,null, "…æ≥˝÷–£¨«Î…‘∫Û...",true);
					pd.show();
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							showedimage.setImageBitmap(null);
							pd.dismiss();
							ShowHandlePic.this.finish();
						}
					}, 1500);
					break;
				default:
					break;
				}
			}
		};
		img_edit.setOnClickListener(handlePicListener );
		img_info.setOnClickListener(handlePicListener );
		img_delete.setOnClickListener(handlePicListener );
		
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(myApp.isPicChanged()){
			if(keyCode==KeyEvent.KEYCODE_BACK){
					pictureChangeHandler.sendEmptyMessage(PIC_CHANGED);
					this.finish();
			}
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}

	private Bundle getPicExif(String filename){
		try {
			ExifInterface exif = new ExifInterface(filename);
			if(exif!=null){
				String name = filename.substring(filename.lastIndexOf("/")+1, filename.length());
				bundle.putString("name", name);
				String length = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
				bundle.putString("size_length", length);
				String width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
				bundle.putString("size_width", width);
				String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
				bundle.putString("date", date);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bundle;
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAttacher.cleanup();
	}
	
}
