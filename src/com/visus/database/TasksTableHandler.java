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
	private Session session;
			
	private static final String QRY_SPACING = " ";
		
	public TasksTableHandler(Context context) {
		dbHandler = new DatabaseHandler(context);
	}
	
	public void open() {
		dbHandler.getWritableDatabase();
	}
	
	public void close() {
		dbHandler.close();
	}
	
	/**
	 * Add's a new task to the ToDo list
	 * @param task
	 * @throws SQLiteException
	 */
	public void add(Task task) throws SQLiteException {
		ContentValues values = new ContentValues();
		
		values.put(KEY_USER_ID, task.getUserId() );
		values.put(KEY_TASK, task.getTask() );
		values.put(KEY_TASK_DESCRIPTION, task.getDescription() );
		values.put(KEY_DAY, task.getDay() );
		values.put(KEY_MONTH, task.getMonth() );
		values.put(KEY_YEAR, task.getYear() );
		
		try {
			db.insert(TABLE_NAME, null, values);
		} 
		catch(SQLiteException ex) {
			Log.e("Visus", "Error writing to database Sessions", ex);
		}
	}
	
	/**
	 * Return's all tasks to be completed
	 * @param userId
	 * @return
	 */
	public ArrayList<Task> get(int userId) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Task task = new Task();
		Cursor cursor = null;
		String queryTasks = "SELECT * " +
							"FROM " + ITasksTable.TABLE_NAME + 
							" WHERE " + ITasksTable.KEY_USER_ID + " = " + userId; 
		
		cursor = db.rawQuery(queryTasks, null);
		
		int dayIndex = cursor.getColumnIndex(ITasksTable.KEY_DAY);
		int monthIndex = cursor.getColumnIndex(ITasksTable.KEY_MONTH);
		int yearIndex = cursor.getColumnIndex(ITasksTable.KEY_YEAR);
		
		int taskIndex = cursor.getColumnIndex(ITasksTable.KEY_TASK);
		int taskDescIndex = cursor.getColumnIndex(ITasksTable.KEY_TASK_DESCRIPTION);
		
		while(cursor.moveToNext() ) {
			task.setDay(cursor.getInt(dayIndex));
			task.setMonth(cursor.getInt(monthIndex));
			task.setYear(cursor.getInt(yearIndex));
			
			task.setTask(cursor.getString(taskIndex));
			task.setDescription(cursor.getString(taskDescIndex));
			
			tasks.add(task);
		}
		
		cursor.close();
		
		return tasks;
	}
	
	public void remove(int id, int userId) {
		
	}

}
