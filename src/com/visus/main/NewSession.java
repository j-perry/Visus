package com.visus.main;

// core apis
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
// android apis
import android.os.*;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.view.View;
import android.view.View.OnClickListener;

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
	@SuppressWarnings("unused")
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
		
	private Context context = this;
	
	private TextView sessionType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session);
		
		ArrayList<String> types = new ArrayList<String>();
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
				
		initUIComponents();
		
        sessionType = (TextView) findViewById(R.id.session_type);
		
		// hide the stop button
		stopTimerBtn = (Button) findViewById(R.id.timer_stop_btn);
		stopTimerBtn.setVisibility(View.GONE);
		
												
		session = new Session();
		dbHandler = new SessionHandler(this);
		dbUser = new UserHandler(this);
		
		// get the active user id
		Bundle userId = getIntent().getExtras();
		final Dialog dialog = new Dialog(context);
		
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.alert_dialog_new_session);
		
		final AutoCompleteTextView sessionTypes = (AutoCompleteTextView) dialog.findViewById(com.visus.R.id.dialog_new_session_auto_complete_type);
		
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
						
			/**
			 * 	Alert Dialog
			 */
			ArrayAdapter<String> adapterTypes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types); 
			
			sessionTypes.setAdapter(adapterTypes); // TODO
		}
		
		// buttons
		Button cancel = (Button) dialog.findViewById(R.id.alert_dialog_new_session_btn_cancel);
		Button ok = (Button) dialog.findViewById(R.id.alert_dialog_new_session_btn_ok);
		
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				type = sessionTypes.getText().toString().trim();
				
				if(!type.isEmpty()) {
					sessionType.setText("#" + type);
					sessionType.setVisibility(View.VISIBLE);
		            dialog.dismiss();
				}
				else {
					String tstMsg = "Enter an activity type";
					Toast tstInput = Toast.makeText(getApplicationContext(), tstMsg, Toast.LENGTH_LONG);					
					tstInput.setGravity(Gravity.CENTER, 0, 0);
					tstInput.show();
				}
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				type = "#None";
	            sessionType.setText(type);
	            dialog.dismiss();
			}
		});
		
		dialog.show();
		
		
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
	@SuppressWarnings("deprecation")
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
		
		TextView timer = (TextView) findViewById(R.id.timer);
		
		// used to store user input for setting session duration 
		int iMins = 25;
		int iSecs = 0;
				
		// hide the start button
		startTimerBtn.setVisibility(View.GONE);
		
		// display the stop button
		stopTimerBtn.setVisibility(View.VISIBLE);
						
		// hide the session duration setter layout
		sessionDuration.setVisibility(View.GONE);
				
		// display the timer
		timer.setVisibility(View.VISIBLE);
		
		sessionType.setVisibility(View.GONE);
		
		// initialise a new session
		initSession(iMins, iSecs);	
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
		this.durationSeconds = iSecs; // + 1; // TODO
				
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
		
		sessionMins = (durationMinutes - remainingMins) - 1;
		
		sessionSecs = (60 - remainingSecs);
						
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
	@SuppressWarnings("unused")
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
				timeHandler.postDelayed(this, 0); // TODO
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
		        
		        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		        		        
		        try {
		        	MediaPlayer mediaPlayer = new MediaPlayer();
		        	mediaPlayer.setDataSource(getApplicationContext(), notification);
		        	mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
		            mediaPlayer.prepare();
		            
		            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
		            	
		            	@Override
		            	public void onCompletion(MediaPlayer mp) {
		            		mp.release();
		            		finishSession();
		            	}
		            			        	
		            });
		            			        
		            mediaPlayer.start();	  
		        }
		        catch (IllegalArgumentException e) {
		        	e.printStackTrace();
		        } 
		        catch (SecurityException e) {
		        	e.printStackTrace();
		        } 
		        catch (IllegalStateException e) {
		        	e.printStackTrace();
		        } 
		        catch (IOException e) {
		        	e.printStackTrace();
		        }       
		    }
		};
		
		// starts the timer
		sessionTimer.start();
	}	
	
	/**
	 * Display's an Android notification in the notification bar to inform
	 * the user their session has finished, should they be doing something else
	 */
	@SuppressWarnings("unused")
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