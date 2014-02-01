package com.visus.main;

import java.util.*;
import java.util.Map.Entry;

import com.visus.R;
import com.visus.database.SessionRecordsHandler;
import com.visus.ui.settings.fragments.GeneralFragment;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivities extends Activity {
	
	private int activeUserId;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.layout_settings_activities);
		
		Bundle bundle = getIntent().getExtras();
		activeUserId = bundle.getInt("ActiveUserId");
		
		Log.e("Visus", "SettingsActivity: " + activeUserId);
		
		SessionRecordsHandler srHandler = new SessionRecordsHandler(this);
		HashMap<String, Double> records = new HashMap<String, Double>();
		
		try {
			srHandler.open();
			records = srHandler.getRecordsDesc(activeUserId);
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQL Error " + e);
		}
		finally {
			srHandler.close();
		}
		
		Iterator<Entry<String, Double>> it = records.entrySet().iterator();
		
		// output the key and value
		while(it.hasNext()) {
			Map.Entry<String, Double> entry = (Entry<String, Double>) it.next();			
			
			Log.e("Visus", entry.getKey() + ": " + entry.getValue() );
		}
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
				Intent upIntent = new Intent(this, GeneralFragment.class);
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
	
}
