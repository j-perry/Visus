/**
 * 
 */
package com.visus.database;

import java.util.*;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

import com.visus.entities.sessions.Session;
import com.visus.entities.Task;

/**
 * @author Jonathan Perry
 *
 */
public class TasksTableHandler implements ITasksTable {

	private DatabaseHandler dbHandler;
	private SQLiteDatabase db;
	private Long result;
			
	private static final String QRY_SPACING = " ";
	
	public TasksTableHandler(Context context) {
		dbHandler = new DatabaseHandler(context);
	}
	
	public void open() {
		db = dbHandler.getWritableDatabase();
	}
	
	public void close() {
		dbHandler.close();
	}
	
	/**
	 * Add's a new task to the ToDo list
	 * @param task
	 * @throws SQLiteException
	 */
	public void add(Task task, int userId) throws SQLiteException {
		ContentValues values = new ContentValues();
		
		values.put(ITasksTable.KEY_USER_ID, userId);
		values.put(ITasksTable.KEY_TASK, task.getTask() );
		values.put(ITasksTable.KEY_TASK_DESCRIPTION, task.getDescription() );
		values.put(ITasksTable.KEY_DAY, task.getDay() );
		values.put(ITasksTable.KEY_MONTH, task.getMonth() );
		values.put(ITasksTable.KEY_YEAR, task.getYear() );
		
		try {
			result = db.insert(ITasksTable.TABLE_NAME, null, values);
		} catch(SQLiteException ex) {
			Log.e("Visus", "Error writing to database Sessions", ex);
		} finally {
			// -1 denotes, unsuccessful. 0 and higher denotes no. of written items
			if(result == -1) {
				Log.e("Visus", "----------------");
				Log.e("Visus", String.valueOf(result) + " | Failed to write to db");
				Log.e("Visus", "----------------");
			} else {
				Log.e("Visus", "----------------");
				Log.e("Visus", "ID: " + String.valueOf(result) + " | Written to db");
			}
		}
	}
	
	/**
	 * Return's all tasks to be completed
	 * @param userId
	 * @return
	 */
	public ArrayList<HashMap<String, Object>> get(int userId) {
		ArrayList<HashMap<String, Object>> tasks = new ArrayList<HashMap<String, Object>>();
		String queryTasks = "SELECT * " +
							"FROM " + ITasksTable.TABLE_NAME + " " +
							"WHERE " + ITasksTable.KEY_USER_ID + " = " + userId; 
		Cursor cursor = null;		
		
		cursor = db.rawQuery(queryTasks, null);
		
		int dayIndex = cursor.getColumnIndex(ITasksTable.KEY_DAY);
		int monthIndex = cursor.getColumnIndex(ITasksTable.KEY_MONTH);
		int yearIndex = cursor.getColumnIndex(ITasksTable.KEY_YEAR);
		
		int taskIndex = cursor.getColumnIndex(ITasksTable.KEY_TASK);
		int taskDescIndex = cursor.getColumnIndex(ITasksTable.KEY_TASK_DESCRIPTION);
		
		// store each item
		while(cursor.moveToNext() ) {
			HashMap<String, Object> task = null;
			
			task.put(ITasksTable.KEY_TASK, cursor.getString(taskIndex) );
			task.put(ITasksTable.KEY_TASK_DESCRIPTION, cursor.getString(taskDescIndex) );
			task.put(ITasksTable.KEY_DAY, cursor.getInt(dayIndex) );
			task.put(ITasksTable.KEY_MONTH, cursor.getInt(monthIndex) );
			task.put(ITasksTable.KEY_YEAR, cursor.getInt(yearIndex) );
			
			tasks.add(task);
		}
		
		cursor.close();
		
		return tasks;
	}
	
	/**
	 * Removes a ToDo task from the database
	 * @param id
	 * @return
	 */
	public int remove(int userId, int id) {
		String whereClause = ITasksTable.KEY_ID + " = " + id + " AND " +
				             ITasksTable.KEY_USER_ID + " = " + userId;
		int result = 0;
		
		result = db.delete(ITasksTable.TABLE_NAME, whereClause, null);
		
		return result;
	}
	
	public int getCount() {
		int noTasks = 0;
		String qry = "SELECT count(*) " +
					 "FROM " + ITasksTable.TABLE_NAME;
		Cursor cursor = null;
		
		cursor = db.rawQuery(qry, null);
		
		if(cursor == null) {
			noTasks = 0;
		} else {
			noTasks = cursor.getCount();
		}
				
		cursor.close();
		
		return noTasks;
	}
	
}
