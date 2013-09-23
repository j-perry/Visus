package com.visus.main;

// core apis
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// android apis
import android.os.*;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.widget.*;

// core program packages
import com.visus.R;
import com.visus.database.*;
import com.visus.entities.*;
import com.visus.entities.sessions.Session;


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
	private SessionHandler dbHandler;
	private UserHandler dbUser;
	
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
		
	// date formats
	private static final String strFormatDayCalNo = "dd";
	private static final String strFormatDay = "EEE";
	private static final String strFormatMonth = "MMM";
	private static final String strFormatYear = "yyyy";
	
	// UI components
	private TextView clockTime, timer;
	private Button startTimerBtn, stopTimerBtn;
	
	private CountDownTimer sessionTimer;
	
	private AutoCompleteTextView sessionTypes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session);
		
		ArrayList<String> types = new ArrayList<String>();
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
				
		initUIComponents();
		
		// hide the stop button
		stopTimerBtn = (Button) findViewById(R.id.timer_stop_btn);
		stopTimerBtn.setVisibility(View.GONE);
		
												
		session = new Session();
		dbHandler = new SessionHandler(this);
		dbUser = new UserHandler(this);
		
		// get the active user id
		Bundle userId = getIntent().getExtras();
		
		// if userId is not null
		if(userId != null) {
			// get the user id (int)
			activeUserId = userId.getInt("ActiveUserId");
			Log.e("Visus", "----------------------------------------");
			Log.e("Visus", "New Session - User id is " + activeUserId);
		}
		else {
			// find the active user
			User user = dbUser.getActiveUser();
			activeUserId = user.getUserId();
		}
		
		try {
			dbHandler.open();			
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			sessionTypes = (AutoCompleteTextView) findViewById(com.visus.R.id.session_auto_complete_type);
			types = dbHandler.getAllSessionTypes(activeUserId);
			dbHandler.close();
		}	
		
		if(types.size() == 0) {
			Log.e("Visus", "NULL");
		}
		else {
			Log.e("Visus", "Session Types Available");
			
			for(String type : types) {
				Log.e("Visus", type);
			}
		}
				
		ArrayAdapter<String> adapterTypes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types); 
		sessionTypes.setAdapter(adapterTypes);
		
		
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
		this.timer = (TextView) findViewById(R.id.timer);
		this.startTimerBtn = (Button) findViewById(R.id.timer_btn);
		this.stopTimerBtn = (Button) findViewById(R.id.timer_stop_btn);
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
	 * Action bar events
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			// app logo
			case android.R.id.home:
				Intent upIntent = new Intent(this, MainActivity.class);
	            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
	                // This activity is not part of the application's task, so create a new task
	                // with a synthesized back stack.
	                TaskStackBuilder.from(this)
	                        // If there are ancestor activities, they should be added here.
	                        .addNextIntent(upIntent)
	                        .startActivities();
	                finish();
	            } else {
	                // This activity is part of the application's task, so simply
	                // navigate up to the hierarchical parent activity.
	                NavUtils.navigateUpTo(this, upIntent);
	            }
	            break;
	        default:
	        	break;
		}
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
		
		// used to store user input for setting session duration 
		int iMins = 0;
		int iSecs = 0;
		
		// hide the start button
		startTimerBtn = (Button) findViewById(R.id.timer_btn);
		startTimerBtn.setVisibility(View.GONE);
		
		// display the stop button
		stopTimerBtn = (Button) findViewById(R.id.timer_stop_btn);
		stopTimerBtn.setVisibility(View.VISIBLE);
		
				
		// hide the session duration setter layout 
		LinearLayout sessionDuration = (LinearLayout) findViewById(R.id.set_session_duration);
		sessionDuration.setVisibility(View.GONE);
				
		// display the timer
		TextView timer = (TextView) findViewById(R.id.timer);
		timer.setVisibility(View.VISIBLE);
		
		// find EditText fields for value retrieval
		EditText etMins = (EditText) findViewById(R.id.timer_set_minutes);
		EditText etSecs = (EditText) findViewById(R.id.timer_set_seconds);
			
		// if session type is empty
		if(sessionTypes.getText().toString().length() == 0) {
			this.type = "Uncategoried";
		}
		else {
			this.type = sessionTypes.getText().toString();
		}
		
		
		// output session type
		Log.e("Visus", "Type: " + this.type);
		
		// hide the session view
		sessionTypes.setVisibility(View.GONE);
				
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
		int dayNo = Integer.parseInt(new SimpleDateFormat(strFormatDayCalNo).format(new Date()) );
		String day = new SimpleDateFormat(strFormatDay).format(new Date() );
		String month = new SimpleDateFormat(strFormatMonth).format(new Date() );
		int year = Integer.parseInt(new SimpleDateFormat(strFormatYear).format(new Date()) );
		
		Log.e("Visus", day + " " + dayNo + " " + month + ", " + year);
		
		// remove callback to timer handler
		timerHandler.removeCallbacks(runUpdateTimer);
		timerHandler.postDelayed(runUpdateTimer, 0);
		
		// initialise the session date
		session.setDayNo(dayNo);
		session.setDay(day);
		session.setMonth(month);
		session.setYear(year);
		
		// set the date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date() );
		session.setDate(date);
		
		Log.e("Visus", "Date set: " + session.getDate());
		
		// get the session time
		int hour = Integer.parseInt(new SimpleDateFormat("hh").format(new Date() ));
		int minutes = Integer.parseInt(new SimpleDateFormat("mm").format(new Date() ));
		String dayPeriod = new SimpleDateFormat("a").format(new Date() );
		
		// set the time (GMT) - hours
		session.setTimeHour(hour);
		
		// minutes...		
		session.setTimeMinutes(minutes);
				
		
		// initialise the session time
