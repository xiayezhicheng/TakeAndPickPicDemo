
package com.wanghao.takeandpickpicdemo.dao;



import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;


public class DataProvider extends ContentProvider { 
		
	public static final Object obj = new Object();
	public static final String AUTHORITY = "com.wanghao.provider";
	public static final String SCHEME = "content://";
	
	   // messages
    public static final String PATH_PICS = "/pics";

    public static final Uri PICS_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_PICS);

    private static final int PICS = 0;

    /*
     * MIME type definitions
     */
    public static final String PIC_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.wanghao.pic";

    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "pics", PICS);
    }
	
	private static DBHelper mDBHelper;
	
	public static DBHelper getDBHelper() {
	    if (mDBHelper == null) {
	        mDBHelper = new DBHelper(MyApp.getContext());
	    }
	    return mDBHelper;
	}
	
	@Override
	public boolean onCreate() {
	    return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
	    synchronized (obj) {
	        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	        queryBuilder.setTables(matchTable(uri));
	
	        SQLiteDatabase db = getDBHelper().getReadableDatabase();
	        Cursor cursor = queryBuilder.query(db,
	                projection,
	                selection,
	                selectionArgs,
	                null,
	                null,
	                sortOrder);
	        cursor.setNotificationUri(getContext().getContentResolver(), uri);
	        return cursor;
	    }
	}
	
	private String matchTable(Uri uri) {
	    String table = null;
	    switch (sUriMatcher.match(uri)) {
		    case PICS:
	            table = PicsDataHelper.PicsDBInfo.TABLE_NAME;
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown Uri " + uri);
	    }
	    return table;
	}
	
	@Override
	public String getType(Uri uri) {
	    switch (sUriMatcher.match(uri)) {
		    case PICS:
	            return PIC_CONTENT_TYPE;
	        default:
	            throw new IllegalArgumentException("Unknown Uri " + uri);
	    }
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
	    synchronized (obj) {
	        SQLiteDatabase db = getDBHelper().getWritableDatabase();
	        long rowId = 0;
	        db.beginTransaction();
	        try {
	            rowId = db.insert(matchTable(uri), null, values);
	            db.setTransactionSuccessful();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            db.endTransaction();
	        }
	        if (rowId > 0) {
	            Uri returnUri = ContentUris.withAppendedId(uri, rowId);
	            getContext().getContentResolver().notifyChange(uri, null);
	            return returnUri;
	        }
	        throw new SQLException("Failed to insert row into " + uri);
	    }
	}
	
	@Override
	public int bulkInsert(Uri uri,ContentValues[] values) {
	    synchronized (obj) {
	        SQLiteDatabase db = getDBHelper().getWritableDatabase();
	        db.beginTransaction();
	        try {
	            for (ContentValues contentValues : values) {
	                db.insertWithOnConflict(matchTable(uri), BaseColumns._ID, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
	            }
	            db.setTransactionSuccessful();
	            getContext().getContentResolver().notifyChange(uri, null);
	            return values.length;
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            db.endTransaction();
	        }
	        throw new SQLException("Failed to insert row into " + uri);
	    }
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
	    synchronized (obj) {
	        SQLiteDatabase db = getDBHelper().getWritableDatabase();
	        int count = 0;
	        db.beginTransaction();
	        try {
	            count = db.delete(matchTable(uri), selection, selectionArgs);
	            db.setTransactionSuccessful();
	        } finally {
	            db.endTransaction();
	        }
	        getContext().getContentResolver().notifyChange(uri, null);
	        return count;
	    }
	}
	
	public static void clearDBCache() {
	    synchronized (DataProvider.obj) {
	        DataProvider.DBHelper mDBHelper = DataProvider.getDBHelper();
	        SQLiteDatabase db = mDBHelper.getWritableDatabase();
	    }
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
	    synchronized (obj) {
	        SQLiteDatabase db = getDBHelper().getWritableDatabase();
	        int count;
	        db.beginTransaction();
	        try {
	            count = db.update(matchTable(uri), values, selection, selectionArgs);
	            db.setTransactionSuccessful();
	        } finally {
	            db.endTransaction();
	        }
	        getContext().getContentResolver().notifyChange(uri, null);
	        return count;
	    }
	}
	
	public static class DBHelper extends SQLiteOpenHelper {
		// 数据库名
	    private static final String DB_NAME = "takeandpickpic.db";
	    // 数据库版本
	    private static final int DB_VERSION = 1;
	
	    private DBHelper(Context context) {
	        super(context, DB_NAME, null, DB_VERSION);
	    }
	
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	PicsDataHelper.PicsDBInfo.TABLE.create(db);
	    }
	
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    }
	}
}
