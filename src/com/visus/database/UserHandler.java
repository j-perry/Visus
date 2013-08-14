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
	
	public UserHandler(Context context) {
		dbHandler = new DatabaseHandler(context);
	}
	
	@Override
	public void open() throws SQLiteException {
		db = dbHandler.getReadableDatabase(); // reads and writes
	}
	
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
	
	public void updateUser(User user) throws SQLiteException {
		ContentValues userValues = new ContentValues();

		userValues.put(DatabaseHandler.KEY_ID, user.getUserId() );
		userValues.put(DatabaseHandler.KEY_ACTIVE, user.getActive() );
		userValues.put(DatabaseHandler.KEY_NAME, user.getFirstname() );
		userValues.put(DatabaseHandler.KEY_GENDER, user.getGender() );
		userValues.put(DatabaseHandler.KEY_AGE, user.getAge() );
		
		db.update(DatabaseHandler.USERS_TABLE, 											// table
				  userValues, 											                // values
				  DatabaseHandler.KEY_ID + " = " + String.valueOf(user.getUserId()), 	// query
				  new String[] { String.valueOf(user.getUserId()) });	                // arguments in query
		
		db.close();
	}
	
	public User getActiveUser() {
		Cursor cursor;
		String [] columns = { DatabaseHandler.KEY_ID, 
				              DatabaseHandler.KEY_ACTIVE, 
				              DatabaseHandler.KEY_NAME, 
				              DatabaseHandler.KEY_GENDER, 
				              DatabaseHandler.KEY_AGE };
		
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
			
}
