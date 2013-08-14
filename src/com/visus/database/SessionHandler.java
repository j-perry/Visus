package com.visus.database;

import java.util.*;

import com.visus.entities.sessions.*;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.Log;

public class SessionHandler implements IDatabaseTable {
		
	private DatabaseHandler dbHandler;
	private SQLiteDatabase db;
	private Long result;
		
	public SessionHandler(Context context) {
		dbHandler = new DatabaseHandler(context);
	}
	
	@Override
	public void open() throws SQLiteException {
		db = dbHandler.getWritableDatabase(); // reads and writes
	}
	
	@Override
	public void close() throws SQLiteException {
		dbHandler.close();
	}
		
	/**
	 * 	Session methods
	 */
	public void add(Session session) {
		ContentValues sessionValues = new ContentValues();
				
		// user id
		sessionValues.put(DatabaseHandler.KEY_USER_ID, session.getUserId());
		
		// date
		sessionValues.put(DatabaseHandler.KEY_DAY_NO, session.getDayNo());
		sessionValues.put(DatabaseHandler.KEY_DAY, session.getDay());
		sessionValues.put(DatabaseHandler.KEY_MONTH, session.getMonth());
		sessionValues.put(DatabaseHandler.KEY_YEAR, session.getYear());
		
		// time
		sessionValues.put(DatabaseHandler.KEY_TIME_HOUR, session.getTimeHour());
		sessionValues.put(DatabaseHandler.KEY_TIME_MINS, session.getTimeMinutes());
		sessionValues.put(DatabaseHandler.KEY_TIMEZONE, session.getDayPeriod());
		
		// duration
		sessionValues.put(DatabaseHandler.KEY_DURATION_MINS, session.getDurationMinutes());
		sessionValues.put(DatabaseHandler.KEY_DURATION_SECS, session.getDurationSeconds());
		
		// type
		sessionValues.put(DatabaseHandler.KEY_TYPE, session.getType());
		
		try {
			result = db.insert(DatabaseHandler.SESSIONS_TABLE, null, sessionValues);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "Error writing to database Sessions", e);
		}
		finally {			
			// -1 denotes, unsuccessful. 0 and higher denotes no. of written items
			if(result == -1) {
				Log.e("Visus", "----------------");
				Log.e("Visus", String.valueOf(result) + " | Failed to write to db");
				Log.e("Visus", "----------------");
			}
			else {
				Log.e("Visus", "----------------");
				Log.e("Visus", "ID: " + String.valueOf(result) + " | Written to db");
				Log.e("Visus", "Written: \n"
			  					             + "o User ID: " + session.getUserId() + "\n" 
							                 + "o Session date (day no): " + session.getDayNo() + "\n"
							                 + "o Session date (day): " + session.getDay() + "\n"
							                 + "o Session date (month): "+ session.getMonth() + "\n"
							                 + "o Session date (year): " + session.getYear() + "\n"
							                 + "o Session time (hour): " + session.getTimeHour() + "\n"
							                 + "o Session time (minutes): " + session.getTimeMinutes() + "\n"
							                 + "o Session timezone: " + session.getDayPeriod() + "\n"
							                 + "o Session duration (mins): " + session.getDurationMinutes() + "\n"
							                 + "o Session duration (secs): " + session.getDurationSeconds() + "\n"
							                 + "o Session type: " + session.getType());
							
				Log.e("Visus", "----------------");
			}
		}
		
	}
	
//	public Overview getOverview(int userId) {
//		SQLiteDatabase db = this.getReadableDatabase();
//		
//		Overview overview = new Overview();
//		
//		String columns [] = { KEY_ID, 
//				              KEY_USER_ID, 
//				              KEY_DATE,
////							  KEY_TIME,
////				              KEY_DURATION, 
////				              KEY_TYPE 
//				            };
//		
//		// TODO
//		// KEY_USER_ID + ", " + 
//		final String qryOverview = "SELECT " + KEY_DURATION + ", " + KEY_TYPE +
//		                           " FROM " + SESSIONS_TABLE +
//		                           " WHERE " + KEY_USER_ID + " = ?";
//		
//		Cursor cursor = db.query(SESSIONS_TABLE,
//				                 columns,
//				                 columns[1] + "=" + String.valueOf(userId),
//				                 null, null, null, null, null);
//		
////		if(!cursor.moveToFirst()) {
////			Log.e("Visus", "query returned null");
////		}
////		else
////			Log.e("Visus", "query returned not null");
//		
//
//		int noSessions = 0;
//		int noHours = 0;
//		Stack<String> activities = new Stack<String>();
//				
////		Log.e("Visus", String.valueOf(cursor.getColumnCount() ));
//		
//		// get no of session hours
////		while(cursor.moveToNext()) {
////			// count no. of session results
////			noSessions++;
////			
////			noHours += Integer.parseInt(cursor.getString(7)); // TODO get KEY_DURATION
////			
////			// get activity categories
//////			if(!activities.contains(cursor.getString(8)) )
//////				activities.add(cursor.getString(8));
////		}
//		
////		cursor.close();
//		
//		// assign overview
//		overview.setHours(20);
//		overview.setNoOfSessions(30);
////		overview.setActivitiesNos(activities.size());
//		
//		Log.e("Visus", String.valueOf(overview.getHours()) );
//		Log.e("Visus", String.valueOf(overview.getSessionNos() ));
////		Log.e("Visus", String.valueOf(overview.getActivitiesNos() ));
//		
//		return overview;
//	}
	
}
