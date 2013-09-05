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
	 * @param userId
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
	 * @param userId
	 * @return
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
	
	public ArrayList<Session> getLatestSessions(int userId) throws SQLiteException {
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
		
		ArrayList<Session> latestSessions = new ArrayList<Session>();
		
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
		    dateIndex,
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
		dateIndex			 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DATE);
		timeHourIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_HOUR);
		timeMinutesIndex 	 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIME_MINS);
		timezoneIndex 		 = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TIMEZONE);
		durationMinutesIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_MINS);
		durationSecondsIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_SECS);
		typeIndex            = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TYPE);
		
		int noItems = 0;
		
		while(cursor.moveToNext()) {
			if(noItems != 5) {
				session = new Session();
		
				session.setDayNo(cursor.getInt(dayNoIndex));
				session.setDay(cursor.getString(dayIndex));
				session.setMonth(cursor.getString(monthIndex));
				session.setYear(cursor.getInt(yearIndex));
				session.setDate(cursor.getString(dateIndex));
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
							
				latestSessions.add(session);
				
				noItems++;
			}
			else {
				break;
			}
		}
		
		cursor.close();
		db.close();
		
		return latestSessions;
	}
	
	/**
	 * Returns any sessions made today
	 * @param userId the current user id
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
		
		cursor.close();
		db.close();		
		
		return sessionsToday;
	}
	
	/**
	 * Returns any sessions made this week
	 * @param userId
	 * @return
	 * @throws SQLiteException
	 * @throws ParseException 
	 */
	public ArrayList<Session> getSessionsThisWeek(int userId, Week beginning, Week end) throws SQLiteException {		
		ArrayList<Session> sessionsThisWeek = new ArrayList<Session>();
		Week wkBeginning = beginning;
		Week wkEnd = end;
		
				
		// week beginning (date)
		int dayBeginning = wkBeginning.getDayNo();
		int tmp_monthBeginning = wkBeginning.getMonth();
		int yearBeginning = wkBeginning.getYear();
				
		// week end (date)... not to be confused with weekend!
		int dayEnd = wkEnd.getDayNo();
		int tmp_monthEnd = wkEnd.getMonth();
		int yearEnd = wkEnd.getYear();
		
		String dBeginning = null;
		String mBeginning = null;
		String dEnd = null;
		String mEnd = null;
		
		String strBeginning = null;
		String strEnd = null;
		
		String qryThisWeek = null;
		
		
		/**
		 * Beginning
		 */
		
		// day beginning
		if(dayBeginning < 10) {
			dBeginning = "0" + String.valueOf(dayBeginning);
		}
		else {
			dBeginning = String.valueOf(dayBeginning);
		}
		
		// month beginning
		if(tmp_monthBeginning < 10) {
			mBeginning = "0" + String.valueOf(tmp_monthBeginning);
		}
		else {
			mBeginning = String.valueOf(tmp_monthBeginning);
		}
		
		
		/**
		 * End
		 */
		
		// day end
		if(dayEnd < 10) {
			dEnd = "0" + String.valueOf(dayEnd);
		}
		else {
			dEnd = String.valueOf(dayEnd);
		}
		
		// month end
		if(tmp_monthEnd < 10) {
			mEnd = "0" + String.valueOf(tmp_monthEnd);
		}
		else {
			mEnd = String.valueOf(tmp_monthEnd);
		}
		
		strBeginning = String.valueOf(yearBeginning) + "-" + mBeginning + "-" + dBeginning;
		strEnd = String.valueOf(yearBeginning) + "-" + mEnd + "-" + dEnd;
		
		qryThisWeek = "SELECT * " +
					  "FROM " + DatabaseHandler.SESSIONS_TABLE + QRY_SPACING +
					  "WHERE " + DatabaseHandler.KEY_USER_ID + " = " + userId + QRY_SPACING +
					  "AND "
					      	   + DatabaseHandler.KEY_DATE + QRY_SPACING +
					  "BETWEEN date('" + strBeginning + "') " +
					 "AND date('" + strEnd + "')";
			
		Log.e("Visus", "---------------");
		Log.e("Visus", "qryThisWeek: ");
		Log.e("Visus", qryThisWeek);
		Log.e("Visus", "---------------");
		
		
		Log.e("Visus", "getSessionsThisWeek() " + yearBeginning + "-" + tmp_monthBeginning + "-" + dayBeginning);
		Log.e("Visus", "getSessionsThisWeek() " + yearEnd + "-" + tmp_monthEnd + "-" + dayEnd);
			 
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
		
		cursor.close();
		db.close();
		
		return sessionsThisWeek;
	}
	
	/**
	 * Returns any sessions made this month
	 * @param userId
	 * @return
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
		
		cursor.close();
		db.close();
		
		return sessionsThisMonth;
	}
	
	/**
	 * Returns any sessions made this year
	 * @param userId
	 * @return
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
		
		cursor.close();
		db.close();
		
		return sessionsThisYear;	
	}
}