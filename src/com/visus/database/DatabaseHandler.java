package com.visus.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	/* Generic properties for database */
	private final static String DATABASE_NAME = "Visus";
	private final static int    DATABASE_VERSION = 1;
		
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.e("Visus", "DatabaseHandler()");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// be very careful to include leave spaces between statements!!
		String createUsersTable = "CREATE TABLE " + IUserTable.TABLE_NAME +
				                  " ( " +
				                  	 IUserTable.KEY_ID + " INTEGER PRIMARY KEY, " +		// act's as our USER_ID
				                  	 IUserTable.KEY_ACTIVE + " INTEGER, " +
				                  	 IUserTable.KEY_TARGET_DAY + " INTEGER, " +			
				                  	 IUserTable.KEY_TARGET_MONTH + " INTEGER, " +		
				                  	 IUserTable.KEY_DURATION_TODAY + " INTEGER, " +		
				                  	 IUserTable.KEY_DURATION_MONTH + " INTEGER" +		
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
		
		String createSessionsRecordTable = "CREATE TABLE " + ISessionsRecordTable.TABLE_NAME +
				                           " ( " +
				                           		ISessionsRecordTable.KEY_ID + " INTEGER PRIMARY KEY, " +
				                           		ISessionsRecordTable.KEY_USER_ID + " INTEGER, " +
				                           		ISessionsRecordTable.KEY_ACTIVITY + " TEXT, " +
				                           		ISessionsRecordTable.KEY_ACTIVITY_DURATION + " TEXT " +
				                           ");";
		
		String createTasksRecordTable = "CREATE TABLE " + ITasksTable.TABLE_NAME +
										" ( " +
				                        	ITasksTable.KEY_ID + " INTEGER PRIMARY KEY, " +
										    ITasksTable.KEY_USER_ID + " INTEGER, " +
				                        	ITasksTable.KEY_TASK + " TEXT, " +
										    ITasksTable.KEY_TASK_DESCRIPTION + " TEXT " +
										");";
		
		db.execSQL(createUsersTable);
		Log.e("Visus", "Users table created");
		
		db.execSQL(createSessionsRecordTable);
		Log.e("Visus", "Session Records table created");
		
		db.execSQL(createSessionsTable);
		Log.e("Visus", "Sessions table created");
		
		db.execSQL(createTasksRecordTable);
		Log.e("Visus", "Tasks table created");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + IUserTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ISessionTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ITasksTable.TABLE_NAME);
 
        // Create tables again
        onCreate(db);		
	}
			
}
