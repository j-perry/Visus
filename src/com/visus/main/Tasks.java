package com.visus.main;

import com.visus.R;
import com.visus.database.TasksTableHandler;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Tasks extends Activity {

	private TextView txtNoTasks;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_tasks);
		
		displayNoTasks();
	}
	
	/**
	 * Displays number of tasks available
	 */
	private void displayNoTasks() {
		TasksTableHandler tasksHandler = new TasksTableHandler(this);
		tasksHandler.open();
		int count = tasksHandler.getCount();
		tasksHandler.close();
		txtNoTasks = (TextView) findViewById(R.id.tasks_no_tasks_02);
		txtNoTasks.setTextSize(24);
		
		if(count == 0) {
			txtNoTasks.setText("0 Tasks");
		} else {
			if(count == 1) {
				txtNoTasks.setText(String.valueOf(count) + " Task");
			} else {
				txtNoTasks.setText(String.valueOf(count) + " Tasks");				
			}
		}
	}
	
	/**
	 * Displays a list of all tasks created
	 */
	private void displayTasks() {
		
	}
	
}
