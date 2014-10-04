package com.visus.main;

import com.visus.R;
import com.visus.database.TasksTableHandler;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Tasks extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_tasks);
		
//		displayNoTasks();
	}
	
	/**
	 * Displays number of tasks available
	 */
	private void displayNoTasks() {
		TasksTableHandler tasksHandler = new TasksTableHandler(this);
		tasksHandler.open();
		int count = tasksHandler.getCount();
		tasksHandler.close();
		
//		TextView txtNoTasks = (TextView) findViewById(R.id.tasks_no_tasks);
		
		if(count == 0) {
//			txtNoTasks.setText(0);
		} else {
//			txtNoTasks.setText(count);
		}	
	}
	
}
