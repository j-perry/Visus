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



public class UserHandler extends SQLiteOpenHelper {
		
	private final static String DATABASE_NAME = "Visus";
	private final static int    DATABASE_VERSION = 1;
	
	private final static String USERS_TABLE = "Users";
	
	private final static String KEY_ID = "Id";
	private final static String KEY_ACTIVE = "Active";
	private final static String KEY_NAME = "Name";
	private final static String KEY_AGE = "Age";
	private final static String KEY_GENDER = "Gender";
		
	private static final int ACTIVE_USER = 1;
	private static final int NON_ACTIVE_USER = 0;
	
	public UserHandler(Context context) {
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
				                  	 KEY_GENDER + " TEXT, " +
				                  	 KEY_AGE + " INTEGER " +
				                  " )";
		db.execSQL(createUsersTable);
		Log.e("Visus", "Users table created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
 
        // Create tables again
        onCreate(db);		
	}
	
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
		
		int id = db.delete(USERS_TABLE, 					// db name
				           KEY_ID + " = ?", 			// where clause
				           new String[] { String.valueOf(user.getUserId()) } );	// arguments
		
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
		ContentValues nuValues = new ContentValues();

		nuValues.put(KEY_ID, user.getUserId() );
		nuValues.put(KEY_ACTIVE, user.getActive() );
		nuValues.put(KEY_NAME, user.getFirstname() );
		nuValues.put(KEY_GENDER, user.getGender() );
		nuValues.put(KEY_AGE, user.getAge() );
		
		db.update(USERS_TABLE, 											// table
				  nuValues, 											// values
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
			
}
