package com.visus.settings.activities;

import java.text.DecimalFormat;

import android.util.Log;

public class ActivitiesManager {

	public ActivitiesManager() {
		super();
	}
		
	public Double add(Double [] activity) {
		Double product = 0.0;
		
		for(int i = 0; i < activity.length; i++) {
			product += activity[i];
		}
		Log.e("Visus", "Product: " + String.valueOf(product));
		
		DecimalFormat df = new DecimalFormat("#.#####");
		product = Double.valueOf(df.format(product) );
		
		return product;
	}	

	public Double [] sort(Double [] activities) {
		Double [] sortedActivities = new Double[activities.length];
		Double highestValue = 0.0;
		int big = 0;
		Double temp = 0.0;
		int first = 0;
		int length = activities.length;
		
		int i;
		int j = 0;
		

		Log.e("Visus", "activities contents: \n");
		
		for(i = 0; i < activities.length; i++) {
			Log.e("Visus", String.valueOf(activities[i] ));
		}
		
		
		for(i = length-1; i > 0; i--) {
			big = first;
			
			for(j = first + 1; j <= first + i; j++)
				if(activities[big] < activities[j])
					big = j;
			
			temp = activities[first + i];
			
			activities[first + i] = activities[big];
			activities[big] = temp;
			Log.e("Visus", "Highest value: " + String.valueOf(activities[big]) );
		}
		
		return activities;
	}
}