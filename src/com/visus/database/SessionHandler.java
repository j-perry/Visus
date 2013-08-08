package com.visus.database;

import java.util.List;

import com.visus.entities.Session;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SessionHandler extends SQLiteOpenHelper {

	// important database details 
	private final static String DATABASE_NAME = "Visus";
	private final static int 	DATABASE_VERSION = 1;
	private final static String TABLE_SESSIONS = "Sessions";
	
	// columns
	private final static String KEY_ID = "Id";
	private final static String KEY_USER_ID = "UserId";
	private final static String KEY_DATE_DAY_NO = "DayNo";
	private final static String KEY_DATE_DAY = "Day";
	private final static String KEY_DATE_MONTH = "Month";
	private final static String KEY_DATE_YEAR = "Year";
	private final static String KEY_TIME = "Time";
	private final static String KEY_DURATION = "Duration";
	private final static String KEY_TYPE = "Type";
	
	public SessionHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createSessionsTable = "CREATE TABLE " + TABLE_SESSIONS + 
				                     " ( " +
				                     	KEY_ID + " INTEGER PRIMARY KEY, " + 
				                        KEY_USER_ID + " INTEGER, " +
				                     	KEY_DATE_DAY_NO + " TEXT, " +
				                     	KEY_DATE_DAY + " TEXT, " +
				                     	KEY_DATE_MONTH + " TEXT, " +
				                     	KEY_DATE_YEAR + " TEXT, " +
				                        KEY_TIME + " TEXT, " +
				                        KEY_DURATION + " TEXT" +
				                        KEY_TYPE + " TEXT" +
				                     " )";
		db.execSQL(createSessionsTable);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
 
        // Create tables again
        onCreate(db);	
	}
	
	public void add(Session session) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues sessionValues = new ContentValues();
		
		sessionValues.put(KEY_USER_ID, session.getUserId());
		sessionValues.put(KEY_DATE_DAY_NO, session.getDayNo());
		sessionValues.put(KEY_DATE_DAY, session.getDay());
		sessionValues.put(KEY_DATE_MONTH, session.getMonth());
		sessionValues.put(KEY_DATE_YEAR, session.getYear());
		sessionValues.put(KEY_TIME, session.getTime());
		sessionValues.put(KEY_DURATION, session.getDuration());
		sessionValues.put(KEY_TYPE, session.getType());
		
		db.insert(TABLE_SESSIONS, null, sessionValues);
		Log.e("Visus", "New Session Added");
		db.close();
	}
	
}
