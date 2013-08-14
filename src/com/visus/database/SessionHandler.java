package com.visus.database;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import com.visus.entities.sessions.Overview;
import com.visus.entities.sessions.Session;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//private final static String KEY_DATE_DAY_NO = "DayNo";
//private final static String KEY_DATE_DAY    = "Day";
//private final static String KEY_DATE_MONTH  = "Month";
//private final static String KEY_DATE_YEAR   = "Year";

public class SessionHandler extends SQLiteOpenHelper {

	// important database details 
	private final static String DATABASE_NAME   = "Visus";
	private final static int 	DATABASE_VERSION = 1;
	
	private final static String TABLE_SESSIONS  = "Sessions";
	
	// columns
	private final static String KEY_ID          = "Id";
	private final static String KEY_USER_ID     = "UserId";	
	private final static String KEY_DATE        = "Date";	
	private final static String KEY_TIME        = "Time";
	private final static String KEY_DURATION    = "Duration";
	private final static String KEY_TYPE        = "Type";
	
	public SessionHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.e("Visus", "Session Handler Init");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createSessionsTable = "CREATE TABLE " + TABLE_SESSIONS + 
				                     " ( " +
				                     	KEY_ID + " INTEGER PRIMARY KEY, " + 
				                        KEY_USER_ID + " INTEGER, " +
				                     	KEY_DATE + " TEXT " +
//				                        KEY_TIME + " TEXT, " +
//				                        KEY_DURATION + " TEXT " +
//				                        KEY_TYPE + " TEXT" +
				                     " )";
		db.execSQL(createSessionsTable);	
		Log.e("Visus", "Session table created");	
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
		
		
		Log.e("Visus SessionHandler", "UserId " + String.valueOf(session.getUserId()) );
		Log.e("Visus SessionHandler", "Date " + session.getDate() );
		
		sessionValues.put(KEY_USER_ID, session.getUserId());
		sessionValues.put(KEY_DATE, session.getDate());
		
//		sessionValues.put(KEY_DATE, session.getDate());
//		sessionValues.put(KEY_TIME, session.getTime());
//		sessionValues.put(KEY_DURATION, session.getDuration());
//		sessionValues.put(KEY_TYPE, session.getType());
		
		Long result = db.insert(TABLE_SESSIONS, null, sessionValues);
		
		if(result == -1) {
			Log.e("Visus", String.valueOf(result) + " | Failed to write to db");
		}
		else {
			Log.e("Visus", "Written to db");
		}
		
		db.close();
	}
	
	public Overview getOverview(int userId) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Overview overview = new Overview();
		
		String columns [] = { KEY_ID, 
				              KEY_USER_ID, 
				              KEY_DATE,
//							  KEY_TIME,
//				              KEY_DURATION, 
//				              KEY_TYPE 
				            };
		
		// TODO
		// KEY_USER_ID + ", " + 
		final String qryOverview = "SELECT " + KEY_DURATION + ", " + KEY_TYPE +
		                           " FROM " + TABLE_SESSIONS +
		                           " WHERE " + KEY_USER_ID + " = ?";
		
		Cursor cursor = db.query(TABLE_SESSIONS,
				                 columns,
				                 columns[1] + "=" + String.valueOf(userId),
				                 null, null, null, null, null);
		
//		if(!cursor.moveToFirst()) {
//			Log.e("Visus", "query returned null");
//		}
//		else
//			Log.e("Visus", "query returned not null");
		

		int noSessions = 0;
		int noHours = 0;
		Stack<String> activities = new Stack<String>();
				
//		Log.e("Visus", String.valueOf(cursor.getColumnCount() ));
		
		// get no of session hours
//		while(cursor.moveToNext()) {
//			// count no. of session results
//			noSessions++;
//			
//			noHours += Integer.parseInt(cursor.getString(7)); // TODO get KEY_DURATION
//			
//			// get activity categories
////			if(!activities.contains(cursor.getString(8)) )
////				activities.add(cursor.getString(8));
//		}
		
//		cursor.close();
		
		// assign overview
		overview.setHours(20);
		overview.setNoOfSessions(30);
//		overview.setActivitiesNos(activities.size());
		
		Log.e("Visus", String.valueOf(overview.getHours()) );
		Log.e("Visus", String.valueOf(overview.getSessionNos() ));
//		Log.e("Visus", String.valueOf(overview.getActivitiesNos() ));
		
		return overview;
	}
	
}
