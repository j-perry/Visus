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
 * Displays any sessions made this month
 * @author Jonathan Perry
 */
public class FragmentThisMonth extends Fragment {
	
	private int activeUserId;
	
	public FragmentThisMonth() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// get user id
		Bundle bundle = this.getArguments();
		activeUserId = bundle.getInt("ActiveUserId");
		
		View rootView = inflater.inflate(com.visus.R.layout.fragment_sessions_this_month, container, false);
		SessionHandler dbSession = new SessionHandler(getActivity() ); // getActivity() should do the trick!
		ArrayList<Session> sessions = new ArrayList<Session>();
		ArrayList<HashMap<String, String>> sessionsThisMonth = new ArrayList<HashMap<String, String>>();
		ListViewAdapter adapter;
		
		ListView lvMonth = (ListView) rootView.findViewById(com.visus.R.id.listview_sessions_this_month);
		
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
			sessions = dbSession.getSessionsThisMonth(activeUserId);
			dbSession.close();
		}
		
		if(sessions.isEmpty()) {
			HashMap<String, String> map = new HashMap<String, String>();
			String msg = "None created";
			map.put(ListViewValues.SESSION_NO, "#");
			map.put(ListViewValues.SESSION, msg);
			sessionsThisMonth.add(map);
		}
		else {
			// retrieve sessions
			@SuppressWarnings("unused")
			int id = 1;
			
			for(Session session : sessions) {
				String durationSeconds = null;
				
				if(session.getDurationSeconds() < 10) {
					durationSeconds = "0" + String.valueOf(session.getDurationSeconds() );
				}
				else {
					durationSeconds = String.valueOf(session.getDurationSeconds() );
				}
				

				@SuppressWarnings("unused")
				String timeMinutes = null;
				
				if(session.getTimeMinutes() < 10) {
					timeMinutes = "0" + String.valueOf(session.getTimeMinutes() );
				}
				else {
					timeMinutes = String.valueOf(session.getTimeMinutes() );
				}
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				map.put(ListViewValues.SESSION_NO, session.getDurationMinutes() + ":" + 
						 							 durationSeconds );
				
				map.put(ListViewValues.SESSION, session.getDayNo() + " " +  
												  session.getMonth() + " " +
								                  session.getType()
						);

				sessionsThisMonth.add(map);
				id++;
			}
		}
				
		adapter = new ListViewAdapter(getActivity(), sessionsThisMonth);
		
		lvMonth.setAdapter(adapter);
				
		return rootView;
	}
	
	public void onDestoryView() {
		super.onDestroyView();
	}
	
}
