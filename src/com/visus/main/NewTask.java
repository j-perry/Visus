package com.visus.main;

import com.visus.R;
import com.visus.database.TasksTableHandler;
import com.visus.entities.Task;
import com.visus.ui.main.fragments.LatestActivityFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewTask extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_new_task);
	}
	
	/**
	 * Add's a new task to the database
	 */
	public void addTask(View view) {
		Task task = new Task();
		EditText etTask = (EditText) findViewById(R.id.new_task_header);
		EditText etDescription = (EditText) findViewById(R.id.new_task_description);
		
		task.setTask(etTask.getText().toString() );
		task.setDescription(etDescription.getText().toString() );
		Bundle bundle = getIntent().getExtras();
		int userId = bundle.getInt("ActiveUserId");
		
		// write our data to the database table
		TasksTableHandler tasksHandler = new TasksTableHandler(this);
		
		tasksHandler.open();
		tasksHandler.add(task, userId);
		tasksHandler.close();
		
		Intent intent = new Intent(this, Tasks.class);
		startActivity(intent);
	}
	
}
