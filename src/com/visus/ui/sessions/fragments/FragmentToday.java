package com.visus.ui.sessions.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import com.visus.R;
import com.visus.database.SessionHandler;
import com.visus.entities.sessions.Session;
import com.visus.ui.MainMenuAdapter;
import com.visus.ui.MainMenuListView;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Displays any sessions made today
 * @author Jonathan Perry
 */
public class FragmentToday extends Fragment {
	
	private int userId;
	
	public FragmentToday() {
		super();
	}
	
	public FragmentToday(int userId) {
		this.userId = userId;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SessionHandler dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
		ArrayList<Session> sessions = new ArrayList<Session>();
		ArrayList<HashMap<String, String>> sessionsToday = new ArrayList<HashMap<String, String>>();
		MainMenuAdapter adapter;
		
		View rootView = inflater.inflate(com.visus.R.layout.fragment_sessions_today, container, false);

		ListView lvToday = (ListView) rootView.findViewById(com.visus.R.id.listview_sessions_today);
		
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
			sessions = dbSession.getSessionsToday(userId);
			dbSession.close();
		}
		
		if(sessions.isEmpty()) {
			HashMap<String, String> map = new HashMap<String, String>();
			String msg = "None Created";
			map.put(MainMenuListView.SESSION, msg);
			sessionsToday.add(map);
		}
		else {
			// retrieve sessions
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
				map.put(MainMenuListView.SESSION, session.getDay() + " " +
								    					  session.getDayNo() + " " +  
								    					  session.getMonth() + ", " +
								    					  session.getYear() + " - " +
										                  session.getTimeHour() + ":" +
										                  timeMinutes + " " +
										                  session.getDayPeriod() + " - " +
										                  session.getDurationMinutes() + ":" +
										                  durationSeconds + " - " +
										                  session.getType()
						       );
				
				sessionsToday.add(map);
			}
		}
			
		adapter = new MainMenuAdapter(getActivity(), sessionsToday);
		
		lvToday.setAdapter(adapter);
				
		return rootView;
	}
	
	public void onDestroyView() {
		super.onDestroyView();
	}

}
