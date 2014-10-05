package com.visus.main;

import java.util.Calendar;

import com.visus.R;
import com.visus.database.TasksTableHandler;
import com.visus.entities.Task;
import com.visus.ui.main.fragments.LatestActivityFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewTask extends Activity {
	
	private TextView tvDate;
	private int day;
	private int month;
	private int year;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_new_task);
		
		tvDate = (TextView) findViewById(com.visus.R.id.new_task_show_date_picker);
		tvDate.setText("Set date");
		
		Calendar cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
	}
	
	@SuppressWarnings("deprecation")
	public void setDate(View view) {
		showDialog(999);
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		if(id == 999) {
			return new DatePickerDialog(this, myDateListener, year, month, day);
		}
		return null;
	}
	
	/**
	 * Add's a new task to the database
	 */
	public void addTask(View view) {
		Task task = new Task();
		EditText etTask = (EditText) findViewById(R.id.new_task_header);
		EditText etDescription = (EditText) findViewById(R.id.new_task_description);		
		tvDate = (TextView) findViewById(com.visus.R.id.new_task_show_date_picker);
		
		task.setTask(etTask.getText().toString() );
		task.setDescription(etDescription.getText().toString() );
		task.setDay(day);
		task.setMonth(month);
		task.setYear(year);
		
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
		
	/**
	 * Create an event listener that displays a date picker
	 */
	private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			tvDate = (TextView) findViewById(com.visus.R.id.new_task_show_date_picker);
			tvDate.setText("");
			setDatePicker(dayOfMonth, monthOfYear+1, year);
		}
		
	};
	
	/**
	 * 
	 * @param day
	 * @param month
	 * @param year
	 */
	private void setDatePicker(int day, int month, int year) {
		tvDate = (TextView) findViewById(com.visus.R.id.new_task_show_date_picker);
		tvDate.setText(day + "/" + month + "/" + year);
	}
	
}
