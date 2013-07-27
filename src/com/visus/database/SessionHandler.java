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
	private final static String KEY_DATE = "Date";
	private final static String KEY_TIME = "Time";
	private final static String KEY_DURATION = "Duration";
	
	public SessionHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createSessionsTable = "CREATE TABLE " + TABLE_SESSIONS + 
				                     " ( " +
				                     	KEY_ID + " INTEGER PRIMARY KEY, " + 
				                        KEY_USER_ID + " INTEGER, " +
				                     	KEY_DATE + " TEXT, " +
				                        KEY_TIME + " TEXT, " +
				                        KEY_DURATION + " TEXT" +
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
		sessionValues.put(KEY_DATE, session.getDate());
		sessionValues.put(KEY_TIME, session.getTime());
		sessionValues.put(KEY_DURATION, session.getDuration());
		
		db.insert(TABLE_SESSIONS, null, sessionValues);
		Log.e("Visus", "New session added");
		db.close();
	}
	
}
