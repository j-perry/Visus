/**
 * 
 */
package com.visus.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.visus.entities.sessions.Session;

/**
 * @author Jonathan Perry
 *
 */
public class TasksTableHandler implements ITasksTable {

	private DatabaseHandler dbHandler;
	private SQLiteDatabase db;
	private Long result;
	private Session session;
	
	private Session firstSession;
		
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

}
