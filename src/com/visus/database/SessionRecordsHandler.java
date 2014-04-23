package com.visus.database;

import java.util.*;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

/**
 * Database handler used to get and set a sessions records table that 
 * ranks accumulated minutes for each activity type (category)
 * @author Jonathan Perry
 *
 */
public class SessionRecordsHandler implements IDatabaseTable {

	private DatabaseHandler dbHandler;
	private SQLiteDatabase db;
	private StringBuilder query;
	private Cursor cursor;
	
	private String select,
				   from,
				   where,
				   orderBy;
	
	public SessionRecordsHandler(Context context) {
		dbHandler = new DatabaseHandler(context);
	}

	/**
	 * Opens the local database connection with read/write access
	 */
	@Override
	public void open() throws SQLiteException {
		db = dbHandler.getWritableDatabase(); 
	}
	
	/**
	 * Closes the local database connection
	 */
	@Override
	public void close() throws SQLiteException {
		dbHandler.close();
	}
	
	/**
	 * Gets each activity type and duration associated to each
	 * @param userId
	 * @return records
	 */
	public ArrayList<HashMap<String, Double>> getRecords(int userId) throws SQLiteException {
		ArrayList<HashMap<String, Double>> records = new ArrayList<HashMap<String, Double>>();
		HashMap<String, Double> cursorRecords = new HashMap<String, Double>();
		select = "SELECT * ";
		from   = "FROM " + ISessionsRecordTable.TABLE_NAME + " ";
		where  = "WHERE " + ISessionsRecordTable.KEY_ID + " = 1";
		query = new StringBuilder(select + from + where);
		
		cursor = db.rawQuery(query.toString(), null);
		
		int activityIndex = cursor.getColumnIndexOrThrow(ISessionsRecordTable.KEY_ACTIVITY);
		int activityDurIndex = cursor.getColumnIndexOrThrow(ISessionsRecordTable.KEY_ACTIVITY_DURATION);
		
		while(cursor.moveToNext()) {
			cursorRecords.put(cursor.getString(activityIndex), 			// activity type
							  cursor.getDouble(activityDurIndex));		// activity duration
		}
		
		cursor.close();
		records.add(cursorRecords);			
		
		return records;
	}
	
	/**
	 * Sorts activity records (hi-lo) and returns them
	 * @param userId
	 * @return records
	 */
	public HashMap<String, Double> getRecordsDesc(int userId) throws SQLiteException {
		HashMap<String, Double> cursorResults = new HashMap<String, Double>();
		
		select = "SELECT * ";
		from   = "FROM " + ISessionsRecordTable.TABLE_NAME + " ";
		orderBy = "ORDER BY " + ISessionsRecordTable.KEY_ACTIVITY_DURATION + " DESC";
		query = new StringBuilder(select + from + orderBy);
		
		Log.e("Visus", query.toString() );
		
		cursor = db.rawQuery(query.toString(), null);
		
		int activityIndex = cursor.getColumnIndexOrThrow(ISessionsRecordTable.KEY_ACTIVITY);
		int activityDurIndex = cursor.getColumnIndexOrThrow(ISessionsRecordTable.KEY_ACTIVITY_DURATION);
		
		if(cursor.getCount() != 0) {
			while(cursor.moveToNext()) {
				cursorResults.put(cursor.getString(activityIndex), 		// activity
						          cursor.getDouble(activityDurIndex));  // activity duration
			}
		}
		else {
			cursorResults = null;
		}
		
		cursor.close();
		
		return cursorResults;
	}
	
