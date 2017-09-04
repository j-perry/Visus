package com.visus.main;

// core apis

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.visus.R;
import com.visus.database.SessionHandler;
import com.visus.database.SessionRecordsHandler;
import com.visus.database.UserHandler;
import com.visus.entities.TimerConvert;
import com.visus.entities.User;
import com.visus.entities.sessions.Session;
import com.visus.logging.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

// android apis
// core program packages

/**
 * Creates a new session
 *
 * @author Jonathan Perry
 * @version 1.0
 */
@SuppressLint("SimpleDateFormat")
public class NewSession extends Activity {

    private Session session;
    private SessionHandler dbHandler;
    private UserHandler dbUser;

    // stores the active user's id
    private int activeUserId;

    private Handler timeHandler,
            timerHandler;

    private boolean terminateCount = false;
    private int timerCount = 0;

    private String type;

    // used to store and manipulate the session duration
    private int durationMilliseconds;
    private int durationMinutes;
    @SuppressWarnings("unused")
    private int durationSeconds;

    private int minutesRemaining;
    private int secondsRemaining;

    private static final String strFormatDayCalNo = "dd";
    private static final String strFormatDay = "EEEE";
    private static final String strFormatMonth = "MMMM";
    private static final String strFormatYear = "yyyy";

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        initUIComponents();
        createNewSession();
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
        switch (item.getItemId()) {
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
     * <p>
     * Starts a new session
     * </p>
     *
     * @param view
     */
    public void onStart(View view) {
        timerHandler = new Handler();

        startTimerBtn = (Button) findViewById(R.id.timer_btn);
        stopTimerBtn = (Button) findViewById(R.id.timer_stop_btn);

        // session duration setter layout
        TextView sessionDuration = (TextView) findViewById(R.id.timer_set_minutes);

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

    public void createNewSession() {
        sessionType = (TextView) findViewById(R.id.session_type);
        ArrayList<String> types = new ArrayList<String>();

        hideStopBtn();

        session = new Session();
        dbHandler = new SessionHandler(this);
        dbUser = new UserHandler(this);

        // get the active user id
        Bundle bundle = getIntent().getExtras();

        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.alert_dialog_new_session);

        final AutoCompleteTextView sessionTypes = (AutoCompleteTextView) dialog.findViewById(R.id.dialog_new_session_auto_complete_type);

        // if userId is not null
        if (bundle != null) {
            // get the user id (int)
            activeUserId = bundle.getInt("ActiveUserId");
            Logger.log("New Session", "UserID: " + activeUserId);
        }
//        else {
//            // find the active user
//            User user = dbUser.getActiveUser();
//            activeUserId = user.getUserId();
//        }

        try {
            dbHandler.open();
        } catch (SQLiteException e) {
            Logger.log("SQL Error", e);
        } finally {
            types = dbHandler.getAllSessionTypes(activeUserId);
            dbHandler.close();
        }

        if (types.size() == 0) {
            Logger.log("Types", "NULL");
        } else {
            for (String type : types) {
                Logger.log("Type", type);
            }

            // 	Alert Dialog
            ArrayAdapter<String> adapterTypes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types);
            sessionTypes.setAdapter(adapterTypes);
        }

        Button cancel = (Button) dialog.findViewById(R.id.alert_dialog_new_session_btn_cancel);
        Button ok = (Button) dialog.findViewById(R.id.alert_dialog_new_session_btn_ok);

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                type = sessionTypes.getText().toString().trim();

                if (!type.isEmpty()) {
                    sessionType.setText("#" + type);
                    sessionType.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } else {
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
                type = "None";
                sessionType.setText("#" + type);
                dialog.dismiss();
            }
        });

        dialog.show();

        timerHandler = new Handler();
        timerHandler.removeCallbacks(runUpdateTimer);
    }

    public void hideStopBtn() {
        // hide the stop button
        stopTimerBtn = (Button) findViewById(R.id.timer_stop_btn);
        stopTimerBtn.setVisibility(View.GONE);
    }

    /**
     * Initialises a new session
     *
     * @param iMins minutes set
     * @param iSecs seconds set
     */
    private void initSession(int iMins, int iSecs) {
        // convert inputted session duration into milliseconds
        setDuration(iMins, iSecs);

        // assign session duration to global variable
        this.durationMinutes = iMins;
        this.durationSeconds = iSecs;

        // get the session date
        int dayNo = Integer.parseInt(new SimpleDateFormat(strFormatDayCalNo).format(new Date()));
        String day = new SimpleDateFormat(strFormatDay).format(new Date());
        String month = new SimpleDateFormat(strFormatMonth).format(new Date());
        int year = Integer.parseInt(new SimpleDateFormat(strFormatYear).format(new Date()));

        Logger.log("Session", day + " " + dayNo + " " + month + ", " + year);

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
        String date = sdf.format(new Date());
        session.setDate(date);

        Logger.log("Date Set", session.getDate());

        // get the session time
        int hour = Integer.parseInt(new SimpleDateFormat("hh").format(new Date()));
        int minutes = Integer.parseInt(new SimpleDateFormat("mm").format(new Date()));
        String dayPeriod = new SimpleDateFormat("a").format(new Date());

        // set the time (GMT) - hours
        session.setTimeHour(hour);

        // minutes...
        session.setTimeMinutes(minutes);

        // day period
        session.setDayPeriod(dayPeriod);

        Logger.log("Set Time", session.getTimeHour() + ":" + session.getTimeMinutes());
        Logger.log("Set Day Period", session.getDayPeriod());
    }

    /**
     * Pauses the session timer
     *
     * @param view
     */
    public void onPause(View view) {
        timerHandler.removeCallbacks(runUpdateTimer);
    }

    /**
     * Ends the session
     *
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
        SessionRecordsHandler srHandler = new SessionRecordsHandler(context);

        int sessionMins = 0;
        int sessionSecs = 0;

        int remainingMins = getTimeRemainingMinutes();
        int remainingSecs = getTimeRemainingSeconds();

        if ((remainingMins == 0) && (remainingSecs == 0)) {
            Logger.log("Time Remaining", "Empty");
        } else {
            Logger.log("Time Left", remainingMins + ":" + remainingSecs);
        }

        sessionMins = (durationMinutes - remainingMins) - 1;
        sessionSecs = (60 - remainingSecs);

        int secsLeft = 0;

        if (sessionMins == 24 && sessionSecs == 59) {
            sessionSecs += 1; // bug fix
            session.setDurationMinutes(sessionMins);
        } else {
            session.setDurationMinutes(sessionMins);
        }

        if (sessionSecs >= 60) {
            sessionMins += 1;
            secsLeft = (sessionSecs - 60);
            sessionSecs = secsLeft;

            session.setDurationMinutes(sessionMins);
            session.setDurationSeconds(sessionSecs);
        } else {
            session.setDurationSeconds(sessionSecs);
        }

        session.setUserId(activeUserId);
        session.setType(type);

        Logger.log("SessionMins", sessionMins);
        Logger.log("SessionSecs", sessionSecs);

        // write session to database
        try {
            dbHandler.open();
            dbHandler.add(session);

            // write log to records table
            srHandler.open();
            HashMap<String, Double> existingRecord = srHandler.getActivityRecordByName(type);

            // get the existing duration for the activity type
            double recordDuration = 0.0;

            Session.SessionFormatter sf = new Session.SessionFormatter();

            // if existing records do exist...
            if (!existingRecord.isEmpty()) {
                Logger.log("Existing Records", "Not Empty");

                double exRecordDuration = 0.0;
                Iterator<Entry<String, Double>> it = existingRecord.entrySet().iterator();

                Entry<String, Double> entry = (Entry<String, Double>) it.next();
                exRecordDuration = entry.getValue();

                // return both products correctly time formatted
                recordDuration = sf.formatSessionDurations(exRecordDuration, sessionMins, sessionSecs);

                // write to the local database
                srHandler.updateActivityRecordByName(type, recordDuration, activeUserId);
            } else {
                recordDuration = sf.formatSessionDuration(sessionMins, sessionSecs);
                srHandler.insertActivityRecord(type, recordDuration, activeUserId);
            }
        } catch (SQLiteException e) {
            Logger.log("SQL Error", e);
        } finally {
            dbHandler.close();
            srHandler.close();
        }

        // go to previously made sessions
        Intent intent = new Intent(NewSession.this, Sessions.class);
        intent.putExtra("ActiveUserId", activeUserId);
        intent.putExtra("DisplayNotification", true);
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
     * Gets the session duration in milliseconds
     *
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

            if (terminateCount == true) {
                timerCount++;
            }

            if (timerCount > 25) {
                timeHandler.removeCallbacksAndMessages(runUpdateTime);
            } else {
                // delay for 1 second
                timeHandler.postDelayed(this, 0);
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
     * Sets the number of minutes remaining
     *
     * @param minutes
     */
    private void setTimeRemainingMinutes(int minutes) {
        Logger.log("setTimeRemainingMinutes", minutes);
        this.minutesRemaining = minutes;
    }

    /**
     * Gets the number of minutes remaining
     */
    private int getTimeRemainingMinutes() {
        return minutesRemaining;
    }

    /**
     * Sets the number of seconds remaining
     */
    private void setTimeRemainingSeconds(int seconds) {
        Logger.log("setTimeRemainingSeconds", seconds);
        this.secondsRemaining = seconds;
    }

    /**
     * Gets the number of minutes remaining
     */
    private int getTimeRemainingSeconds() {
        return secondsRemaining;
    }

    /**
     * Updates the timer TextView display from the timer handler
     */
    private void updateTimer() {
        int millisecs = getDuration();

        sessionTimer = new CountDownTimer(millisecs, 1000) {
            private String minutes;
            private String seconds;

            public void onTick(long millisUntilFinished) {
                // NB: minutes has a limit of 60. If minutes is set to 60, it will just go null.
                // A potential bug that could be solved in the future.
                minutes = String.valueOf(((millisUntilFinished / (1000 * 60)) % 60)); // TODO change % 60 to 61??
                seconds = String.valueOf((millisUntilFinished / 1000) % 60);

                Logger.log("Minutes ", minutes);
                Logger.log("Seconds ", seconds);

                // if the timer has ended
                if (minutes.equals("0") && seconds.equals("0")) {
                    Logger.log("Timer ", "Ended");

                    // go to the user's previous sessions
                    Intent intent = new Intent(NewSession.this, Sessions.class);
                    startActivity(intent);
                } else {
                    if (Integer.parseInt(minutes) < 10)
                        minutes = "0" + minutes;

                    if (Integer.parseInt(seconds) < 10)
                        seconds = "0" + seconds;
                }
                // display the updated timer
                timer.setText(minutes + ":" + seconds);

                // update in case the user stops the clock
                setTimeRemainingMinutes(Integer.parseInt(minutes));
                setTimeRemainingSeconds(Integer.parseInt(seconds));
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
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
        notBuilder = new NotificationCompat.Builder(this);
        notBuilder.setSmallIcon(R.drawable.ic_launcher_4);
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
        notManager.notify(0, notBuilder.build());

        finishSession();
    }
}