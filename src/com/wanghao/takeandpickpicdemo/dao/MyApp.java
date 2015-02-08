package com.wanghao.takeandpickpicdemo.dao;

import com.wanghao.takeandpickpicdemo.ui.MainActivity.PictureChangeHandler;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application{

	private PictureChangeHandler handler=null;
	private boolean isPicChanged = false;
	private static Context context;
	
	public PictureChangeHandler getHandler() {
		return handler;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
	}
	
	public static Context getContext(){
		return context;
	}
	
	public void setHandler(PictureChangeHandler handler) {
		this.handler = handler;
	}
	public boolean isPicChanged() {
		return isPicChanged;
	}
	public void setPicChanged(boolean isPicChanged) {
		this.isPicChanged = isPicChanged;
	}

}
