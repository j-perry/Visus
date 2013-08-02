package com.visus.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import com.visus.R;
import com.visus.database.SessionHandler;
import com.visus.database.UserHandler;
import com.visus.entities.Session;
import com.visus.entities.TimerConvert;
import com.visus.entities.User;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Creates a new session
 * @author Jonathan Perry
 * @version 1.0
 */
@SuppressLint("SimpleDateFormat")
public class NewSession extends Activity {
			
	private Session session;
	private SessionHandler sessionHandler;
	private UserHandler userHandler;
	
	private int activeUserId;
	
	private Handler timeHandler, 
					timerHandler;
	
	private boolean terminateCount = false;
	private int timerCount = 0;
	
	private int [] sessionDuration;
	private int durationMilli;
				
	private long TIMER_DURATION; // minutes
	
	// ui components
	private TextView clockTime, timer;
	private Button startTimerBtn, stopTimerBtn;
	
	private CountDownTimer sessionTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session);
		
		ActionBar ab = getActionBar();
		
		initUIComponents();
		
		// TODO we'll need to add user selection functionality later!
		// TODO this is temporary code
//		setDuration();
										
		session = new Session();
		sessionHandler = new SessionHandler(this);
		
		// get the active user id
		Bundle userId = getIntent().getExtras();
		
		// if userId is not null
		if(userId != null) {
			// get the user id (int)
			activeUserId = userId.getInt("ActiveUserId");
			Log.e("Visus", "New Session - User id is " + activeUserId);
		}
		else {
			// find the active user
			User user = userHandler.getActiveUser();
			activeUserId = user.getUserId();
		}
		
		// create a new instance of handler to manage the timer thread
		//timeHandler = new Handler();
		
		// default - ensures the timer doesn't start
		//timeHandler.removeCallbacks(runUpdateTime);
		
		// delay the timer runnable for 1 second
		//timeHandler.postDelayed(runUpdateTime, 0);
		
		timerHandler = new Handler();
		
		timerHandler.removeCallbacks(runUpdateTimer);
	}
	
	/**
	 * Initialise all UI components
	 */
	private void initUIComponents() {
		//clockTime = (TextView) findViewById(R.id.clock_time);
		timer = (TextView) findViewById(R.id.timer);
		startTimerBtn = (Button) findViewById(R.id.timer_btn);
		stopTimerBtn = (Button) findViewById(R.id.timer_stop_btn);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_new_view, menu);
		return true;
	}
	
	/**
	 * Event handler 
	 * 
	 * Starts a new session
	 * @param view
	 */
	public void onStart(View view) {
		// hide the session duration setter layout 
		LinearLayout sessionDuration = (LinearLayout) findViewById(R.id.set_session_duration);
		sessionDuration.setVisibility(View.GONE);		

		timerHandler = new Handler();
		
		String strFormatDay = "EEEE";
		String strFormatMonth = "MMMM";
		String strFormatYear = "yyyy";
		
		// display the timer
		TextView timer = (TextView) findViewById(R.id.timer);
		timer.setVisibility(View.VISIBLE);
		
		// find EditText fields for value retrieval
		EditText etMins = (EditText) findViewById(R.id.timer_set_minutes);
		EditText etSecs = (EditText) findViewById(R.id.timer_set_seconds);
		
		// NB: 'i' denotes input
		int iMins = 0;
		int iSecs = 0;
		
		// if both fields are empty
		if( (etMins.getText().toString().isEmpty() ) &&
		    (etSecs.getText().toString().isEmpty() )) {
			
			String msg = "Enter at least one value\nin either of the input fields";
			
			// inform the user they are both empty ...
			Toast.makeText(getApplicationContext(), 
					       msg,
					       Toast.LENGTH_SHORT).show();
			
			// ... and delay the start time
			timerHandler.removeCallbacks(runUpdateTimer);
			
		}
		// if minutes field is empty
		// and seconds field is not empty
		else if( (etMins.getText().toString().isEmpty()) && 
				 ( !etSecs.getText().toString().isEmpty() )) {
			
			// append two noughts
			iMins = 00;
			iSecs = Integer.parseInt( etSecs.getText().toString() );
		}
		// if minutes field is not empty
		// and seconds field is empty
		else if( ( !etMins.getText().toString().isEmpty() ) &&
				 (etSecs.getText().toString().isEmpty()) ) {
			
			iMins = Integer.parseInt( etMins.getText().toString() );
			
			// append two noughts
			iSecs = 00;
		}
		// if both fields are not empty
		else {
			// if the length of the input is 1 - i.e., min = 1
			if(etMins.getText().toString().length() == 1) {
				// and the number is less than 10 minutes
				if(Integer.parseInt( etMins.getText().toString()) < 10) {
					// append a nought to minutes
					iMins = 0;
					iMins += Integer.parseInt(etMins.getText().toString() );
				}
			}
			else {
				iMins = Integer.parseInt(etMins.getText().toString() );
			}
			
			// repeat (for seconds)...
			if(etSecs.getText().toString().length() == 1) {
				// and the number is less than 10 seconds		
				if(Integer.parseInt( etSecs.getText().toString()) < 10) {
					iSecs = 0;
					iSecs += Integer.parseInt(etSecs.getText().toString() );
				}
			} else {
				iSecs = Integer.parseInt(etSecs.getText().toString() );
			}
		}
			
		
		// convert inputed session duration into milliseconds
		setDuration(iMins, iSecs);
		
		// 
		String day = new SimpleDateFormat(strFormatDay).format(new Date() );
		String month = new SimpleDateFormat(strFormatMonth).format(new Date() );
		String year = new SimpleDateFormat(strFormatYear).format(new Date() );
		
		Log.e("Visus", day + " " + month + " " + year);
		
		timerHandler.removeCallbacks(runUpdateTimer);				
		timerHandler.postDelayed(runUpdateTimer, 0);
		
		session.setDate(day,	// day - e.g., Thursday
		                month,	// month - e.g., April
		                year);	// year - e.g., 2013
		
		// calendar date
		Log.e("Visus", "Set date: " + session.getDate());
		
		String hour = new SimpleDateFormat("hh").format(new Date() );
		String minutes = new SimpleDateFormat("mm").format(new Date() );
		String seconds =  new SimpleDateFormat("ss").format(new Date() );
		
		if(Integer.parseInt(hour) < 10)
			hour = "0" + hour;
		if(Integer.parseInt(minutes) < 10)
			minutes = "0" + minutes;
		if(Integer.parseInt(seconds) < 10) 
			seconds = "0" + seconds;		
		
		session.setTime(Integer.parseInt(hour),		// hour
						Integer.parseInt(minutes), 	// minutes
						Integer.parseInt(seconds));	// seconds
		
		// clock time
		Log.e("Visus", "Set time: " + session.getTime());		
	}
	
	/**
	 * Pauses the session
	 * @param view
	 */
	public void onPause(View view) {
		// pause the timer
		timerHandler.removeCallbacks(runUpdateTimer);
	}
	
	/**
	 * Ends the session
	 * @param view
	 */
	public void onEnd(View view) {
		timerHandler.removeCallbacks(runUpdateTimer);
				
//		sessionDuration[0] = ((getDuration() / (1000 * 60)) % 60);	// minutes
//		sessionDuration[1] = ((getDuration() / 1000) % 60);			// seconds
				
		session.setUserId(activeUserId);
		session.setDuration(30, 00); // TODO validation test
		
		// write session to db
		sessionHandler.add(session);
		
//		Intent intent = new Intent(NewSession.this, PrevSessions.class);
//		startActivity(intent);
	}	
	
	/**
	 * Sets the session duration and converts inputed 
	 * minutes and seconds into milliseconds
	 */
	private void setDuration(int minutes, int seconds) {
		TimerConvert convert = new TimerConvert();
		
		this.durationMilli = convert.minutesAndSecondsToMilliseconds(minutes, seconds);
	}
	
	/**
	 * Gets the session duration
	 * @return
	 */
	private int getDuration() {
		return durationMilli;
	}
	
	/**
	 * Executes update time thread on UI
	 */
	private Runnable runUpdateTime = new Runnable() {
		// NB: run() executes the thread
		public void run() {
			// very important method - runOnUiThread(runnable)
			runOnUiThread(updateTimeOnUi);
			
			if(terminateCount == true) {
				timerCount++;
			}
			
			if(timerCount > 25) {
				timeHandler.removeCallbacksAndMessages(runUpdateTime);
			}
			else {
				// delay for 1 second
				timeHandler.postDelayed(this, 1000);
			}			
		}
	};
	
	/**
	 * UI handler starts the update time 
	 */
	private Runnable updateTimeOnUi = new Runnable() {
		// run() executes the thread
		public void run() {
			// updates the timer
			updateTime();
		}
	};
	
	/**
	 * Sets the clock time
	 * NB: This method does not handle the clock timer!
	 */
	private void updateTime() {
		// set the clock time (hh:mm:ss)
		String currentTime = DateFormat.format("hh:mm:ss aa", new Date()).toString();
		clockTime.setText(currentTime);
	}
	
	/**
	 * Executes update (session) timer thread on UI
	 */
	private Runnable runUpdateTimer = new Runnable() {
		// run() executes the thread
		public void run() {
			runOnUiThread(updateTimerOnUi);
		}
	};
	
	/**
	 * UI handler starts the update timer
	 */
	private Runnable updateTimerOnUi = new Runnable() {
		// run() executes the thread
		public void run() {			        
	        updateTimer();
		}
	};
	
	/**
	 * 
	 */
	private void updateTimer() {
		// get milliseconds
		int millisecs = getDuration();
		
		// THE countdown timer
		sessionTimer = new CountDownTimer(millisecs, 1000) {
			
			private String minutes;
			private String seconds;
			
			public void onTick(long millisUntilFinished) {
				
				minutes = String.valueOf( ((millisUntilFinished / (1000 * 60)) % 60) );
				seconds = String.valueOf( (millisUntilFinished / 1000) % 60 );
				
				// if the timer has ended
				if((Integer.parseInt(minutes) == 00) &&
				   (Integer.parseInt(seconds) == 00)) {
					
					// go to the user's previous sessions
					Intent intent = new Intent(NewSession.this, PrevSessions.class);
					startActivity(intent);
				}
				else {
					if(Integer.parseInt(minutes) < 10)
						minutes = "0" + minutes;
					
					if(Integer.parseInt(seconds) < 10)
						seconds = "0" + seconds;
				}
						
			    timer.setText(minutes + ":" + seconds);
		    }

		    public void onFinish() {
		        timer.setText("00:00");
		    }
		};
		
		sessionTimer.start();		
	}
	
}