package com.visus.ui.sessions.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import com.visus.database.SessionHandler;
import com.visus.entities.sessions.Session;
import com.visus.ui.ListViewAdapter;
import com.visus.ui.ListViewValues;

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
	
	private int activeUserId;
	
	public FragmentToday() {
		super();
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// get user id
		Bundle bundle = this.getArguments();
		activeUserId = bundle.getInt("ActiveUserId");
		
		SessionHandler dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
		ArrayList<Session> sessions = new ArrayList<Session>();
		ArrayList<HashMap<String, Object>> sessionsToday = new ArrayList<HashMap<String, Object>>();
		ListViewAdapter.Sessions adapter;
		
		View rootView = inflater.inflate(com.visus.R.layout.fragment_sessions_today, container, false);

		ListView lvToday = (ListView) rootView.findViewById(com.visus.R.id.listview_sessions_today);
		
		/*
		 * bind and display our content from the database!
		 */
		try {
			dbSession.open();
		} catch(SQLiteException e) {
			Log.e("Visus", "SQLite Exception Error", e);
		} finally {
			sessions = dbSession.getSessionsToday(activeUserId);
			dbSession.close();
		}
		
		if(sessions.isEmpty()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String msg = "None created";
			
			map.put(ListViewValues.Session.NO, "#");
			map.put(ListViewValues.Session.HEADER, msg);
			
			sessionsToday.add(map);
		} else {
			// retrieve sessions
			String durationSeconds = null;
			String timeMinutes = null;
			
			for(Session session : sessions) {
				
				if(session.getDurationSeconds() < 10) {
					durationSeconds = "0" + String.valueOf(session.getDurationSeconds() );
				} else {
					durationSeconds = String.valueOf(session.getDurationSeconds() );
				}
							
				if(session.getTimeMinutes() < 10) {
					timeMinutes = "0" + String.valueOf(session.getTimeMinutes() );
				} else {
					timeMinutes = String.valueOf(session.getTimeMinutes() );
				}
				
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				
				map.put(ListViewValues.Session.NO, session.getDurationMinutes() + ":" + 
				                                   durationSeconds );
				map.put(ListViewValues.Session.HEADER, session.getTimeHour() + ":" +
						  						       timeMinutes +
						  						       session.getDayPeriod() + " " +
						  						       session.getType()
					   );
				
				sessionsToday.add(map);
			}
		}
			
		adapter = new ListViewAdapter.Sessions(getActivity(), sessionsToday);
		
		lvToday.setAdapter(adapter);
				
		return rootView;
	}
	
	public void onDestroyView() {
		super.onDestroyView();
	}

}
