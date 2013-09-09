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
	
	public final static String KEY_ID       = "Id"; 	// All DB tables will share this property!
	
	/**
	 * Users table
	 */
	public static final String USERS_TABLE  = "Users";
	
	public static final String KEY_ACTIVE   = "Active";
	public static final String KEY_NAME     = "Name";
	public static final String KEY_AGE      = "Age";
	public static final String KEY_GENDER   = "Gender";
		
	public static final int ACTIVE_USER = 1;
	public static final int NON_ACTIVE_USER = 0;
	
	
	/**
	 * Sessions table
	 */
	public static final String SESSIONS_TABLE    = "Sessions";
	
	// columns
	public static final String KEY_USER_ID       = "UserId";
	public static final String KEY_DAY_NO        = "DayNo";
	public static final String KEY_DATE          = "Date";
	public static final String KEY_DAY           = "Day";
	public static final String KEY_MONTH         = "Month";
	public static final String KEY_YEAR          = "Year";
	public static final String KEY_TIME_HOUR     = "Hour";
	public static final String KEY_TIME_MINS     = "Mins";
	public static final String KEY_TIMEZONE      = "TimeZone";
	public static final String KEY_DURATION_MINS = "DurationMinutes";
	public static final String KEY_DURATION_SECS = "DurationSeconds";	
	public static final String KEY_TYPE          = "Type";
	
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.e("Visus", "User Handler Init");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// be very careful to include leave spaces between statements!!
		String createUsersTable = "CREATE TABLE " + USERS_TABLE +
				                  " ( " +
				                  	 KEY_ID + " INTEGER PRIMARY KEY, " +		// act's as our USER_ID
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
                	                 	KEY_DATE + " DATE DEFAULT CURRENT_TIMESTAMP, " +	// NEW!
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
			
}
