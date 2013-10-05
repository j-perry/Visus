package com.visus.ui;

import java.util.*;

import com.visus.R;

import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SessionsAdapter extends BaseAdapter {
	
	private ArrayList<HashMap<String, String>> data;
	private Activity activity;
	private static LayoutInflater inflater = null;
	
	public SessionsAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
		this.data = data;
		this.activity = activity;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if(convertView == null) {
			v = inflater.inflate(com.visus.R.layout.layout_sessions_items, null);
		}
			
		TextView sessionId = (TextView) v.findViewById(com.visus.R.id.session_id);
//		TextView date = (TextView) v.findViewById(com.visus.R.id.date);
//		TextView time = (TextView) v.findViewById(com.visus.R.id.time);
		TextView activity = (TextView) v.findViewById(com.visus.R.id.session_activity);
		
		HashMap<String, String> item = new HashMap<String, String>();
		item = data.get(position);
		
//		date.setText( item.get(SessionsListView.DATE));
//		time.setText( item.get(SessionsListView.TIME));
//		activity.setText( item.get(SessionsListView.ACTIVITY));

		sessionId.setText( item.get(SessionsListView.SESSION_ID) );
		activity.setText( item.get(SessionsListView.SESSION) );
		
		return v;
	}	
}
