package com.visus.entities.sessions;

import java.util.Date;

import android.util.Log;

public class Session {

	private StringBuilder duration;
	private StringBuilder time;
	private StringBuilder date;
	private int dayNo;
	private String day;
	private String month;	
	private int year;
	private String type;
	
	private int hour;
	private int minutes;	
	private String dayPeriod;
	
	private int durationMinutes;
	private int durationSeconds;
	
	private int userId;
		
	private final static String [] days = {
		"Sat", 
	    "Sun", 
	    "Mon", 
		"Tue", 
		"Wed", 
		"Thu", 
		"Fri" 
	};
	
	private final static String [] months = {
		"Jan",
		"Feb",
		"Mar",
		"Apr",
		"May",
		"Jun",
		"Jul",
		"Aug",
		"Sept",
		"Oct",
		"Nov",
		"Dec"
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
	
	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	
	public int getDurationMinutes() {
		return durationMinutes;
	}
	
	public void setDurationSeconds(int durationSeconds) {
		this.durationSeconds = durationSeconds;
	}
	
	public int getDurationSeconds() {
		return durationSeconds;
	}
	
	/**
	 * Set the time of the session (i.e., 13:34pm) format as HH:mm:ss
	 * @param hour
	 * @param minutes
	 * @param seconds
	 */
	public void setTime(String hour, String minutes, String dayPeriod) {
		this.time = new StringBuilder(hour + ":");
		this.time.append(minutes + " ");
		this.time.append(dayPeriod);
	}
	
	/**
	 * Returns the session time (AM/PM)
	 * @return session time (AM/PM)
	 */
	public String getTime() {
		return time.toString();
	}
	
	public void setTimeHour(int hour) {
		this.hour = hour;
	}

	public int getTimeHour() {
		return hour;
	}
	
	public int getTimeMinutes() {
		return minutes;
	}
	
	public void setTimeMinutes(int minutes) {
		this.minutes = minutes;
	}
	
	public String getDayPeriod() {
		return dayPeriod;
	}
	
	public void setDayPeriod(String dayPeriod) {
		this.dayPeriod = dayPeriod;
	}
	
	public void setDayNo(int dayNo) {
		this.dayNo = dayNo;
	}
	
	public int getDayNo() {
		return dayNo;
	}
	
	public void setDay(String day) {
		// day - e.g., Mon (EEE format)
		for(String d : days)
			if(d.equals(day))
				this.day = day;
	}
	
	public String getDay() {
		return day;
	}
	
	public void setMonth(String month) {
		// month - e.g., January (MMM format)
		for(String m : months)
			if(m.equals(month))
				this.month = month;
	}
	
	public String getMonth() {
		return month;
	}
	
	/**
	 * Set the session date format as EEE MMM yyyy
	 * 
	 * Validates whether day, month and year are in the right format
	 * @param day
	 * @param month
	 * @param year
	 */
	public void setDate(String dayNo, String day, String month, String year) {
		this.date = new StringBuilder(dayNo);
		this.date.append(" ");
		
		// day - e.g., Monday (EEEE format)
		for(String d : days)
			if(d.equals(day))
				this.date.append(day);
		
		// spacing - "[dayNo]_[day]_"
		date.append(" ");
		
		// month - e.g., January (MMMM format)
		for(String m : months)
			if(m.equals(month))
				this.date.append(month);	
		
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
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public int getYear() {
		return year;
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
