package com.visus.ui.sessions.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.DateTime;

import com.visus.database.SessionHandler;
import com.visus.entities.Week;
import com.visus.entities.sessions.Session;
import com.visus.ui.MainMenuAdapter;
import com.visus.ui.MainMenuListView;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Displays any sessions made this week
 * @author Jonathan Perry
 */
public class FragmentThisWeek extends Fragment {
	
	private int userId;
		
	public FragmentThisWeek() {
		super();
	}
	
	public FragmentThisWeek(int userId) {
		this.userId = userId;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.visus.R.layout.fragment_sessions_this_week, container, false);
		SessionHandler dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
		ArrayList<Session> sessions = new ArrayList<Session>();
		ArrayList<HashMap<String, String>> sessionsThisWeek = new ArrayList<HashMap<String, String>>();
		MainMenuAdapter adapter;
		
		ListView lvWeek = (ListView) rootView.findViewById(com.visus.R.id.listview_sessions_this_week);
		
		/*
		 * bind and display our content from the database!
		 */
		try {
			dbSession.open();
		}
		catch(SQLiteException e) {
			Log.e("Visus", "SQLite Exception Error", e);
		}
		finally {
			// return session results based on this week (between Saturday and Friday)		
			sessions = dbSession.getSessionsThisWeek(userId);
			dbSession.close();
		}
		
		if(sessions.isEmpty()) {
			HashMap<String, String> map = new HashMap<String, String>();
			String msg = "None Created";
			map.put(MainMenuListView.SESSION, msg);
			sessionsThisWeek.add(map);
		}
		else {
			// retrieve sessions
			int id = 1;
			
			for(Session session : sessions) {
				String durationSeconds = null;
				
				if(session.getDurationSeconds() < 10) {
					durationSeconds = "0" + String.valueOf(session.getDurationSeconds() );
				}
				else {
					durationSeconds = String.valueOf(session.getDurationSeconds() );
				}
				
				
				String timeMinutes = null;
				
				if(session.getTimeMinutes() < 10) {
					timeMinutes = "0" + String.valueOf(session.getTimeMinutes() );
				}
				else {
					timeMinutes = String.valueOf(session.getTimeMinutes() );
				}
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				map.put(MainMenuListView.SESSION_NO, session.getDurationMinutes() + ":" + 
                        							 durationSeconds );				
				
				map.put(MainMenuListView.SESSION, session.getDay() + " (" +
								                  session.getTimeHour() + ":" +
								                  timeMinutes + " " +
								                  session.getDayPeriod() + "), " +
								                  session.getType()
						);

				sessionsThisWeek.add(map);
				id++;
			}
		}
		
		adapter = new MainMenuAdapter(getActivity(), sessionsThisWeek);
		
		lvWeek.setAdapter(adapter);
				
		return rootView;
	}
	
	public void onDestroyView() {
		super.onDestroyView();
	}

}
