package com.visus.database;

import com.visus.entities.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserHandler implements IDatabaseTable {
			
	private SQLiteDatabase db;
	private DatabaseHandler dbHandler;
	private static final String QRY_SPACING = " ";
	
	public UserHandler(Context context) {
		dbHandler = new DatabaseHandler(context);
	}
	
	/**
	 * Opens a new database connection
	 */
	@Override
	public void open() throws SQLiteException {
		db = dbHandler.getReadableDatabase(); // reads and writes
	}
	
	/**
	 * Closes the existing database connection
	 */
	@Override
	public void close() throws SQLiteException {
		db.close();
	}
		
	public void addUser(User user) throws SQLiteException {
		ContentValues userValues = new ContentValues();
		
		userValues.put(DatabaseHandler.KEY_ACTIVE, DatabaseHandler.ACTIVE_USER );
		userValues.put(DatabaseHandler.KEY_NAME, user.getFirstname() );
		userValues.put(DatabaseHandler.KEY_GENDER, user.getGender() );
		userValues.put(DatabaseHandler.KEY_AGE, user.getAge() );
		userValues.put(DatabaseHandler.KEY_TARGET_DAY, user.getTargetDay() );
		userValues.put(DatabaseHandler.KEY_TARGET_MONTH, user.getTargetMonth() );
		
		db.insert(DatabaseHandler.USERS_TABLE, null, userValues);
		Log.e("Visus", "New user added");
	}
	
	public void deleteUser() {
		User user = null;
		user = getActiveUser();
		
		Log.e("Visus", "User ID = " + String.valueOf(user.getFirstname() ) );
		
		int id = db.delete(DatabaseHandler.USERS_TABLE, 					
				           DatabaseHandler.KEY_ID + " = ?", 			
				           new String[] { String.valueOf(user.getUserId()) } );	
		
		if(id == 1) {
			Log.e("Visus", "Success");
		}
		else
			Log.e("Visus", "Unsuccessful");
		
		Log.e("Visus", "DB closed");
	}
	
	/**
	 * 
	 * @param user
	 * @throws SQLiteException
	 */
	public void updateUser(User user) throws SQLiteException {
		ContentValues updatedUserValues = new ContentValues();
		
		updatedUserValues.put(DatabaseHandler.KEY_NAME, user.getFirstname() );
		updatedUserValues.put(DatabaseHandler.KEY_GENDER, user.getGender() );
		updatedUserValues.put(DatabaseHandler.KEY_AGE, user.getAge() );
		updatedUserValues.put(DatabaseHandler.KEY_TARGET_DAY, user.getTargetDay() );
		updatedUserValues.put(DatabaseHandler.KEY_TARGET_MONTH, user.getTargetMonth() );
		
		Log.e("Visus", "updateUser()");
		Log.e("Visus", "Firstname: " + user.getFirstname() );
		Log.e("Visus", "Gender: " + user.getFirstname() );
		Log.e("Visus", "Age: " + user.getAge() );
		Log.e("Visus", "Target (Day): " + user.getTargetDay() );
		Log.e("Visus", "Target (Month): " + user.getTargetMonth() );
		
		int result = db.update(DatabaseHandler.USERS_TABLE, 										// table
				               updatedUserValues, 											        // values
				               DatabaseHandler.KEY_ID + " = " + String.valueOf(user.getUserId()), 	// query
				               null);	                											// arguments in query
		
		Log.e("Visus", "updateUser(): Result: " + result);
	}
	
	/**
	 * 
	 * @return
	 */
	public User getActiveUser() {
		Cursor cursor;
		String [] columns = { DatabaseHandler.KEY_ID, 
				              DatabaseHandler.KEY_ACTIVE, 
				              DatabaseHandler.KEY_NAME, 
				              DatabaseHandler.KEY_GENDER, 
				              DatabaseHandler.KEY_AGE,
				              DatabaseHandler.KEY_TARGET_DAY,
				              DatabaseHandler.KEY_TARGET_MONTH
				            };
		
		cursor = db.query(DatabaseHandler.USERS_TABLE, 										// db table
				          columns,															// columns selected
				          columns[1] + "=" + String.valueOf(DatabaseHandler.ACTIVE_USER),   // WHERE query
				          null, null, null, null, null);									// not required...
		
		if(!cursor.moveToFirst() ) {
			db.close();
			Log.e("Visus", "Unable to find result");
			return null;
		}
		
		User user = new User(Integer.parseInt(cursor.getString(0)),		// ID
			                 Integer.parseInt(cursor.getString(1)),  	// Active?
				             cursor.getString(2),						// First name
		                     cursor.getString(3),                		// Gender
		                     cursor.getInt(4),							// Age
		                     cursor.getInt(5),							// Target (Day)
		                     cursor.getInt(6));							// Target (Month)
		db.close();
		
		return user;		
	}
	
	public void setUserActive(int id) {
		// get the active user
		User user = getActiveUser();
		
		// if a user is active
		if(user != null) {
			// set the user as active
			user.setActive(DatabaseHandler.ACTIVE_USER);
			// update their activity status
			updateUser(user);
		}
		
		// get the user's id
		user = getUser(id);
		// set the user as active
		user.setActive(DatabaseHandler.ACTIVE_USER);
		// update their activity status
		updateUser(user);
	}
	
	public User getUser(int id) throws SQLiteException {
		Cursor cursor = null;
		User user = new User();
		
		cursor = db.query(DatabaseHandler.USERS_TABLE, 							// db table
				          new String[] { DatabaseHandler.KEY_ID }, 				// columns
				          DatabaseHandler.KEY_ID + " = " + String.valueOf(id),  // query
				          new String[] { String.valueOf(id) }, 					// return user id
				          null, null, null);									// not required...
		
		if(cursor != null) {
			cursor.moveToFirst(); // move cursor to the first column
		}
		else {
			db.close();
		}
		
		user = new User(Integer.parseInt(cursor.getString(0)),		// ID
                        Integer.parseInt(cursor.getString(1)),  	// Active?
                        cursor.getString(2),						// First name
                        cursor.getString(3),                		// Gender
                        cursor.getInt(4),							// Age
                        cursor.getInt(5),							// Target (Day)
                        cursor.getInt(6));							// Target (Month)
		
		db.close();
		return user;
	}
	
	/**
	 * 
	 * @param userId
	 * @param duration
	 * @throws SQLiteException
	 */
	public void setDurationToday(int userId, float duration) throws SQLiteException {
		ContentValues durationTodayVal = new ContentValues();
		durationTodayVal.put(DatabaseHandler.KEY_DURATION_TODAY, duration);
		
		Log.e("Visus", "setDurationToday(): " + duration);
		
		int result = db.update(DatabaseHandler.USERS_TABLE, 
				  			   durationTodayVal, 
				  			   DatabaseHandler.KEY_ID + "=" + userId,
				               null);
		
		Log.e("Visus", "setDurationToday(): written result succcessfully: " + result);
		
		db.close();
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws SQLiteException
	 */
	public float getDurationToday(int userId) throws SQLiteException {
		float duration = 0.0f;
		String qry = "SELECT " + DatabaseHandler.KEY_DURATION_TODAY + QRY_SPACING +
			     	 "FROM " + DatabaseHandler.USERS_TABLE + QRY_SPACING +
			     	 "WHERE " + DatabaseHandler.KEY_ID + " = " + userId;
		
		Log.e("Visus", "UserHandler.getDurationToday()");
		Log.e("Visus", qry);
		
		Cursor cursor = db.rawQuery(qry, null);
		
		while(cursor.moveToNext()) {
			duration += cursor.getFloat(0);
		}
		
		if(duration == 0.0f) {
			duration = 0.0f;
		}
		
		cursor.close();
		db.close();
		
		return duration;
	}
	
	/**
	 * 
	 * @param userId
	 * @param duration
	 * @throws SQLiteException
	 */
	public void setDurationMonth(int userId, float duration) throws SQLiteException {
			
		
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws SQLiteException
	 */
	public float getDurationMonth(int userId) throws SQLiteException {
		float duration = 0.0f;
		String qry = "SELECT date('now')" + QRY_SPACING +
			     	 "FROM" + QRY_SPACING + DatabaseHandler.USERS_TABLE + QRY_SPACING +
			     	 "WHERE" + QRY_SPACING + 
			     	 	DatabaseHandler.KEY_USER_ID + "=" + QRY_SPACING + userId +
			     	 "AND" + QRY_SPACING +
			     	 	DatabaseHandler.KEY_DATE + QRY_SPACING + "BETWEEN" + QRY_SPACING + 
			     	 		"date('date')" + QRY_SPACING + "AND" + QRY_SPACING +
			     	 		"date('date')";
	
		Log.e("Visus", "UserHandler.getDurationMonth()");
		Log.e("Visus", qry);
	
		Cursor cursor = db.rawQuery(qry, null);
		int durationIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DURATION_MONTH); // TODO
		
		duration = cursor.getFloat(durationIndex);
		
		db.close();
		cursor.close();
		
		return duration;
	}
	
	/**
	 * Return's the user's daily target (usage)
	 * @param userId
	 * @return their daily target (hours)
	 * @throws SQLiteException
	 */
	public int getDailyTarget(int userId) throws SQLiteException {
		int dailyTarget = 0;
		String qryDailyTarget = "SELECT" + QRY_SPACING + DatabaseHandler.KEY_TARGET_DAY + QRY_SPACING + 
				                "FROM" + QRY_SPACING + DatabaseHandler.USERS_TABLE + QRY_SPACING +
				                "WHERE" + QRY_SPACING + DatabaseHandler.KEY_USER_ID + " = " + userId;
		
		Log.e("Visus", "getDailyTarget(): " + qryDailyTarget);
		
		Cursor cursor = db.rawQuery(qryDailyTarget, null);
		int targetDayIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TARGET_DAY);

		dailyTarget = cursor.getInt(targetDayIndex);
		
		db.close();
		cursor.close();
				
		return dailyTarget;
	}
		
	/**
	 * Return's the user's monthly target (usage)
	 * @param userId
	 * @return their monthly target (hours)
	 * @throws SQLiteException
	 */
	public int getMonthlyTarget(int userId) throws SQLiteException {
		int monthlyTarget = 0;
		String qryDailyTarget = "SELECT" + QRY_SPACING + DatabaseHandler.KEY_TARGET_MONTH + QRY_SPACING + 
                				"FROM" + QRY_SPACING + DatabaseHandler.USERS_TABLE + QRY_SPACING +
                				"WHERE" + QRY_SPACING + DatabaseHandler.KEY_USER_ID + " = " + userId;
		
		Log.e("Visus", "getMonthlyTarget: " + qryDailyTarget);
		
		Cursor cursor = db.rawQuery(qryDailyTarget, null);
		int targetMonthIndex = cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TARGET_MONTH);

		monthlyTarget = cursor.getInt(targetMonthIndex);
		
		db.close();
		cursor.close();
				
		return monthlyTarget;
	}	
}
