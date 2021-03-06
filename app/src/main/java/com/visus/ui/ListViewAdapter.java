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

public class ListViewAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	
	public ListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
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
				
		if(convertView == null)
			v = inflater.inflate(com.visus.R.layout.layout_main_menu_item, null);
		
		TextView entry = (TextView) v.findViewById(com.visus.R.id.entry);
		TextView entryNo = (TextView) v.findViewById(com.visus.R.id.entry_no);
		
		
		HashMap<String, String> item = new HashMap<String, String>();
		item = data.get(position);
		
		// new
		entryNo.setText(item.get(ListViewValues.SESSION_NO));
		// bind our data using the required key values
		entry.setText(item.get(ListViewValues.SESSION));
					
		return v;
	}
	
}
