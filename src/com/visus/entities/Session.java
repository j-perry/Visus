package com.visus.entities;

import java.util.Date;

import android.util.Log;

public class Session {

	private StringBuilder duration;
	private StringBuilder time;
	private StringBuilder date;
	private String type;
	
	private int userId;
		
	private final static String [] days = {
		"Saturday", 
	    "Sunday", 
	    "Monday", 
		"Tuesday", 
		"Wednesday", 
		"Thursday", 
		"Friday" 
	};
	
	private final static String [] months = {
		"January",
		"February",
		"March",
		"April",
		"May",
		"June",
		"July",
		"August",
		"September",
		"October",
		"November",
		"December"
	};
	
	
	public Session() {
		super();
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	/**
	 * Set the session duration in minutes and seconds (mm:ss)
	 * @param minutes
	 * @param seconds
	 */
	public void setDuration(int minutes, int seconds) {
		this.duration = new StringBuilder(String.valueOf(minutes));
		this.duration.append(":");
		this.duration.append(String.valueOf(seconds));
	}
	
	/**
	 * Returns the session duration
	 * @return session duration - minutes:seconds
	 */
	public String getDuration() {
		return duration.toString();
	}
	
	/**
	 * Set the time of the session (i.e., 13:34pm) format as HH:mm:ss
	 * @param hour
	 * @param minutes
	 * @param seconds
	 */
	public void setTime(int hour, int minutes, int seconds) {
		this.time = new StringBuilder(String.valueOf(hour) + ":");
		this.time.append(String.valueOf(minutes) + ":");
		this.time.append(String.valueOf(seconds));		
	}
	
	/**
	 * Returns the session time (AM/PM)
	 * @return session time (AM/PM)
	 */
	public String getTime() {
		return time.toString();
	}
	
	/**
	 * Set the session date format as EEEE MMMM yyyy
	 * 
	 * Validates whether day, month and year are in the right format
	 * @param day
	 * @param month
	 * @param year
	 */
	public void setDate(String day, String month, String year) {		
		// day - e.g., Monday (EEEE format)
		for(String d : days)
			if(d.equals(day))
				this.date = new StringBuilder(day);
		
		// spacing - "[day]_"
		date.append(" ");
		
		// month - e.g., January (MMMM format)
		for(String m : months)
			if(m.equals(month))
				this.date.append(month);
		
		// spacing - "[day]_[month]_"
		date.append(" ");
		
		// year - e.g., 2013 (yyyy format)
		if(year.length() == 4)
			this.date.append(year);	
	}
	
	/**
	 * Return's the date
	 * @return day + " " + month + " " + year
	 */
	public String getDate() {
		return date.toString();
	}
	
	/**
	 * Set's the session type - e.g., Email, News, Gaming...
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the session type
	 * @return
	 */
	public String getType() {
		return type;
	}
}
