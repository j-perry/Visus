package com.visus.database;

import java.util.*;

import com.visus.entities.User;
import com.visus.entities.sessions.*;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.Log;

public class SessionHandler implements IDatabaseTable {
		
	private DatabaseHandler dbHandler;
	private UserHandler dbUser;
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
	 * 	Adds a new session to the database
	 * 	Method writes the following data:
	 *  	o user ID
	 * 		o dayNo
	 *  	o day-of-the-week
	 *  	o month
	 *  	o year
	 *  	o hour
	 *  	o minutes
	 *  	o timezone (AM/PM)
	 *  	o duration minutes
	 *  	o duration seconds
	 *  	o type (e.g., Email)
	 *  
	 * @param session parses a session object derived from generated data from the NewSession class
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
	
	/**
	 * Returns an overview of the user's activity since inception
	 * @param userId
	 * @return
	 */
	public Session getOverview(int userId) {
		Session session = new Session();
		Cursor cursor = null;
		Stack<String> activities = new Stack<String>();
		int noSessions = 0;
		int noActivities = 0;
		int noHoursTotal = 0;
		int noMins = 0;
		int noSecs = 0;
				
		// hours query
		String qryNoHours = "SELECT * " +
		                    "FROM " + DatabaseHandler.SESSIONS_TABLE + " " +
		                    "WHERE " + DatabaseHandler.KEY_USER_ID + " = " + userId;
		
		// sessions query
		String qryNoSessions = "SELECT " + DatabaseHandler.KEY_USER_ID + " " +
		                       "FROM " + DatabaseHandler.SESSIONS_TABLE + " " +
		                       "WHERE " + DatabaseHandler.KEY_USER_ID + " = " + userId;
		
		// activities query
		String qryNoActivities = "SELECT " + DatabaseHandler.KEY_TYPE + " " + 
								 "FROM " + DatabaseHandler.SESSIONS_TABLE + " " +
								 "WHERE " + DatabaseHandler.KEY_USER_ID + " = " + userId;
						
		cursor = db.rawQuery(qryNoHours, null);
				
		// validation
		if(cursor == null)
			Log.e("Visus", "SQL Query Failed");
		else
			Log.e("Visus", "SQL Query Successful");
		
		
		int minutesIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_MINS);
		
		// get no. of hours
		while(cursor.moveToNext()) {
			if(noMins > 60) {
				noMins = noMins - 60;
				noHoursTotal++;
			}
			else {
				noMins += cursor.getInt(minutesIndex);
			}
		}
		
		if(noHoursTotal > 1) {
			session.setOverviewHours(noHoursTotal);
		}
		else {
			session.setOverviewHours(noMins);
		}
		
		
		// get no. of sessions
		cursor = db.rawQuery(qryNoSessions, null);
		noSessions = cursor.getCount();
		
				
		// get no. of activities
		cursor = db.rawQuery(qryNoActivities, null);
		int typeIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TYPE);
		
		while(cursor.moveToNext()) {
			if(!activities.contains(cursor.getString(typeIndex) )) 
				activities.add(cursor.getString(typeIndex) );			
		}
		
		db.close();
		cursor.close();
		
		// return no. of activity categories
		noActivities = activities.size();
		
		
		// assign overview
//		TODO	session.setOverviewHours(noHoursTotal);
		session.setOverviewNoSessions(noSessions);
		session.setOverviewNoActivities(noActivities);
		
		return session;
	}
	
}
