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
	
//	public final static String KEY_ID       = "Id"; 	// All DB tables will share this property!
	
	/**
	 * 				Users table
	 */
//	public static final String USERS_TABLE  = "Users";
//	
//	public static final String KEY_ACTIVE   = "Active";
//	public static final String KEY_NAME     = "Name";
//	public static final String KEY_AGE      = "Age";
//	public static final String KEY_GENDER   = "Gender";
//	
//	public static final String KEY_TARGET_DAY = "TargetDay";
//	public static final String KEY_TARGET_MONTH = "TargetMonth";
//	
//	public static final String KEY_DURATION_TODAY = "DurationToday";
//	public static final String KEY_DURATION_MONTH = "DurationMonth";
//		
//	public static final int ACTIVE_USER = 1;
//	public static final int NON_ACTIVE_USER = 0;
	
	
	/**
	 * 				Sessions table
	 */
//	public static final String SESSIONS_TABLE    = "Sessions";
//	
//	// columns
//	public static final String KEY_USER_ID       = "UserId";
//	public static final String KEY_DAY_NO        = "DayNo";
//	public static final String KEY_DATE          = "Date";
//	public static final String KEY_DAY           = "Day";
//	public static final String KEY_MONTH         = "Month";
//	public static final String KEY_YEAR          = "Year";
//	public static final String KEY_TIME_HOUR     = "Hour";
//	public static final String KEY_TIME_MINS     = "Mins";
//	public static final String KEY_TIMEZONE      = "TimeZone";
//	public static final String KEY_DURATION_MINS = "DurationMinutes";
//	public static final String KEY_DURATION_SECS = "DurationSeconds";	
//	public static final String KEY_TYPE          = "Type";
		
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.e("Visus", "User Handler Init");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// be very careful to include leave spaces between statements!!
		String createUsersTable = "CREATE TABLE " + IUserTable.TABLE_NAME +
				                  " ( " +
				                  	 IUserTable.KEY_ID + " INTEGER PRIMARY KEY, " +		// act's as our USER_ID
				                  	 IUserTable.KEY_ACTIVE + " INTEGER, " +
				                  	 IUserTable.KEY_NAME + " TEXT, " +
				                  	 IUserTable.KEY_AGE + " INTEGER, " +
				                  	 IUserTable.KEY_GENDER + " TEXT, " +
				                  	 IUserTable.KEY_TARGET_DAY + " INTEGER, " +			// new!
				                  	 IUserTable.KEY_TARGET_MONTH + " INTEGER, " +			// new!
				                  	 IUserTable.KEY_DURATION_TODAY + " INTEGER, " +		// new!
				                  	 IUserTable.KEY_DURATION_MONTH + " INTEGER" +			// new!
				                  ");";
		
		String createSessionsTable = "CREATE TABLE " + ISessionTable.TABLE_NAME + 
                                     " ( " +
                                     	 ISessionTable.KEY_ID + " INTEGER PRIMARY KEY, " + 
                                     	 ISessionTable.KEY_USER_ID + " INTEGER, " +
	                                     ISessionTable.KEY_DAY_NO + " INTEGER, " +
	                                     ISessionTable.KEY_DATE + " DATE DEFAULT CURRENT_TIMESTAMP, " +	// NEW!
	                                     ISessionTable.KEY_DAY + " TEXT, " +
	                                     ISessionTable.KEY_MONTH + " TEXT, " +
	                                     ISessionTable.KEY_YEAR + " INTEGER, " +
	                                     ISessionTable.KEY_TIME_HOUR + " TEXT, " +
	                                     ISessionTable.KEY_TIME_MINS + " TEXT, " +
	                                     ISessionTable.KEY_TIMEZONE + " TEXT, " +
	                                     ISessionTable.KEY_DURATION_MINS + " REAL, " +
	                                     ISessionTable.KEY_DURATION_SECS + " REAL, " +
	                                     ISessionTable.KEY_TYPE + " TEXT " +
                	                 ");";
		
		db.execSQL(createUsersTable);
		Log.e("Visus", "Users table created");
		
		db.execSQL(createSessionsTable);
		Log.e("Visus", "Sessions table created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + IUserTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ISessionTable.TABLE_NAME);
 
        // Create tables again
        onCreate(db);		
	}
			
}
