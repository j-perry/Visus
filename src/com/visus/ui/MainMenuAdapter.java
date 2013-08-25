package com.visus.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainMenuAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	
	public MainMenuAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
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
		
		int noHeaders = 0;
		
		if(convertView == null)
			v = inflater.inflate(com.visus.R.layout.layout_main_menu_item, null);
		
		// 
		TextView header = (TextView) v.findViewById(com.visus.R.id.main_header);
		TextView entry = (TextView) v.findViewById(com.visus.R.id.entry);
		
		HashMap<String, String> item = new HashMap<String, String>();
		item = data.get(position);
		
		entry.setText(item.get(MainMenuListView.SESSION));
					
		return v;
	}
	
}
