package com.visus.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.visus.R;
import com.visus.database.*;
import com.visus.entities.*;

import android.os.*;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.widget.*;

/**
 * Creates a new session
 * @author Jonathan Perry
 * @version 1.0
 */
@SuppressLint("SimpleDateFormat")
public class NewSession extends Activity {
			
	/*
	 * Entities
	 */
	private Session session;
	
	/*
	 * Controllers
	 */
	private SessionHandler sessionHandler;
	private UserHandler userHandler;
	
	// stores the active user's id
	private int activeUserId;
	
	private Handler timeHandler, 
					timerHandler;
	
	private boolean terminateCount = false;
	private int timerCount = 0;
		
	// session type - i.e., Email, Gaming, News, etc.
	private String type;

	// used to store and manipulate the session duration
	private int durationMilliseconds;			
	private int durationMinutes;
	private int durationSeconds;
	
	private int minutesRemaining;
	private int secondsRemaining;
	
	// UI components
	private TextView clockTime, timer;
	private Button startTimerBtn, stopTimerBtn;
	
	private CountDownTimer sessionTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session);
				
		initUIComponents();
												
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
		timer = (TextView) findViewById(R.id.timer);
		startTimerBtn = (Button) findViewById(R.id.timer_btn);
		stopTimerBtn = (Button) findViewById(R.id.timer_stop_btn);
	}

	/**
	 * 
	 */
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
		timerHandler = new Handler();	
		
		// date formats
		String strFormatDay = "EEEE";
		String strFormatMonth = "MMMM";
		String strFormatYear = "yyyy";
		
		// used to store user input for setting session duration 
		int iMins = 0;
		int iSecs = 0;
				
		// hide the session duration setter layout 
		LinearLayout sessionDuration = (LinearLayout) findViewById(R.id.set_session_duration);
		sessionDuration.setVisibility(View.GONE);
				
		// display the timer
		TextView timer = (TextView) findViewById(R.id.timer);
		timer.setVisibility(View.VISIBLE);
		
		// find EditText fields for value retrieval
		EditText etMins = (EditText) findViewById(R.id.timer_set_minutes);
		EditText etSecs = (EditText) findViewById(R.id.timer_set_seconds);
		
		// get the session type
		EditText sessionType = (EditText) findViewById(R.id.session_type);
		this.type = sessionType.getText().toString();
		Log.e("Visus", this.type);
		
		// hide the session view
		sessionType.setVisibility(View.GONE);
				
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
			} 
			else {
				iSecs = Integer.parseInt(etSecs.getText().toString() );
			}
		}
			
		
		// convert inputted session duration into milliseconds
		setDuration(iMins, iSecs);
		
		// assign session duration to global variable
		this.durationMinutes = iMins;
		this.durationSeconds = iSecs;
		
		// get the session date
		String day = new SimpleDateFormat(strFormatDay).format(new Date() );
		String month = new SimpleDateFormat(strFormatMonth).format(new Date() );
		String year = new SimpleDateFormat(strFormatYear).format(new Date() );
		
		Log.e("Visus", day + " " + month + " " + year);
		
		// remove callback to timer handler
		timerHandler.removeCallbacks(runUpdateTimer);				
		timerHandler.postDelayed(runUpdateTimer, 0);
		
		// initialise the session date
		session.setDate(day,	// day - e.g., Thursday
		                month,	// month - e.g., April
		                year);	// year - e.g., 2013
		
		// calendar date
		Log.e("Visus", "Set date: " + session.getDate());
		
		// get the session time
		String hour = new SimpleDateFormat("hh").format(new Date() );
		String minutes = new SimpleDateFormat("mm").format(new Date() );
		String seconds =  new SimpleDateFormat("ss").format(new Date() );
		
		// validation for hour
		if(Integer.parseInt(hour) < 10)
			hour = "0" + hour;
		
		// .. minutes
		if(Integer.parseInt(minutes) < 10)
			minutes = "0" + minutes;
		
		// .. seconds
		if(Integer.parseInt(seconds) < 10) 
			seconds = "0" + seconds;		
		
		
		// initialise the session time
		session.setTime(Integer.parseInt(hour),		// hour
						Integer.parseInt(minutes), 	// minutes
						Integer.parseInt(seconds));	// seconds
		
		// clock time
		Log.e("Visus", "Set time: " + session.getTime());		
	}
	
	/**
	 * Pauses the session timer
	 * @param view
	 */
	public void onPause(View view) {
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
		
//		TODO		
		int remainingMins = getTimeRemainingMinutes();
		int remainingSecs = getTimeRemainingSeconds();
		
		if((remainingMins == 0) && (remainingSecs == 0)) {
			Log.e("Visus", "Empty time remaining fields");
		}
		else {
			Log.e("Visus", "Time left: " + remainingMins + ":" + remainingSecs);
		}
		
		int sessionMins = durationMinutes - remainingMins;
		int sessionSecs;
		
		// TODO
		// 30 secs is less than 50 secs
		if(durationSeconds < remainingSecs) {
			// store whatever's left over in seconds
			int tempSecs = durationSeconds - remainingSecs;
			remainingSecs = 0;
			
			// reduce no. of minutes
			remainingMins -= -1;
			sessionSecs = (60 - tempSecs);			
		}
		else {
			sessionSecs = durationSeconds - remainingSecs;
		}
				
		session.setUserId(activeUserId);
		session.setDuration(sessionMins, sessionSecs); // TODO validation test
		session.setType(type);
		
		// output contents of the session to be written to the db
		Log.e("Visus", "Session date: " + session.getDate());
		Log.e("Visus", "Session duration: " + session.getDuration());
		Log.e("Visus", "Session time: " + session.getTime());
		Log.e("Visus", "Session type: " + session.getType()); // i.e., email
		
		// write session to db
		sessionHandler.add(session);
		
		Intent intent = new Intent(NewSession.this, Sessions.class);
		startActivity(intent);
	}	
	
	/**
	 * Sets the session duration and converts inputed 
	 * minutes and seconds into milliseconds
	 */
	private void setDuration(int minutes, int seconds) {
		TimerConvert convert = new TimerConvert();
		
		this.durationMilliseconds = convert.minutesAndSecondsToMilliseconds(minutes, seconds);
	}
	
	/**
	 * Gets the session duration
	 * @return
	 */
	private int getDuration() {
		return durationMilliseconds;
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
			
	private void setTimeRemainingMinutes(int minutes) {
		this.minutesRemaining = minutes;
	}
	
	private int getTimeRemainingMinutes() {
		return minutesRemaining;
	}
	
	private void setTimeRemainingSeconds(int seconds) {
		this.secondsRemaining = seconds;
	}
	
	private int getTimeRemainingSeconds() {
		return secondsRemaining;
	}
	
	/**
	 * Updates the timer TextView display from the timer handler
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
					// TODO
					Intent intent = new Intent(NewSession.this, Sessions.class);
					startActivity(intent);
				}
				else {
					if(Integer.parseInt(minutes) < 10)
						minutes = "0" + minutes;
					
					if(Integer.parseInt(seconds) < 10)
						seconds = "0" + seconds;
				}										
				// display the updated timer
			    timer.setText(minutes + ":" + seconds);			    
			    
				// update in case the user stops the clock
				setTimeRemainingMinutes( Integer.parseInt(minutes) );
				setTimeRemainingSeconds( Integer.parseInt(seconds) );
		    }

		    public void onFinish() {
		    	// when the timer has finished...
		        timer.setText("00:00");
		    }
		};
		
		// starts the timer
		sessionTimer.start();		
	}
		
}