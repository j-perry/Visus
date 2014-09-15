/**
 * 
 */
package com.visus.database;

import java.util.*;

import android.content.*;
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
		finally {
			db.close();
		}	
	}
	
	public ArrayList<Task> get() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		
		return tasks;
	}

}