//		session.setTime(hour,		// hour
//						minutes,    // minutes
//						dayPeriod);	// period of the day - AM/PM
		
		Log.e("Visus", "Set time: " + session.getTimeHour() + ":" + session.getTimeMinutes());
		
		session.setDayPeriod(dayPeriod);
		
		Log.e("Visus", "Set day period: " + session.getDayPeriod());	
		
		// clock time
//		Log.e("Visus", "Set time: " + session.getTime());		
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
		int sessionMins = 0;
		int sessionSecs = 0;
								
//		sessionDuration[0] = ((getDuration() / (1000 * 60)) % 60);	// minutes
//		sessionDuration[1] = ((getDuration() / 1000) % 60);			// seconds
		
//		TODO		
		int remainingMins = getTimeRemainingMinutes();
		int remainingSecs = getTimeRemainingSeconds();
				
		if((remainingMins == 0) && (remainingSecs == 0)) {
			Log.e("Visus", "Empty time remaining fields");
		}
		else {
			Log.e("Visus", "----------------------------");
			Log.e("Visus", "Time left: " + remainingMins + ":" + remainingSecs);
		}
		
		sessionMins = durationMinutes - remainingMins;
		
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
		session.setDurationMinutes(sessionMins);
		session.setDurationSeconds(sessionSecs);
		session.setType(type);
		
		Log.e("Visus", "SessionMins: " + sessionMins);
		Log.e("Visus", "SessionSecs: " + sessionSecs);
		
		
		/**
		 * write session to db
		 */		
		try {
			dbHandler.open();
			dbHandler.add(session);
			updateTargetDuration(sessionMins, sessionSecs, activeUserId);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbHandler.close();
		}
						
		// go to previously made sessions
		Intent intent = new Intent(NewSession.this, Sessions.class);
		intent.putExtra("ActiveUserId", activeUserId);
		startActivity(intent);
	}	
	
	/**
	 * 
	 * @param sessionSecs
	 * @param sessionMins
	 * @param userId
	 * @return
	 */
	private void updateTargetDuration(int sessionMins, int sessionSecs, int userId) {		
		/**
		 * compute targets and write equally to db
		 */
		
		// today (incl. newly created session)
		float existingDurationToday = 0.0f;
		float existingDurationMonth = 0.0f;
		float durationToday = 0.0f;
		float durationMonth = 0.0f;
		
		// get our values
		try {
			dbUser.open();
			existingDurationToday = dbUser.getDurationToday(activeUserId);
			
			Log.e("Visus", "existingDurationToday: " + existingDurationToday);
			
			// today
			if(existingDurationToday == 0.0f) {
				existingDurationToday = 0.0f;
			}
						
//			existingDurationMonth = dbUser.getDurationMonth(activeUserId);
			
			// month
//			if(existingDurationMonth == 0) {
//				existingDurationMonth = 0.0f;
//			}
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbUser.close();
		}
		
		// convert newly created session mins + secs to a float
		float tmpSessionSecs = (float) sessionSecs / 100.0f;
				
		Log.e("Visus", "tmpSessionSecs");
		Log.e("Visus", String.valueOf(tmpSessionSecs) );	// output 10 secs (0.1)
		
//		if(tmpSessionSecs > 0.6f) {
//			sessionMins += 1.0f;
//			tmpSessionSecs = (0.6f - tmpSessionSecs);
//		}
				
		float product = (float) sessionMins + tmpSessionSecs;
		Log.e("Visus", "product");
		Log.e("Visus", String.valueOf(product) );
				
		// compute existing and new products (today)
		durationToday = existingDurationToday + product;
		
		// calculate secs (decimal place)
		//    - 1.7 % 1 = 0.7
		//
		// if(secs is greater than 0.6) increment no. of minutes
		// subtract 0.7 (e.g.,) by 0.6
		// add to no of secs
		//
		// we want 1.7 mins to be
		// 2.1 mins
		
		/*************************************************************************************
		 * Format the time according to generic time conventions (i.e., 60 secs in 1 minute)
		 */
		
		// take a copy of session time (just created) - mm:ss
		float durationTodayCpy = durationToday;
		
		// find decimal product derived from mm:ss
		float durationTodaySecs = durationToday % 1;
		
		// calculate no. of minutes accumulated - i.e., 2.5 - 0.5 = 2 mins
		durationTodayCpy = durationTodayCpy - durationTodaySecs;
		
		// if over 60 seconds
		if(durationTodaySecs > 0.6f) {
			float incrMins = 1.0f;
			
			// copy number of seconds accumulated
			float durationTodaySecsCpy = durationTodaySecs;
			durationTodaySecs = 0.0f;
			
			// calculate no. of seconds left over 60 seconds (0.6)
			durationTodaySecs = (durationTodaySecsCpy - 0.6f);
			
			// initialise no. of mins + secs - i.e., 3.1, appending an additional minute for accumulated seconds over 60 seconds
			durationToday = durationTodayCpy + incrMins + durationTodaySecs; 
			
			Log.e("Visus", "Before BIGDECIMAL: " + durationToday);
			
			// format the duration to 2 decimal places - e.g., X.XX
			BigDecimal durationTodayReformatted = new BigDecimal(durationToday).setScale(2, BigDecimal.ROUND_HALF_UP);
			durationToday = durationTodayReformatted.floatValue();
		}
		else {
			Log.e("Visus", "Before BIGDECIMAL: " + durationToday);
			
			BigDecimal durationTodayReformatted = new BigDecimal(durationToday).setScale(2, BigDecimal.ROUND_HALF_UP);
			durationToday = durationTodayReformatted.floatValue();
		}
									
		Log.e("Visus", "durationToday");
		Log.e("Visus", String.valueOf(durationToday) );
			
				
		// compute existing and new products (this month)
//		durationMonth = existingDurationMonth + durationToday;
				
		// write to the database duration accumulated both today and this month
		try {
			dbUser.open();
			dbUser.setDurationToday(activeUserId, durationToday);	// duration accumulated today
//			dbUser.setDurationMonth(activeUserId, durationMonth);	// duration accumulated this month
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error", e);
		}
		finally {
			dbUser.close();
		}
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