package com.visus.main;

import java.util.ArrayList;
import java.util.HashMap;

import com.visus.R;
import com.visus.database.TasksTableHandler;
import com.visus.ui.ListViewAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class Tasks extends Activity {

	private int userId;
	private TextView txtNoTasks;
	
	private ListView list;
	private ListViewAdapter.Tasks adapter;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_tasks);
		
		Bundle bId = getIntent().getExtras();
		userId = bId.getInt("ActiveUserId");
		
		displayNoTasks();
		displayTasks(userId);
	}
	
	/**
	 * Displays number of tasks available
	 */
	private void displayNoTasks() {
		TasksTableHandler tasksHandler = new TasksTableHandler(this);
		final int TXT_NO_TASKS_TYPEFACE_SIZE = 24;
		
		tasksHandler.open();
		int count = tasksHandler.getCount();		
		tasksHandler.close();
		
		txtNoTasks = (TextView) findViewById(R.id.tasks_no_tasks_02);
		txtNoTasks.setTextSize(TXT_NO_TASKS_TYPEFACE_SIZE);
		
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
	private void displayTasks(int userId) {
		ArrayList<HashMap<String, Object>> tasks = new ArrayList<HashMap<String, Object>>();
		int index = 0;
		int noItems = 0;
		TasksTableHandler tasksHandler = new TasksTableHandler(this);
		tasksHandler.open();
		
		tasks = tasksHandler.get(userId);
		noItems = tasks.size();
		
		for(HashMap<String, Object> item : tasks) {
			if(index != noItems) {
				tasks.add(item);
				index++;
			}
		}
		
		/***************************************************
		 * 		Display the user's defined tasks
		 */
		list = (ListView) findViewById(com.visus.R.id.listview_tasks);
		adapter = new ListViewAdapter.Tasks(this, tasks);
		
		list.setAdapter(adapter);
	}
	
}
