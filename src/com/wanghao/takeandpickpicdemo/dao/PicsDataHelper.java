package com.wanghao.takeandpickpicdemo.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;

import com.wanghao.takeandpickpicdemo.dao.database.Column;
import com.wanghao.takeandpickpicdemo.dao.database.SQLiteTable;
import com.wanghao.takeandpickpicdemo.dao.datahelper.BaseDataHelper;

public class PicsDataHelper extends BaseDataHelper{

	public PicsDataHelper(Context context) {
		super(context);
	}

	@Override
	protected Uri getContentUri() {
		return DataProvider.PICS_CONTENT_URI;
	}

	@Override
	protected String getTableName() {
		return PicsDBInfo.TABLE_NAME;
	}

	public void insert(String picUri) {
		ContentValues values = new ContentValues();
		values.put(PicsDBInfo.URI, picUri);
		insert(values);
	}
	
    public void bulkInsert(List<String> picUris) {
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        for (String picUri : picUris) {
        	ContentValues values = new ContentValues();
        	values.put(PicsDBInfo.URI, picUri);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        bulkInsert(contentValues.toArray(valueArray));
    }
    
    public int deletePic(String picUri) {
    	synchronized (DataProvider.obj) {
    		DataProvider.DBHelper mDBHelper = DataProvider.getDBHelper();
    		SQLiteDatabase db = mDBHelper.getWritableDatabase();
    		int row = db.delete(PicsDBInfo.TABLE_NAME, PicsDBInfo.URI+"=?", new String[]{picUri});
    		return row;
    	}
    }
    
    public int deleteAll() {
        synchronized (DataProvider.obj) {
            DataProvider.DBHelper mDBHelper = DataProvider.getDBHelper();
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            int row = db.delete(PicsDBInfo.TABLE_NAME, null, null);
            return row;
        }
    }
    
    public Cursor query(){
    	synchronized (DataProvider.obj) {
    		DataProvider.DBHelper mDBHelper = DataProvider.getDBHelper();
    		SQLiteDatabase db = mDBHelper.getWritableDatabase();
    		Cursor cursor = db.query(PicsDBInfo.TABLE_NAME, null, null,null,null,null,null);
    		return cursor;
    	}
    }
    
    public CursorLoader getCursorLoader() {
        return new CursorLoader(getContext(), getContentUri(), null, null, null, null);
    }
	
	public static final class PicsDBInfo implements BaseColumns {
        private PicsDBInfo() {
        }

        public static final String TABLE_NAME = "pics";

        public static final String URI = "uri";

        public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME).addColumn(URI, Column.DataType.TEXT);
    }
}
