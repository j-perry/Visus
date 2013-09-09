package com.visus.database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.visus.entities.User;
import com.visus.entities.Week;
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
	private Session session;
	
	private static final String QRY_SPACING = " ";
		
	public SessionHandler(Context context) {
		dbHandler = new DatabaseHandler(context);
	}
	
	/**
	 * Opens a new database connection
	 */
	@Override
	public void open() throws SQLiteException {
		db = dbHandler.getWritableDatabase(); // reads and writes
	}
	
	/**
	 * Closes the existing database connection
	 */
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
	 *  	o date (yyyy-mm-dd)
	 *  	o hour
	 *  	o minutes
	 *  	o timezone (AM/PM)
	 *  	o duration minutes
	 *  	o duration seconds
	 *  	o type (e.g., Email)
	 *  
	 * @param session parses a session object derived from generated data from the NewSession class
	 */
	public void add(Session session) throws SQLiteException {
		ContentValues sessionValues = new ContentValues();
				
		// user id
		sessionValues.put(DatabaseHandler.KEY_USER_ID, session.getUserId());
		
		// date
		sessionValues.put(DatabaseHandler.KEY_DAY_NO, session.getDayNo());
		sessionValues.put(DatabaseHandler.KEY_DAY, session.getDay());
		sessionValues.put(DatabaseHandler.KEY_MONTH, session.getMonth());
		sessionValues.put(DatabaseHandler.KEY_YEAR, session.getYear());
		
		sessionValues.put(DatabaseHandler.KEY_DATE, session.getDate());			// NEW!
		
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
							                 + "o Session date: " + session.getDate() + "\n"
							                 
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
	 * Returns all session types (categories)
	 * @param userId The present user's ID
	 * @return
	 */
	public ArrayList<String> getSessionTypes(int userId) throws SQLiteException {
		ArrayList<String> sessionTypes = new ArrayList<String>();
		Cursor cursor = null;
		String qrySessions = "SELECT" + QRY_SPACING + DatabaseHandler.KEY_TYPE + QRY_SPACING + 
				 			 "FROM"   + QRY_SPACING + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
				 			 "WHERE"  + QRY_SPACING + DatabaseHandler.KEY_USER_ID + " = " + userId;
		
		cursor = db.rawQuery(qrySessions, null);
		
		int typeIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TYPE);
		
		Log.e("Visus", "-------------------");
		
		while(cursor.moveToNext()) {
			if(!sessionTypes.contains(cursor.getString(typeIndex) )) { 
				sessionTypes.add(cursor.getString(typeIndex) );
				Log.e("Visus", "Type: " + cursor.getString(typeIndex));
			}
		}
		
		cursor.close();
		db.close();		
		
		return sessionTypes;
	}
	
	/**
	 * Returns an overview of the user's activity since inception
	 * @param userId The present user's ID
	 * @return Overview of user's sessions made so far
	 */
	public Session getOverview(int userId) throws SQLiteException {
		Session session = new Session();
		Cursor cursor = null;
		Stack<String> activities = new Stack<String>();
		final int MINS_PER_HOUR = 60;
		int noSessions = 0;
		int noActivities = 0;
		int noHoursTotal = 0;
		int noMins = 0;
		int noSecs = 0;
		
				
		// hours query
		String qryNoHours = "SELECT *" + QRY_SPACING +
		                    "FROM"     + QRY_SPACING + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
		                    "WHERE"    + QRY_SPACING + DatabaseHandler.KEY_USER_ID + " = " + userId;
		
		// sessions query
		String qryNoSessions = "SELECT" + QRY_SPACING + DatabaseHandler.KEY_USER_ID + QRY_SPACING +
		                       "FROM"   + QRY_SPACING + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
		                       "WHERE " + QRY_SPACING + DatabaseHandler.KEY_USER_ID + " = " + userId;
		
		// activities query
		String qryNoActivities = "SELECT" + QRY_SPACING + DatabaseHandler.KEY_TYPE + QRY_SPACING + 
								 "FROM"   + QRY_SPACING + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
								 "WHERE"  + QRY_SPACING + DatabaseHandler.KEY_USER_ID + " = " + userId;
						
		cursor = db.rawQuery(qryNoHours, null);
				
		// validation
		if(cursor == null)
			Log.e("Visus", "SQL Query Failed");
		else
			Log.e("Visus", "SQL Query Successful");
		
		
		int minutesIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_MINS);
		
		// get no. of hours
		while(cursor.moveToNext()) {
			noMins += cursor.getInt(minutesIndex);
		}

		// convert minutes to no. of hours
		if(noMins > MINS_PER_HOUR )
			noHoursTotal = (int) noMins / MINS_PER_HOUR ; // i.e., 156mins / 60 mins in an hour = 2.6 hours ~ 2 hours
		else
			noMins += cursor.getInt(minutesIndex);
		
		if(noHoursTotal >= 1) {
			// display no. of hours
			session.setOverviewHours(noHoursTotal);
		}
		else {
			// display no. of minutes
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
				
		cursor.close();
		db.close();
		
		// return no. of activity categories
		noActivities = activities.size();
		
		
		// assign overview
//		TODO	session.setOverviewHours(noHoursTotal);
		session.setOverviewNoSessions(noSessions);
		session.setOverviewNoActivities(noActivities);
		
		return session;
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public ArrayList<Session> getSessions(int userId) throws SQLiteException {
		String qrySessions = "SELECT *" + QRY_SPACING + 
                             "FROM"     + QRY_SPACING + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
                             "WHERE"    + QRY_SPACING + DatabaseHandler.KEY_USER_ID + " = " + userId;
		
		ArrayList<Session> sessionsAll = new ArrayList<Session>();
		Cursor cursor = null;
		
		int dayNoIndex,
		    dayIndex,
		    monthIndex,
		    yearIndex,
		    timeHourIndex,
		    timeMinutesIndex,
		    timezoneIndex,
		    durationMinutesIndex,
		    durationSecondsIndex,
		    typeIndex = 0;
		
		
		cursor = db.rawQuery(qrySessions, null);
		
		// find each columns respective index
		dayNoIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY_NO);
		dayIndex   			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY);
		monthIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_MONTH);
		yearIndex  			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_YEAR);
		timeHourIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_HOUR);
		timeMinutesIndex 	 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_MINS);
		timezoneIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIMEZONE);
		durationMinutesIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_MINS);
		durationSecondsIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_SECS);
		typeIndex            = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TYPE);
		
		
		// retrieve sessions
		while(cursor.moveToNext()) {
			session = new Session();
			
			// date
			session.setDayNo(cursor.getInt(dayNoIndex));
			session.setDay(cursor.getString(dayIndex));
			session.setMonth(cursor.getString(monthIndex));
			session.setYear(cursor.getInt(yearIndex));
			
			// time
			session.setTimeHour(cursor.getInt(timeHourIndex));
			session.setTimeMinutes(cursor.getInt(timeMinutesIndex));					
			session.setDayPeriod(cursor.getString(timezoneIndex));
			
			
			/********************
			 * 
			 * 		duration	
			 * 
			 ********************/
			
			// if no. of minutes is a minute or longer
			if(cursor.getInt(durationMinutesIndex) >= 1)
				// assign no. of minutes
				session.setDurationMinutes(cursor.getInt(durationMinutesIndex));
			else
				// else, assign zero to indicate this
				session.setDurationMinutes(0);
			
			session.setDurationSeconds(cursor.getInt(durationSecondsIndex));
							
			// TODO
			session.setType(cursor.getString(typeIndex));
			
			
			// add the session
			sessionsAll.add(session);
		}
		
		cursor.close();
		db.close();
		
		return sessionsAll;
	}
	
	/**
	 * Return's the first five sessions recently conducted
	 * @param userId The present user's ID
	 * @return Latest sessions made (top 5)
	 * @throws SQLiteException
	 */
	public ArrayList<Session> getLatestSessions(int userId) throws SQLiteException {
		// get the current year
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
				
		String dateBeginning = String.valueOf(year) + "-01-01";
		String dateEnding = String.valueOf(year) + "-12-31";
				
		Log.e("Visus", "Year beginning: " + dateBeginning);
		Log.e("Visus", "Year ending: " + dateEnding);
		
		ArrayList<Session> sessionsThisYear = new ArrayList<Session>();
				
		String qryThisYear = "SELECT *" + QRY_SPACING +
						     "FROM" + QRY_SPACING + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
						     "WHERE" + QRY_SPACING + 
		                     	DatabaseHandler.KEY_USER_ID + " = '" + userId + "'" + QRY_SPACING + 
		                     "AND" + QRY_SPACING +
		                        DatabaseHandler.KEY_DATE + QRY_SPACING +       
							 "BETWEEN" + QRY_SPACING +                       	
								"date('" + dateBeginning + "')" + QRY_SPACING +
							 "AND" + QRY_SPACING +
							 	"date('" + dateEnding + "')";
				
		Log.e("Visus", "---------------");
		Log.e("Visus", "qryThisYear: ");
		Log.e("Visus", qryThisYear);
		Log.e("Visus", "---------------");
				
		Cursor cursor = db.rawQuery(qryThisYear, null);
				
		int dayNoIndex,
			dayIndex,
			monthIndex,
			yearIndex,
			timeHourIndex,
			timeMinutesIndex,
			timezoneIndex,
			durationMinutesIndex,
			durationSecondsIndex,
			typeIndex = 0;

		dayNoIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY_NO);
		dayIndex   			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY);
		monthIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_MONTH);
		yearIndex  			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_YEAR);
		timeHourIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_HOUR);
		timeMinutesIndex 	 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_MINS);
		timezoneIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIMEZONE);
		durationMinutesIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_MINS);
		durationSecondsIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_SECS);
		typeIndex            = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TYPE);
				
		while(cursor.moveToNext()) {
					
			session = new Session();
				
			session.setDayNo(cursor.getInt(dayNoIndex));
			session.setDay(cursor.getString(dayIndex));
			session.setMonth(cursor.getString(monthIndex));
			session.setYear(cursor.getInt(yearIndex));
			session.setTimeHour(cursor.getInt(timeHourIndex));
			session.setTimeMinutes(cursor.getInt(timeMinutesIndex));
			session.setDayPeriod(cursor.getString(timezoneIndex));
			session.setDurationMinutes(cursor.getInt(durationMinutesIndex));
			session.setDurationSeconds(cursor.getInt(durationSecondsIndex));
						
			if(!cursor.getString(typeIndex).isEmpty()) {
				session.setType(cursor.getString(typeIndex));
			}
			else {
				session.setType("Undefined");
			}
									
			sessionsThisYear.add(session);
					
		}
				
		// reverse the results (this is a substitute solution to the ORDER BY clause)
		Collections.reverse(sessionsThisYear);
				
		cursor.close();
		db.close();
				
		return sessionsThisYear;		
	}
	
	/**
	 * Returns any sessions made today
	 * @param userId The present user's ID
	 * @return sessions made today
	 * @throws SQLiteException if the database cursor does not return any results or fails executing the query
	 */
	public ArrayList<Session> getSessionsToday(int userId) throws SQLiteException {
		ArrayList<Session> sessionsToday = new ArrayList<Session>();
				
		String qrySessionsToday = "SELECT *" + QRY_SPACING +
                				  "FROM" + QRY_SPACING + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
                				  "WHERE" + QRY_SPACING + " Date = date('now')";
		
		Cursor cursor = db.rawQuery(qrySessionsToday, null);
		
		int dayNoIndex,
	    	dayIndex,
		    monthIndex,
		    yearIndex,
		    timeHourIndex,
		    timeMinutesIndex,
		    timezoneIndex,
		    durationMinutesIndex,
		    durationSecondsIndex,
		    typeIndex = 0;
	
		dayNoIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY_NO);
		dayIndex   			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY);
		monthIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_MONTH);
		yearIndex  			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_YEAR);
		timeHourIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_HOUR);
		timeMinutesIndex 	 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_MINS);
		timezoneIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIMEZONE);
		durationMinutesIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_MINS);
		durationSecondsIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_SECS);
		typeIndex            = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TYPE);
		
		while(cursor.moveToNext()) {
			session = new Session();
	
			session.setDayNo(cursor.getInt(dayNoIndex));
			session.setDay(cursor.getString(dayIndex));
			session.setMonth(cursor.getString(monthIndex));
			session.setYear(cursor.getInt(yearIndex));
			session.setTimeHour(cursor.getInt(timeHourIndex));
			session.setTimeMinutes(cursor.getInt(timeMinutesIndex));
			session.setDayPeriod(cursor.getString(timezoneIndex));
			session.setDurationMinutes(cursor.getInt(durationMinutesIndex));
			session.setDurationSeconds(cursor.getInt(durationSecondsIndex));
			
			if(!cursor.getString(typeIndex).isEmpty()) {
				session.setType(cursor.getString(typeIndex));
			}
			else {
				session.setType("Undefined");
			}
						
			sessionsToday.add(session);
		}
		
		// reverse the results (this is a substitute solution to the ORDER BY clause)
		Collections.reverse(sessionsToday);
		
		cursor.close();
		db.close();		
		
		return sessionsToday;
	}
	
	/**
	 * Returns any sessions made this week
	 * @param userId The present user's ID
	 * @return Sessions made this week
	 * @throws SQLiteException
	 * @throws ParseException 
	 */	
	public ArrayList<Session> getSessionsThisWeek(int userId) throws SQLiteException {		
		ArrayList<Session> sessionsThisWeek = new ArrayList<Session>();		
		Week thisWeek = new Week();
				
		String dBeginning = null;
		String mBeginning = null;
		String dEnd = null;
		String mEnd = null;
		
		String strBeginning = null;
		String strEnd = null;
		
		// Log the beginning and end of this week (dates)
		Log.e("Visus", "getSessionsThisWeek() - Beginning: " + thisWeek.beginning() );
		Log.e("Visus", "getSessionsThisWeek() - Ending: " + thisWeek.ending() );
		
		String qryThisWeek = null;
		
		qryThisWeek = "SELECT * " +
					  "FROM " + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
					  "WHERE " + DatabaseHandler.KEY_USER_ID + " = " + userId + QRY_SPACING +
					  "AND "
					      	   + DatabaseHandler.KEY_DATE + QRY_SPACING +
					  "BETWEEN date('" + thisWeek.beginning() + "') " +
					 "AND date('" + thisWeek.ending() + "')";
			
		Log.e("Visus", "---------------");
		Log.e("Visus", "qryThisWeek: ");
		Log.e("Visus", qryThisWeek);
		Log.e("Visus", "---------------");
			 
		Cursor cursor = db.rawQuery(qryThisWeek, null);
		
		Log.e("Visus", "YES!");
		
		int dayNoIndex,
	    	dayIndex,
		    monthIndex,
		    yearIndex,
		    timeHourIndex,
		    timeMinutesIndex,
		    timezoneIndex,
		    durationMinutesIndex,
		    durationSecondsIndex,
		    typeIndex = 0;

		dayNoIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY_NO);
		dayIndex   			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY);
		monthIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_MONTH);
		yearIndex  			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_YEAR);
		timeHourIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_HOUR);
		timeMinutesIndex 	 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_MINS);
		timezoneIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIMEZONE);
		durationMinutesIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_MINS);
		durationSecondsIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_SECS);
		typeIndex            = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TYPE);
		
		if(cursor.getCount() == 0) {
			Log.e("Visus", "No results :'(");
		}
		else {
			Log.e("Visus", "There are results...");
		}
		
		while(cursor.moveToNext()) {
			session = new Session();
	
			session.setDayNo(cursor.getInt(dayNoIndex));
			session.setDay(cursor.getString(dayIndex));
			session.setMonth(cursor.getString(monthIndex));
			session.setYear(cursor.getInt(yearIndex));
			session.setTimeHour(cursor.getInt(timeHourIndex));
			session.setTimeMinutes(cursor.getInt(timeMinutesIndex));
			session.setDayPeriod(cursor.getString(timezoneIndex));
			session.setDurationMinutes(cursor.getInt(durationMinutesIndex));
			session.setDurationSeconds(cursor.getInt(durationSecondsIndex));
			
			if(!cursor.getString(typeIndex).isEmpty()) {
				session.setType(cursor.getString(typeIndex));
			}
			else {
				session.setType("Undefined");
			}
						
			sessionsThisWeek.add(session);
		}
		
		// reverse the results (this is a substitute solution to the ORDER BY clause)
		Collections.reverse(sessionsThisWeek);
		
		cursor.close();
		db.close();
		
		return sessionsThisWeek;
	}
	
	/**
	 * Returns any sessions made this month
	 * @param userId The present user's ID
	 * @return Sessions made this month
	 * @throws SQLiteException
	 */
	public ArrayList<Session> getSessionsThisMonth(int userId) throws SQLiteException {
		int maxDays = 0;
		int month = 0;
		int year = 0;
		
		String strMonth = null;
		
		Calendar cal = Calendar.getInstance();
		month = cal.get(Calendar.MONTH);
		month++;
		year = cal.get(Calendar.YEAR);
		
		cal = new GregorianCalendar(year, month, 1);
		maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		if(month < 10) {
			strMonth = "0" + String.valueOf(month);
		}
		else {
			strMonth = String.valueOf(month);
		}
		
		
		Log.e("Visus", "Month " + strMonth + ", " + "Year " + year);
		Log.e("Visus", "Max no. of days: " + maxDays);
		
		ArrayList<Session> sessionsThisMonth = new ArrayList<Session>();
		
		String qryThisMonth = "SELECT *" + QRY_SPACING +
                			  "FROM" + QRY_SPACING + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
                              "WHERE" + QRY_SPACING +
                              	DatabaseHandler.KEY_USER_ID + " = '" + userId + "'" + QRY_SPACING + 
                              "AND" + QRY_SPACING + 
                              	DatabaseHandler.KEY_DATE + QRY_SPACING +
                              "BETWEEN" + QRY_SPACING +                              	
                              	"date('" + year + "-" + strMonth + "-" + "01')" + QRY_SPACING +
                              "AND" + QRY_SPACING +
                              		"date('" + year + "-" + strMonth + "-" + maxDays + "')";
	
		Log.e("Visus", "---------------");
		Log.e("Visus", "qryThisMonth: ");
		Log.e("Visus", qryThisMonth);
		Log.e("Visus", "---------------");
		
		Cursor cursor = db.rawQuery(qryThisMonth, null);
		
		int dayNoIndex,
	    	dayIndex,
		    monthIndex,
		    yearIndex,
		    timeHourIndex,
		    timeMinutesIndex,
		    timezoneIndex,
		    durationMinutesIndex,
		    durationSecondsIndex,
		    typeIndex = 0;

		dayNoIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY_NO);
		dayIndex   			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY);
		monthIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_MONTH);
		yearIndex  			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_YEAR);
		timeHourIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_HOUR);
		timeMinutesIndex 	 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_MINS);
		timezoneIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIMEZONE);
		durationMinutesIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_MINS);
		durationSecondsIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_SECS);
		typeIndex            = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TYPE);
		
		while(cursor.moveToNext()) {
			session = new Session();
	
			session.setDayNo(cursor.getInt(dayNoIndex));
			session.setDay(cursor.getString(dayIndex));
			session.setMonth(cursor.getString(monthIndex));
			session.setYear(cursor.getInt(yearIndex));
			session.setTimeHour(cursor.getInt(timeHourIndex));
			session.setTimeMinutes(cursor.getInt(timeMinutesIndex));
			session.setDayPeriod(cursor.getString(timezoneIndex));
			session.setDurationMinutes(cursor.getInt(durationMinutesIndex));
			session.setDurationSeconds(cursor.getInt(durationSecondsIndex));
			
			if(!cursor.getString(typeIndex).isEmpty()) {
				session.setType(cursor.getString(typeIndex));
			}
			else {
				session.setType("Undefined");
			}
						
			sessionsThisMonth.add(session);
		}
		
		// reverse the results (this is a substitute solution to the ORDER BY clause)
		Collections.reverse(sessionsThisMonth);
		
		cursor.close();
		db.close();
		
		return sessionsThisMonth;
	}
	
	/**
	 * Returns any sessions made this year
	 * @param userId The present user's ID
	 * @return Sessions made this year
	 * @throws SQLiteException
	 */
	public ArrayList<Session> getSessionsThisYear(int userId) throws SQLiteException {
		// get the current year
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		
		String dateBeginning = String.valueOf(year) + "-01-01";
		String dateEnding = String.valueOf(year) + "-12-31";
		
		Log.e("Visus", "Year beginning: " + dateBeginning);
		Log.e("Visus", "Year ending: " + dateEnding);
		
		ArrayList<Session> sessionsThisYear = new ArrayList<Session>();
		
		String qryThisYear = "SELECT *" + QRY_SPACING +
				             "FROM" + QRY_SPACING + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
				             "WHERE" + QRY_SPACING + 
                           		DatabaseHandler.KEY_USER_ID + " = '" + userId + "'" + QRY_SPACING + 
                           	 "AND" + QRY_SPACING +
                        		DatabaseHandler.KEY_DATE + QRY_SPACING +       
							 "BETWEEN" + QRY_SPACING +                       	
							 	"date('" + dateBeginning + "')" + QRY_SPACING +
							 "AND" + QRY_SPACING +
							 	"date('" + dateEnding + "')";
		
		Log.e("Visus", "---------------");
		Log.e("Visus", "qryThisYear: ");
		Log.e("Visus", qryThisYear);
		Log.e("Visus", "---------------");
		
		Cursor cursor = db.rawQuery(qryThisYear, null);
		
		int dayNoIndex,
	    	dayIndex,
		    monthIndex,
		    yearIndex,
		    timeHourIndex,
		    timeMinutesIndex,
		    timezoneIndex,
		    durationMinutesIndex,
		    durationSecondsIndex,
		    typeIndex = 0;

		dayNoIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY_NO);
		dayIndex   			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DAY);
		monthIndex 			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_MONTH);
		yearIndex  			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_YEAR);
		timeHourIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_HOUR);
		timeMinutesIndex 	 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_MINS);
		timezoneIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIMEZONE);
		durationMinutesIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_MINS);
		durationSecondsIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_SECS);
		typeIndex            = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TYPE);
		
		while(cursor.moveToNext()) {
			session = new Session();
	
			session.setDayNo(cursor.getInt(dayNoIndex));
			session.setDay(cursor.getString(dayIndex));
			session.setMonth(cursor.getString(monthIndex));
			session.setYear(cursor.getInt(yearIndex));
			session.setTimeHour(cursor.getInt(timeHourIndex));
			session.setTimeMinutes(cursor.getInt(timeMinutesIndex));
			session.setDayPeriod(cursor.getString(timezoneIndex));
			session.setDurationMinutes(cursor.getInt(durationMinutesIndex));
			session.setDurationSeconds(cursor.getInt(durationSecondsIndex));
			
			if(!cursor.getString(typeIndex).isEmpty()) {
				session.setType(cursor.getString(typeIndex));
			}
			else {
				session.setType("Undefined");
			}
						
			sessionsThisYear.add(session);
		}
		
		// reverse the results (this is a substitute solution to the ORDER BY clause)
		Collections.reverse(sessionsThisYear);
		
		cursor.close();
		db.close();
		
		return sessionsThisYear;	
	}
	
	/**
	 * Returns all session types. Formulates part of auto-suggest field in NewSession
	 * @param userId The present user's ID
	 * @return Session types
	 */
	public ArrayList<String> getAllSessionTypes(int userId) throws SQLiteException {
		ArrayList<String> types = new ArrayList<String>();
		String qryTypes = null;
		int typesIndex = 0;
		
		qryTypes = "SELECT" + QRY_SPACING + DatabaseHandler.KEY_TYPE + QRY_SPACING +
				   "FROM" + QRY_SPACING + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
				   "WHERE" + QRY_SPACING + DatabaseHandler.KEY_USER_ID + " = " + userId;
		
		Log.e("Visus", qryTypes);
		
		Cursor cursor = db.rawQuery(qryTypes, null);
		
		typesIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TYPE);
		
		while(cursor.moveToNext()) {
			String type = cursor.getString(typesIndex);
			
			if(!types.contains(type)) {
				types.add(type);				
			}
		}
		
		cursor.close();
		db.close();
		
		return types;
	}
	
	/**
	 * Delete's sessions made this month
	 * @param userId
	 * @return
	 * @throws SQLiteException
	 */
	public int deleteSessionsThisMonth(int userId) throws SQLiteException {
		int result = 0;
		int maxDays = 0;
		int month = 0;
		int year = 0;
		
		String strMonth = null;
		
		Calendar cal = Calendar.getInstance();
		month = cal.get(Calendar.MONTH);
		month++;
		year = cal.get(Calendar.YEAR);
		
		cal = new GregorianCalendar(year, month, 1);
		maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		if(month < 10) {
			strMonth = "0" + String.valueOf(month);
		}
		else {
			strMonth = String.valueOf(month);
		}
		
		String qryDeleteMonth = DatabaseHandler.KEY_USER_ID + " = " + userId + QRY_SPACING +
				                "AND" + QRY_SPACING +
				                DatabaseHandler.KEY_DATE + QRY_SPACING +
				                	"BETWEEN" + QRY_SPACING +
				                		"date('" + year + "-" + strMonth + "-01')" + QRY_SPACING + 
				                	"AND" + QRY_SPACING +
				                		"date('" + year + "-" + strMonth + "-" + maxDays + "')";
		
		Log.e("Visus", qryDeleteMonth);
		
		result = db.delete(DatabaseHandler.SESSIONS_TABLE, 
						   qryDeleteMonth,
				           null);
		
		Log.e("Visus", "deleteSessionsThisMonth() - Result: " + result);
		
		return result;
	}
	
	/**
	 * Delete's sessions made this year
	 * @param userId
	 * @return
	 * @throws SQLiteException
	 */
	public int deleteSessionsThisYear(int userId) throws SQLiteException {
		int year = 0;
		int result = 0;
		Calendar cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		
		String qryDeleteYear = DatabaseHandler.KEY_USER_ID + " = " + userId + QRY_SPACING +
				                "AND" + QRY_SPACING +
				               DatabaseHandler.KEY_DATE + QRY_SPACING +
				                	"BETWEEN" + QRY_SPACING +
				                		"date('" + year + "-01-01')" + QRY_SPACING + 
				                	"AND" + QRY_SPACING +
				                		"date('" + year + "-12-31')";
		
		Log.e("Visus", qryDeleteYear);
		
		result = db.delete(DatabaseHandler.SESSIONS_TABLE, 
						   "UserId = " + userId, 
				           null);
		
		Log.e("Visus", "deleteSessionsThisYear() - Result: " + result);
		
		return result;
	}
	
	/**
	 * Delete's all sessions made... ever!
	 * @param userId The present user's ID
	 * @return Return's whether the result was successful
	 */
	public int deleteAllSessions(int userId) throws SQLiteException {
		int result = 0;
		String qryDeleteSessions = null;
		
		qryDeleteSessions = DatabaseHandler.KEY_USER_ID + " = " + userId;
		
		result = db.delete(DatabaseHandler.SESSIONS_TABLE, 
				  		   qryDeleteSessions, 
				           null);
		
		return result;
	}
}