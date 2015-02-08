package com.wanghao.takeandpickpicdemo.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wanghao.takeandpickpicdemo.R;
import com.wanghao.takeandpickpicdemo.dao.MyApp;
import com.wanghao.takeandpickpicdemo.dao.PicsDataHelper;
import com.wanghao.takeandpickpicdemo.dao.PicsDataHelper.PicsDBInfo;
import com.wanghao.takeandpickpicdemo.utils.ImageTools;
import com.wanghao.takeandpickpicdemo.utils.ScreenTools;


public class MainActivity extends ActionBarActivity {

	private static final int TAKE_PICTURE = 0;
	private static final int PICK_PICTURE = 1;
	private final static int INTERVAL = 12;
	private static final int PIC_CHANGED=0x0010;
	private static int factor ;
	private LinearLayout picasa;
	private ImageView btn_camera;
	private ImageView btn_folder;
	private File picturefile;
	private String photoName;
	private Uri picUri;
	private ImageView bmpThumb =null;
	private ProgressBar progressBar = null;
	private PicsDataHelper picsDataHelper;
	private PictureChangeHandler pictureChangeHandler;
	private MyApp myApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		picsDataHelper = new PicsDataHelper(MyApp.getContext());
		//图片尺寸的决定因子
		factor = ScreenTools.dpToPxInt(getApplicationContext(), 200);
		//创建存储照片的文件
		picturefile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TakeAndPickPic");
		initView();
		initData();
	}
	
	private void initView() {
		
		myApp = (MyApp)getApplication();
		pictureChangeHandler = new PictureChangeHandler();
		
		picasa = (LinearLayout)findViewById(R.id.picasa);
		btn_camera = (ImageView)findViewById(R.id.btn_camera);
		btn_folder = (ImageView)findViewById(R.id.btn_folder);
		
		btn_camera.setOnClickListener(onGetPicListener);
		btn_folder.setOnClickListener(onGetPicListener);
		//加载图片的进度圆
		progressBar = new ProgressBar(getApplicationContext());
	}

	private void initData(){

		Cursor cursor = picsDataHelper.query();
		while(cursor.moveToNext()){
			String column_uri = cursor.getString(cursor.getColumnIndexOrThrow(PicsDBInfo.URI));
			File file = new File(column_uri);
			if(TextUtils.isEmpty(column_uri)||!file.exists()){
				picsDataHelper.deletePic(column_uri);
			}else{
				new BitmapWorkerTask().execute(column_uri);
			}
		}
		cursor.close();
	}
	
	class BitmapWorkerTask extends AsyncTask<String,Void,ImageView>{
		
		@Override
		protected ImageView doInBackground(String... urls) {
				String uri =urls[0];
				bmpThumb = handleBitmap(uri);
			return bmpThumb;
		}
	
		@Override
		protected void onPostExecute(ImageView result) {
				addPic(result);
		}
	}
	
	class loadBitmapTask extends AsyncTask<String, Void, ImageView>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			addPic(progressBar);
		}

		@Override
		protected ImageView doInBackground(String... params) {
			String uriString = params[0];
			Bitmap handledBitmap = ImageTools.getHandledBitmap(factor, uriString);
			int degree = ImageTools.readBitmapDegree(uriString);
			final Bitmap newBitmap = ImageTools.rotateBitmap(degree, handledBitmap);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					ImageTools.savePhotoToSDCard(picturefile.getAbsolutePath(), photoName+".jpg", newBitmap);
				}
			}).start();
			ImageView img = getImgFunction(newBitmap, uriString);
			return img;
		}
		
		@Override
		protected void onPostExecute(ImageView result) {
			super.onPostExecute(result);
			picasa.removeView(progressBar);
			picasa.addView(result); 
		}
		
	}

	OnClickListener onGetPicListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.btn_camera:
					TakePicture();
					break;
				case R.id.btn_folder:
					PickPicture();
					break;
				default:
					break;
			}
		}

		private void TakePicture() {
			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if(!picturefile.exists()) picturefile.mkdir();
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd_HHmmss");
			photoName = dateformat.format(date);
			picUri = Uri.fromFile(new File(picturefile.getAbsoluteFile(),photoName+".jpg"));
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
			startActivityForResult(openCameraIntent,TAKE_PICTURE);
		}
		
		private void PickPicture() {
			Intent pickAlbumIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(pickAlbumIntent, PICK_PICTURE);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, requestCode, data);
		if(resultCode==RESULT_OK){
			switch(requestCode){
				case TAKE_PICTURE:
					String TPicUri = picturefile.getAbsolutePath()+"/"+photoName+".jpg";
					picsDataHelper.insert(TPicUri);
					new loadBitmapTask().execute(TPicUri);
					break;
				case PICK_PICTURE:
					Uri PPicUri = data.getData();
					if(PPicUri!=null){
						Cursor cursor =this.getContentResolver().query(PPicUri, null, null, null, null);
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						String path = cursor.getString(column_index);
						cursor.close();
						ImageView bmpThumbnail = handleBitmap(path);
						addPic(bmpThumbnail);
						picsDataHelper.insert(path);
					}
					break;
				default:
					break;
			}
		}
	}
	
	
	private ImageView handleBitmap(final String uri) {
		
		Bitmap handledBitmap = ImageTools.getHandledBitmap(factor, uri);
		if (handledBitmap==null) {
			picsDataHelper.deletePic(uri);
			return null;
		}
			
		return getImgFunction(handledBitmap, uri);
	}
	
	private ImageView getImgFunction(Bitmap bm, final String uri){
		
		final ImageView imgThumb = new ImageView(getApplicationContext());
		imgThumb.setScaleType(ScaleType.FIT_CENTER);
		imgThumb.setImageBitmap(bm);
		
		imgThumb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				File file = new File(uri);
				if(TextUtils.isEmpty(uri)||!file.exists()){
					Toast.makeText(MainActivity.this, "本地大图已不存在", Toast.LENGTH_SHORT).show();
				}else{
					//初始化因子，判断返回是否需要刷新
					myApp.setPicChanged(false);
					myApp.setHandler(pictureChangeHandler);
					Intent intent = new Intent(MainActivity.this,ShowHandlePic.class);
					intent.putExtra("uri", uri);
					startActivity(intent);
				}
			}
		});
		imgThumb.setOnTouchListener(new SwipeDismissTouchListener(imgThumb, null,
				new SwipeDismissTouchListener.DismissCallbacks() {
					
					@Override
					public void onDismiss(View view, Object token) {
						picasa.removeView(imgThumb);
						picsDataHelper.deletePic(uri);
					}
					@Override
					public boolean canDismiss(Object token) {
						return true;
					}
				}
			)
		);	
		return imgThumb;
	}

	private void addPic(View view) {
		if(view==null) return;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		layoutParams.setMargins(ScreenTools.dpToPxInt(this, INTERVAL), 0, ScreenTools.dpToPxInt(this, INTERVAL), 0);
		picasa.addView(view,layoutParams);
	}

	public class PictureChangeHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case PIC_CHANGED:
					super.handleMessage(msg);
					picasa.removeAllViews();
					initData();
					break;
				default:
					break;
			}
		}
		
	}
}
