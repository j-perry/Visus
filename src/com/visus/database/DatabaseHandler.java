package com.visus.database;

import java.util.Stack;

import com.visus.entities.User;
import com.visus.entities.sessions.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	/* Generic properties for database */
	private final static String DATABASE_NAME = "Visus";
	private final static int    DATABASE_VERSION = 1;
	
	private final static String KEY_ID       = "Id"; // All DB tables will share this property!
	
	/**
	 * Users table
	 */
	private final static String USERS_TABLE = "Users";
	
	private final static String KEY_ACTIVE   = "Active";
	private final static String KEY_NAME     = "Name";
	private final static String KEY_AGE      = "Age";
	private final static String KEY_GENDER   = "Gender";
		
	private static final int ACTIVE_USER = 1;
	private static final int NON_ACTIVE_USER = 0;
	
	
	/**
	 * Sessions table
	 */
	private final static String SESSIONS_TABLE    = "Sessions";
	
	// columns
	private final static String KEY_USER_ID       = "UserId";
	private final static String KEY_DAY_NO        = "DayNo";
	private final static String KEY_DAY           = "Day";
	private final static String KEY_MONTH         = "Month";
	private final static String KEY_YEAR          = "Year";
	private final static String KEY_TIME_HOUR     = "Hour";
	private final static String KEY_TIME_MINS     = "Mins";
	private final static String KEY_TIMEZONE      = "TimeZone";
	private final static String KEY_DURATION_MINS = "DurationMinutes";
	private final static String KEY_DURATION_SECS = "DurationSeconds";	
	private final static String KEY_TYPE          = "Type";
	
	
	// old fields - not suitable types	
	private final static String KEY_DATE        = "Date";	
	private final static String KEY_TIME        = "Time";
	private final static String KEY_DURATION    = "Duration";
		
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.e("Visus", "User Handler Init");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// be very careful to include leave spaces between statements!!
		String createUsersTable = "CREATE TABLE " + USERS_TABLE +
				                  " ( " +
				                  	 KEY_ID + " INTEGER PRIMARY KEY, " +
				                  	 KEY_ACTIVE + " INTEGER, " +
				                  	 KEY_NAME + " TEXT, " +
				                  	 KEY_AGE + " INTEGER, " +
				                  	 KEY_GENDER + " TEXT " +
				                  ");";
		
		String createSessionsTable = "CREATE TABLE " + SESSIONS_TABLE + 
                                     " ( " +
                	                 	KEY_ID + " INTEGER PRIMARY KEY, " + 
                	                 	KEY_USER_ID + " INTEGER, " +
                	                 	KEY_DAY_NO + " INTEGER, " +
                	                 	KEY_DAY + " TEXT, " +
                	                 	KEY_MONTH + " TEXT, " +
                	                 	KEY_YEAR + " INTEGER, " +
                	                 	KEY_TIME_HOUR + " TEXT, " +
                	                 	KEY_TIME_MINS + " TEXT, " +
                	                 	KEY_TIMEZONE + " TEXT, " +
                	                 	KEY_DURATION_MINS + " INTEGER, " +
                	                 	KEY_DURATION_SECS + " INTEGER, " +
                	                 	KEY_TYPE + " TEXT " +
                	                 ");";
		
		db.execSQL(createUsersTable);
		Log.e("Visus", "Users table created");
		
		db.execSQL(createSessionsTable);
		Log.e("Visus", "Sessions table created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SESSIONS_TABLE);
 
        // Create tables again
        onCreate(db);		
	}
	
	/**
	 * 	User methods
	 */
	public void addUser(User user) throws SQLiteException {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues userValues = new ContentValues();
		
		userValues.put(KEY_ACTIVE, ACTIVE_USER );
		userValues.put(KEY_NAME, user.getFirstname() );
		userValues.put(KEY_GENDER, user.getGender() );
		userValues.put(KEY_AGE, user.getAge() );
		
		db.insert(USERS_TABLE, null, userValues);
		Log.e("Visus", "New user added");
		db.close();
	}
	
	public void deleteUser() {
		SQLiteDatabase db = this.getWritableDatabase();
		User user = null;
		user = getActiveUser();
		
		Log.e("Visus", "User ID = " + String.valueOf(user.getFirstname() ) );
		
		int id = db.delete(USERS_TABLE, 					
				           KEY_ID + " = ?", 			
				           new String[] { String.valueOf(user.getUserId()) } );	
		
		if(id == 1) {
			Log.e("Visus", "Success");
		}
		else
			Log.e("Visus", "Unsuccessful");
		
		db.close();
		Log.e("Visus", "DB closed");
	}
	
	public void updateUser(User user) throws SQLiteException {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues userValues = new ContentValues();

		userValues.put(KEY_ID, user.getUserId() );
		userValues.put(KEY_ACTIVE, user.getActive() );
		userValues.put(KEY_NAME, user.getFirstname() );
		userValues.put(KEY_GENDER, user.getGender() );
		userValues.put(KEY_AGE, user.getAge() );
		
		db.update(USERS_TABLE, 											// table
				  userValues, 											// values
				  KEY_ID + " = " + String.valueOf(user.getUserId()), 	// query
				  new String[] { String.valueOf(user.getUserId()) });	// arguments in query
		
		db.close();
	}
	
	public User getActiveUser() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor;
		String [] columns = { KEY_ID, KEY_ACTIVE, KEY_NAME, KEY_GENDER, KEY_AGE };
		
		cursor = db.query(USERS_TABLE, 										// db table
				          columns,											// columns selected
				          columns[1] + "=" + String.valueOf(ACTIVE_USER),   // WHERE query
				          null, null, null, null, null);					// not required...
		
		if(!cursor.moveToFirst() ) {
			db.close();
			Log.e("Visus", "Unable to find result");
			return null;
		}
		
		User user = new User(Integer.parseInt(cursor.getString(0)),		// ID
			                 Integer.parseInt(cursor.getString(1)),  	// Active?
				             cursor.getString(2),						// First name
		                     cursor.getString(3),                		// Gender
		                     Integer.parseInt(cursor.getString(4)));	// Age				
		db.close();
		
		return user;		
	}
	
	public void setUserActive(int id) {
		// get the active user
		User user = getActiveUser();
		
		// if a user is active
		if(user != null) {
			// set the user as active
			user.setActive(ACTIVE_USER);
			// update their activity status
			updateUser(user);
		}
		
		// get the user's id
		user = getUser(id);
		// set the user as active
		user.setActive(ACTIVE_USER);
		// update their activity status
		updateUser(user);
	}
	
	public User getUser(int id) throws SQLiteException {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		User user = new User();
		
		cursor = db.query(USERS_TABLE, 							// db table
				          new String[] { KEY_ID }, 				// columns
				          KEY_ID + " = " + String.valueOf(id),  // query
				          new String[] { String.valueOf(id) }, 	// return user id
				          null, null, null);					// not required...
		
		if(cursor != null)
			cursor.moveToFirst(); // move cursor to the first column
		else
			db.close();
		
		user = new User(Integer.parseInt(cursor.getString(0)),		// ID
		                Integer.parseInt(cursor.getString(1)),  	// Active?
		                cursor.getString(2),						// First name
                        cursor.getString(3),                    	// Gender
                        Integer.parseInt(cursor.getString(4)));		// Age
		
		db.close();
		return user;
	}
	
	
	/**
	 * 	Session methods
	 */
	public void add(Session session) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues sessionValues = new ContentValues();
				
		// user id
		sessionValues.put(KEY_USER_ID, session.getUserId());
		
		// date
		sessionValues.put(KEY_DAY_NO, session.getDayNo());
		sessionValues.put(KEY_DAY, session.getDay());
		sessionValues.put(KEY_MONTH, session.getMonth());
		sessionValues.put(KEY_YEAR, session.getYear());
		
		// time
		sessionValues.put(KEY_TIME_HOUR, session.getTimeHour());
		sessionValues.put(KEY_TIME_MINS, session.getTimeMinutes());
		sessionValues.put(KEY_TIMEZONE, session.getDayPeriod());
		
		// duration
		sessionValues.put(KEY_DURATION_MINS, session.getDurationMinutes());
		sessionValues.put(KEY_DURATION_SECS, session.getDurationSeconds());
		
		// type
		sessionValues.put(KEY_TYPE, session.getType());
		
		try {
			Long result = db.insert(SESSIONS_TABLE, null, sessionValues);
			
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
		finally {			
			db.close();
		}
		
	}
		
}