	/**
	 * Gets an activity type and record by name
	 * @param activity
	 * @return record
	 */
	public HashMap<String, Double> getActivityRecordByName(String activity) throws SQLiteException {
		HashMap<String, Double> record = new HashMap<String, Double>();
		select = "SELECT * ";
		from   = "FROM " + ISessionsRecordTable.TABLE_NAME + " ";
		where  = "WHERE " + ISessionsRecordTable.KEY_ACTIVITY + " = '" + activity + "'";
		query = new StringBuilder(select + from + where);
		
		cursor = db.rawQuery(query.toString(), null);
		
		Log.e("Visus", "getActivityRecordByName():");
		
		int activityIndex = cursor.getColumnIndexOrThrow(ISessionsRecordTable.KEY_ACTIVITY);
		int activityDurIndex = cursor.getColumnIndexOrThrow(ISessionsRecordTable.KEY_ACTIVITY_DURATION);
		
		if(cursor.getCount() != 0) {
			Log.e("Visus", "getActivityRecordByName() is not empty");
			
			while(cursor.moveToNext()) {
				Log.e("Visus", "activityIndex: " + cursor.getString(activityIndex) );
				Log.e("Visus", "activityDurIndex: " + cursor.getDouble(activityDurIndex) );
				
				
				record.put(cursor.getString(activityIndex), 	// activity
						   cursor.getDouble(activityDurIndex)); // activity duration
			}
		}
		else {
			Log.e("Visus", "getActivityRecordByName() is empty");			
		}
		
		cursor.close();
		
		return record;
	}
	
	/**
	 * Inserts a activity record
	 * @param activity
	 * @param duration
	 * @param userId
	 * @return database result
	 * @throws SQLiteException
	 */
	public int insertActivityRecord(String activity, Double duration, int userId) throws SQLiteException {
		ContentValues values = new ContentValues();
		Log.e("Visus", "insertActivityRecord(): " + userId);
		Log.e("Visus", "insertActivityRecord(): " + activity);
		Log.e("Visus", "insertActivityRecord(): " + duration);
		
		values.put(ISessionsRecordTable.KEY_USER_ID, userId);
		values.put(ISessionsRecordTable.KEY_ACTIVITY, activity);
		values.put(ISessionsRecordTable.KEY_ACTIVITY_DURATION, duration);
		
		int result = (int) db.insert(ISessionsRecordTable.TABLE_NAME, 
				               		 null, 
				               		 values);		
		
		if(result == 1) {
			Log.e("Visus", "insertActivityRecord(): activity record inserted");			
		} 
		else {
			Log.e("Visus", "insertActivityRecord(): activity record is not inserted");			
		}
		
		return result;
	}
	
	/**
	 * Updates accumulated duration of activity just performed
	 * @param activity
	 */
	public int updateActivityRecordByName(String activity, Double duration, int userId) throws SQLiteException {
		ContentValues values = new ContentValues();
		
		Log.e("Visus", "insertActivityRecord(): " + userId);
		Log.e("Visus", "insertActivityRecord(): " + activity);
		Log.e("Visus", "insertActivityRecord(): " + duration);
		
		values.put(ISessionsRecordTable.KEY_USER_ID, userId);
		values.put(ISessionsRecordTable.KEY_ACTIVITY, activity);
		values.put(ISessionsRecordTable.KEY_ACTIVITY_DURATION, duration);
		
		Log.e("Visus", "updateActivityRecordByName():");
					
		int result = db.update(ISessionsRecordTable.TABLE_NAME, 
							   values, 
							   ISessionsRecordTable.KEY_USER_ID + " = " + userId, 
							   null);
		
		if(result == 1) {
			Log.e("Visus", "updateActivityRecordByName(): activity record updated");			
		} 
		else {
			Log.e("Visus", "updateActivityRecordByName(): activity record is not updated");			
		}
		return result;
	}
	
	/**
	 * Deletes an activity by name
	 * @param userId
	 * @param activity
	 * @return database result
	 */
	public int deleteActivityByName(int userId, String activity) throws SQLiteException {
		where = ISessionsRecordTable.KEY_ID + " = " + userId +
				" AND " +
				ISessionsRecordTable.KEY_ACTIVITY + " = " + activity;
		
		int result = db.delete(ISessionsRecordTable.TABLE_NAME, 
				  			   where, 
				  			   null);
		return result;
	}
	
	/**
	 * Deletes all activity types
	 * @param userId
	 * @return
	 */
	public int deleteAllActivities(int userId) throws SQLiteException {
		where = ISessionsRecordTable.KEY_ID + " = " + userId;
		
		int result = db.delete(ISessionsRecordTable.TABLE_NAME, 
				               where, 
				               null);
		return result;
	}
}