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
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
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
	private NotificationCompat.Builder notBuilder;
	
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
		
		startTimerBtn = (Button) findViewById(R.id.timer_btn);
		stopTimerBtn = (Button) findViewById(R.id.timer_stop_btn);
		
		// session duration setter layout 
		LinearLayout sessionDuration = (LinearLayout) findViewById(R.id.set_session_duration);
		
		// find EditText fields for value retrieval
		EditText etMins = (EditText) findViewById(R.id.timer_set_minutes);
		EditText etSecs = (EditText) findViewById(R.id.timer_set_seconds);
		
		TextView timer = (TextView) findViewById(R.id.timer);
		
		// used to store user input for setting session duration 
		int iMins = 0;
		int iSecs = 0;
		
		int MINS_LIMIT = 60;
		int SECS_LIMIT = 59;
		
		
		/******************************************************************
		 * 			If both minutes and seconds fields are empty
		 */
		if( (etMins.getText().toString().isEmpty() ) && 
			(etSecs.getText().toString().isEmpty() )) {
			String toastMsg = "Enter at least one value in either of the number fields";
			
			// inform the user they are both empty ...
			Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 250);
			toast.show();
			
			// ... and delay the start time
			timerHandler.removeCallbacks(runUpdateTimer);			
		}
		/**********************************************************************
		 * 		If minutes field is empty and seconds field is NOT empty
		 */
		else if( (etMins.getText().toString().isEmpty()) &&
				 (!etSecs.getText().toString().isEmpty() )) {
			
			// if seconds set are above 59
			if(Integer.parseInt(etSecs.getText().toString() ) > SECS_LIMIT) {
				// inform the user it must be between 0 - 59
				String toastMsg = "Invalid range. Please enter seconds between 0 - 59";
				
				etSecs.setText("");
				
				// display a message
				Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 250);
				toast.show();
			}
			else {
				// append two noughts
				iMins = 00;
				iSecs = Integer.parseInt( etSecs.getText().toString() );
				
				// if session type is empty
				if(sessionTypes.getText().toString().length() == 0) {
					this.type = "Uncategorised";
					// output session type
					Log.e("Visus", "Type: " + "Uncategorised");
				}
				else {
					this.type = sessionTypes.getText().toString();
					// output session type
					Log.e("Visus", "Type: " + this.type);
				}
					
				// hide the session view
				sessionTypes.setVisibility(View.GONE);
				
				// hide the start button
				startTimerBtn.setVisibility(View.GONE);
				
				// display the stop button
				stopTimerBtn.setVisibility(View.VISIBLE);
								
				// hide the session duration setter layout
				sessionDuration.setVisibility(View.GONE);
						
				// display the timer
				timer.setVisibility(View.VISIBLE);
				
				// initialise a new session
				initSession(iMins, iSecs);
			}		
		}
		// TODO
		/******************************************************************************
		 * 		If the minutes field is NOT empty and the seconds field is empty
		 */
		else if( (!etMins.getText().toString().isEmpty() ) && (etSecs.getText().toString().isEmpty()) ) {
			
			// if minutes set are above 60
			if(Integer.parseInt(etMins.getText().toString() ) > MINS_LIMIT) {
				// inform the user it must be between 0 - 59
				String toastMsg = "Invalid range. Please enter minutes between 0 - 60";
				
				etMins.setText("");
				
				// display a message
				Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 250);
				toast.show();
			}
			// else, if minutes equals 60
			else if( Integer.parseInt(etMins.getText().toString() ) == MINS_LIMIT) {				
				iMins = Integer.parseInt( etMins.getText().toString() );
				
				// append two noughts
				iSecs = 0;
				
				// if session type is empty
				if(sessionTypes.getText().toString().length() == 0) {
					this.type = "Uncategorised";
					// output session type
					Log.e("Visus", "Type: " + "Uncategorised");
				}
				else {
					this.type = sessionTypes.getText().toString();
					// output session type
					Log.e("Visus", "Type: " + this.type);
				}
								
				// hide the session view
				sessionTypes.setVisibility(View.GONE);
							
				// hide the start button
				startTimerBtn.setVisibility(View.GONE);
				
				// display the stop button
				stopTimerBtn.setVisibility(View.VISIBLE);
								
				// hide the session duration setter layout
				sessionDuration.setVisibility(View.GONE);
						
				// display the timer
				timer.setVisibility(View.VISIBLE);
				
				// initialise a new session
				initSession(iMins, iSecs);
			}
			// else, if minutes is less than 60
			else if ( Integer.parseInt(etMins.getText().toString() ) < MINS_LIMIT ) {
//				iMins = 59;
				iSecs = 59;
				
				iMins = Integer.parseInt( etMins.getText().toString() );
			
				// append two noughts
				iSecs = 0;
				
				Log.e("Visus", "iMins = " + iMins);
				Log.e("Visus", "iSecs = " + iSecs);
				
				
				// if session type is empty
				if(sessionTypes.getText().toString().length() == 0) {
					this.type = "Uncategorised";
					// output session type
					Log.e("Visus", "Type: " + "Uncategorised");
				}
				else {
					this.type = sessionTypes.getText().toString();
					// output session type
					Log.e("Visus", "Type: " + this.type);
				}
								
				// hide the session view
				sessionTypes.setVisibility(View.GONE);
							
				// hide the start button
				startTimerBtn.setVisibility(View.GONE);
				
				// display the stop button
				stopTimerBtn.setVisibility(View.VISIBLE);
								
				// hide the session duration setter layout
				sessionDuration.setVisibility(View.GONE);
						
				// display the timer
				timer.setVisibility(View.VISIBLE);
				
				// initialise a new session
				initSession(iMins, iSecs);
			}		
		}
		// if both fields are NOT empty...
		else if( (!etMins.getText().toString().isEmpty() ) &&
				 (!etSecs.getText().toString().isEmpty()) ) {
			
			// if greater than 60 minutes
			if( (Integer.parseInt(etMins.getText().toString() ) > MINS_LIMIT) ) {
				// inform the user it must be between 0 - 59
				String toastMsg = "Invalid range. Please enter minutes between 0 - 59";
				
				// display a message
				Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 250);
				toast.show();
			}
			// else if minutes equals 60 minutes
			else if( (Integer.parseInt(etMins.getText().toString() ) == MINS_LIMIT) ) {
				
				// and if seconds is greater than zero
				if( !(Integer.parseInt(etSecs.getText().toString() ) == 0 )) {
					// inform the user it must be up to 60:00 minutes (1 hour)
					String toastMsg = "Invalid range. Please enter a duration up to 60:00 minutes";
					
					etMins.setText("");
					etSecs.setText("");
					
					Log.e("Visus", "etMins now: " + etMins.getText().toString() );
					Log.e("Visus", "etSecs now: " + etSecs.getText().toString() );
					
					// display a message
					Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 250);
					toast.show();
				}
				else {
					// if set to 60:00
					if( (Integer.parseInt(etMins.getText().toString() ) == MINS_LIMIT ) && (Integer.parseInt(etSecs.getText().toString() ) == 00 ) ) {
						// set to 59:59 on start
						iMins = 59;
						iSecs = 59;
					}
					else {
						iMins = 59;
						iSecs = Integer.parseInt( etSecs.getText().toString() );
					}				
					
					// if session type is empty
					if(sessionTypes.getText().toString().length() == 0) {
						this.type = "Uncategorised";
						// output session type
						Log.e("Visus", "Type: " + "Uncategorised");
					}
					else {
						this.type = sessionTypes.getText().toString();
						// output session type
						Log.e("Visus", "Type: " + this.type);
					}
					
					
					// hide the session view
					sessionTypes.setVisibility(View.GONE);
									
					// hide the start button
					startTimerBtn.setVisibility(View.GONE);
					
					// display the stop button
					stopTimerBtn.setVisibility(View.VISIBLE);
									
					// hide the session duration setter layout
					sessionDuration.setVisibility(View.GONE);
							
					// display the timer
					timer.setVisibility(View.VISIBLE);
					
					// initialise a new session
					initSession(iMins, iSecs);
				}
			}
			// else, if minutes equals 60 minutes AND seconds is empty
			else if( Integer.parseInt(etMins.getText().toString() ) == MINS_LIMIT && etSecs.getText().toString().isEmpty() ) {
				
				iMins = Integer.parseInt(etMins.getText().toString());
				iSecs = 00;
				
				// if session type is empty
				if(sessionTypes.getText().toString().length() == 0) {
					this.type = "Uncategorised";
					// output session type
					Log.e("Visus", "Type: " + "Uncategorised");
				}
				else {
					this.type = sessionTypes.getText().toString();
					// output session type
					Log.e("Visus", "Type: " + this.type);
				}
				
				
				// hide the session view
				sessionTypes.setVisibility(View.GONE);
								
				// hide the start button
				startTimerBtn.setVisibility(View.GONE);
				
				// display the stop button
				stopTimerBtn.setVisibility(View.VISIBLE);
								
				// hide the session duration setter layout
				sessionDuration.setVisibility(View.GONE);
						
				// display the timer
				timer.setVisibility(View.VISIBLE);
				
				// initialise a new session
				initSession(iMins, iSecs);
			}
			// if minutes is less than 60 and less than 59 seconds
			else if( (Integer.parseInt(etMins.getText().toString() ) < MINS_LIMIT) && (Integer.parseInt(etSecs.getText().toString() ) < SECS_LIMIT) ) {
				
				// get minutes
				iMins = Integer.parseInt(etMins.getText().toString() );
				
				// get seconds
				iSecs = Integer.parseInt(etSecs.getText().toString() );
				
				// if session type is empty
				if(sessionTypes.getText().toString().length() == 0) {
					this.type = "Uncategorised";
					// output session type
					Log.e("Visus", "Type: " + "Uncategorised");
				}
				else {
					this.type = sessionTypes.getText().toString();
					// output session type
					Log.e("Visus", "Type: " + this.type);
				}
				
				
				// hide the session view
				sessionTypes.setVisibility(View.GONE);
								
				// hide the start button
				startTimerBtn.setVisibility(View.GONE);
				
				// display the stop button
				stopTimerBtn.setVisibility(View.VISIBLE);
								
				// hide the session duration setter layout
				sessionDuration.setVisibility(View.GONE);
						
				// display the timer
				timer.setVisibility(View.VISIBLE);
				
				// initialise a new session
				initSession(iMins, iSecs);				
			}
			// else, if minutes is less than 60 AND seconds is not less than or equal to 59
			else if( (Integer.parseInt(etMins.getText().toString() ) < MINS_LIMIT) && !(Integer.parseInt(etSecs.getText().toString() ) < 60) ) {
				Log.e("Visus", "Timer (mins): " + etMins.getText().toString() );
				Log.e("Visus", "Timer (secs): " + etSecs.getText().toString() );
				
				etMins.setText("");
				etSecs.setText("");
				
				// inform the user it must be between 0 - 59
				String toastMsg = "Invalid range. Please enter seconds between 0 - 59";
				
				// display a message
				Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 250);
				toast.show();
			}
			else {
				
				// get minutes
				iMins = Integer.parseInt(etMins.getText().toString() );
				
				// get seconds
				iSecs = Integer.parseInt(etSecs.getText().toString() );
				
				// if session type is empty
				if(sessionTypes.getText().toString().length() == 0) {
					this.type = "Uncategorised";
					// output session type
					Log.e("Visus", "Type: " + "Uncategorised");
				}
				else {
					this.type = sessionTypes.getText().toString();
					// output session type
					Log.e("Visus", "Type: " + this.type);
				}
				
				
				// hide the session view
				sessionTypes.setVisibility(View.GONE);
								
				// hide the start button
				startTimerBtn.setVisibility(View.GONE);
				
				// display the stop button
				stopTimerBtn.setVisibility(View.VISIBLE);
								
				// hide the session duration setter layout
				sessionDuration.setVisibility(View.GONE);
						
				// display the timer
				timer.setVisibility(View.VISIBLE);
				
				// initialise a new session
				initSession(iMins, iSecs);				
			}
		}
		
