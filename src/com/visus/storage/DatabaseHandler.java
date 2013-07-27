package com.visus.storage;

import java.util.List;

import com.visus.entities.Session;
import com.visus.entities.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.*;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	private SQLiteDatabase db;
	private User user;
	
	private static final int USER_ACTIVE 	 = 1;
	private static final int USER_NON_ACTIVE = 0;
		
	private static final String DATABASE_NAME 	 = "Visus";
	private static final int    DATABASE_VERSION = 1;
	
	private static final String TABLE_USERS 	= "Users";
	private static final String TABLE_SESSIONS  = "Sessions";
	
	/**
	 * USERS Table
	 */
	private static final String[] TableUsersColumnNames = { 
		"user_id", 				// user id
		"user_firstname",		// user first name
		"user_gender", 			// user gender
		"user_age" 				// user age
	};
	private static final String[] TableUsersColumnTypes = {
		"INTEGER",				// user id
		"VAR(25)",				// first name
		"VAR(10)",				// gender
		"INTEGER"				// age
	};
	private static final String[] TableUsersColumnKeys  = {	
		"PRIMARY KEY AUTO_INCREMENT",	// user id
		"NOT NULL",						// first name
		"NOT NULL",						// gender
		"NOT NULL"						// age
	};
	
	
	/**
	 * SESSIONS Table
	 */
	private static final String[] TableSessionsColumnNames = { 
		"session_id",					// session id
		"session_date_time",			// session date and time
		"session_time"					// session time (<30:00)
	};
	private static final String[] TableSessionsColumnTypes = {
		"INTEGER",						// session id
		"VAR(25)",						// date and time
		"VAR(10)"						// session time
	};
	private static final String[] TableSessionsColumnKeys  = {	
		"PRIMARY KEY AUTO_INCREMENT",	// session id
		"NOT NULL",						// date and time
		"NOT NULL"						// session time
	};
		
	/**
	 * Constructor
	 * @param context
	 */
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Adds a user
	 */
	public int addUser(User user) {
		ContentValues values = new ContentValues();
		int id = 0;
		
		values.put("active", USER_NON_ACTIVE);
		values.put("firstname", user.getFirstname());
		values.put("gender", user.getGender());
		values.put("age", user.getAge());
		
		try {
			db = this.getWritableDatabase();
			id = (int) db.insert(TABLE_USERS, null, values);
		}
		catch(SQLiteException e) {
			Log.e("SQL Error", "", e);
		} 
		finally {			
			db.close();
		}
		
		return id; // user id
	}
	
	/**
	 * Removes a user
	 */
	public void removeUser(User user) {
		
	}
		
	/**
	 * Add's a new session by user id
	 */
	public void addSession(Session session, User user) {
		
	}
	
	/**
	 * Removes a user session
	 */
	public void removeSession(Session session, User user) {
		
	}
	
	public List<Session> getSessions() {
		List<Session> sessions = null;
		
		return sessions;
    }
	
	/**
	 * Remove's all user sessions by user id
	 */
	public void removeSessions(User user) {
		
	}
	
}
