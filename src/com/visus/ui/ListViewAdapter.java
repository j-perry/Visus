package com.visus.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter {
	
	public static class Sessions extends BaseAdapter {
		
		@SuppressWarnings("unused")
		private Activity activity;
		private ArrayList<HashMap<String, Object>> data;
		private static LayoutInflater inflater = null;
		
		public Sessions(Activity activity, ArrayList<HashMap<String, Object>> data) {
			this.activity = activity;
			this.data = data;
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
				v = inflater.inflate(com.visus.R.layout.layout_main_menu_item, null);
			}
				
			TextView entry = (TextView) v.findViewById(com.visus.R.id.entry);
			TextView entryNo = (TextView) v.findViewById(com.visus.R.id.entry_no);
					
			HashMap<String, Object> item = new HashMap<String, Object>();
			item = data.get(position);
			
			// new
			entryNo.setText((String) item.get(ListViewValues.Session.NO));
			// bind our data using the required key values
			entry.setText((String) item.get(ListViewValues.Session.HEADER));
						
			return v;
		}
		
	}
	
	public static class Tasks extends BaseAdapter {
		
		@SuppressWarnings("unused")
		private Activity activity;
		private ArrayList<HashMap<String, Object>> data;
		private static LayoutInflater inflater = null;
		
		public Tasks(Activity activity, ArrayList<HashMap<String, Object>> data) {
			this.activity = activity;
			this.data = data;
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
				v = inflater.inflate(com.visus.R.layout.layout_main_menu_item, null);
			}
				
			TextView entry = (TextView) v.findViewById(com.visus.R.id.entry);
			TextView entryNo = (TextView) v.findViewById(com.visus.R.id.entry_no);
					
			HashMap<String, Object> item = new HashMap<String, Object>();
			item = data.get(position);
			
			// new
			entryNo.setText((String) item.get(ListViewValues.Task.NO));
			// bind our data using the required key values
			entry.setText((String) item.get(ListViewValues.Task.HEADER));
						
			return v;
		}
		
	}
	
}