//		/****************************************************************
//		 * 		If both fields are NOT empty, do some formatting...
//		 */
//		else {
//			/****************************************************************
//			 * 		...before doing that...
//			 */
//			// if minutes set are above 60 AND seconds set are above 59
//			if( (Integer.parseInt(etMins.getText().toString() ) > MINS_LIMIT) && 
//				(Integer.parseInt(etSecs.getText().toString()) > SECS_LIMIT) ) {
//				
//				// inform the user it must be between 0 - 59
//				String toastMsg = "Invalid range. Please enter minutes between 0 - 60";
//							
//				// display a message
//				Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG);
//				toast.setGravity(Gravity.CENTER, 0, 250);
//				toast.show();
//			}
//			else {
//				/****************************************************
//				 * 		Otherwise, get on with some formatting!
//				 */
//				
//				// if the length of the input is 1 - i.e., min = 1
//				if(etMins.getText().toString().length() == 1) {
//					// and the number is less than 10 minutes
//					if(Integer.parseInt( etMins.getText().toString()) < 10) {
//						// append a nought to minutes
//						iMins = 0;
//						iMins += Integer.parseInt(etMins.getText().toString() );
//					}
//				}
//				else {
//					iMins = Integer.parseInt(etMins.getText().toString() );
//				}
//				
//				// repeat (for seconds)...
//				if(etSecs.getText().toString().length() == 1) {
//					// and the number is less than 10 seconds		
//					if(Integer.parseInt( etSecs.getText().toString()) < 10) {
//						iSecs = 0;
//						iSecs += Integer.parseInt(etSecs.getText().toString() );
//					}
//				} 
//				else {
//					iSecs = Integer.parseInt(etSecs.getText().toString() );
//				}
//				
//				
//				/***************************************
//				 * 		If session type is empty
//				 */ 
//				if(sessionTypes.getText().toString().length() == 0) {
//					this.type = "Uncategorised";
//					
//					// output session type
//					Log.e("Visus", "Type: " + "Uncategorised");
//				}
//				else {
//					this.type = sessionTypes.getText().toString();
//					
//					// output session type
//					Log.e("Visus", "Type: " + this.type);
//				}
//						
//				// hide the session view
//				sessionTypes.setVisibility(View.GONE);
//								
//				// hide the start button
//				startTimerBtn.setVisibility(View.GONE);
//				
//				// display the stop button
//				stopTimerBtn.setVisibility(View.VISIBLE);
//								
//				// hide the session duration setter layout
//				sessionDuration.setVisibility(View.GONE);
//						
//				// display the timer
//				timer.setVisibility(View.VISIBLE);
//				
//				// initialise a new session
//				initSession(iMins, iSecs);			
//			}			
//		}				
	}
	
	/**
	 * Initialises a new session
	 * @param iMins minutes set
	 * @param iSecs seconds set
	 */
	private void initSession(int iMins, int iSecs) {
		Log.e("Visus", "initSession()");
		
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

		// day period
		session.setDayPeriod(dayPeriod);
		
		Log.e("Visus", "Set time: " + session.getTimeHour() 
						            + ":" 
				                    + session.getTimeMinutes());
						
		Log.e("Visus", "Set day period: " + session.getDayPeriod());
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
		finishSession();
	}	
	
	/**
	 * Finishes the session and saves it
	 */
	private void finishSession() {
		sessionTimer.cancel();
		timerHandler.removeCallbacks(runUpdateTimer);
		
		int sessionMins = 0;
		int sessionSecs = 0;
		
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
		Log.e("Visus", "setTimeRemainingMinutes() " + minutes);
		this.minutesRemaining = minutes;
	}
	
	private int getTimeRemainingMinutes() {
		return minutesRemaining;
	}
	
	private void setTimeRemainingSeconds(int seconds) {
		Log.e("Visus", "setTimeRemainingSeconds() " + seconds);
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
				
				/*******************************************************************************************
				 * 		NB: minutes has a limit of 60. If minutes is set to 60, it will just go null.
				 * 		A potential bug that could be solved in the future.
				 */
				minutes = String.valueOf( ((millisUntilFinished / (1000 * 60)) % 60) ); // TODO change % 60 to 61??
				seconds = String.valueOf( (millisUntilFinished / 1000) % 60 );
				
				Log.e("Visus", "Minutes: " + minutes);
				Log.e("Visus", "Seconds: " + seconds);
				
				// if the timer has ended
				if(minutes.equals("0") &&
				   seconds.equals("0") ) {
					
					Log.e("Visus", "Timer Has Ended");
					
					// go to the user's previous sessions
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

			/**
			 * When the timer has finished...
			 */
		    public void onFinish() {
		        timer.setText("00:00");
		        displayNotification();
		    }
		};
		
		// starts the timer
		sessionTimer.start();
	}	
	
	/**
	 * Display's an Android notification in the notification bar to inform
	 * the user their session has finished, should they be doing something else
	 */
	private void displayNotification() {
		Log.e("Visus", "displayNotification()");
		
		notBuilder = new NotificationCompat.Builder(this);
		notBuilder.setSmallIcon(com.visus.R.drawable.ic_launcher_4);
		notBuilder.setContentTitle("Session Ended");
		notBuilder.setContentText("You're session has finished");
		
		Intent intent = new Intent(NewSession.this, Sessions.class);
		intent.putExtra("ActiveUserId", activeUserId);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addNextIntent(intent);
		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		
		notBuilder.setContentIntent(pendingIntent);
		NotificationManager notManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		notBuilder.setAutoCancel(true);
		
		// display it!
		notManager.notify(0, notBuilder.build() );
		
		finishSession();
	}
}