package com.example.androiddemo;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;

/**
 * Creates a new session
 * @author Jonathan Perry
 * @version 1.0
 */
@SuppressLint("SimpleDateFormat")
public class NewSession extends Activity {
	
	private TextView time;
	private Date date;
	private SimpleDateFormat df;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session);
		
		// format the current time
		df = new SimpleDateFormat("HH:mm aa");
		date = new Date();
		
		// set the current time
		time = (TextView) findViewById(R.id.clock_time);
		time.setText(df.format(date));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_view, menu);
		return true;
	}
	
	/**
	 * Navigate's the user to their previous sessions
	 * @param savedInstance
	 */
	public void onEnd(View view) {
		Intent intent = new Intent(NewSession.this, PrevSessions.class);
		startActivity(intent);
	}

}
