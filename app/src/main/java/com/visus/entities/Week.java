package com.visus.entities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

public class Week {
	
	private String beginning;
	private String ending;
	
	private int dayNo;
	private int month;
	private int year;

	// constant/s
	final String DASH = "-";

	public Week() {

	}
	
	/* Delete */
	public void setDayNo(int dayNo) {
		this.dayNo = dayNo;
	}
	
	public int getDayNo() {
		return dayNo;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	
	public int getMonth() {
		return month;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public int getYear() {
		return year;
	}
	/* End of delete */
	
	
	/**
	 * Finds the beginning date of the present week
	 * @return The beginning of the week formatted as yyyy-mm-dd
	 */
	@SuppressLint("SimpleDateFormat")
	public String beginning() {
		String day = null;
		int dayNoResult = 0;
		int monthResult = 0;
		int yearResult = 0;
		@SuppressWarnings("unused")
		String monthResultStr = null;
		boolean firstDayFound = false;
				
		
		// convert dayNo to day (String) rep
		// first get the current month
		Log.e("Visus", "---------------------");
		Log.e("Visus", "findBeginningOfWeek()");
		Log.e("Visus", "---------------------");
		
		// get the current date
		Calendar cal = Calendar.getInstance();
		// set the time
		cal.setTime(new Date());
				
		do {
			// return today's day no. in the present month
			DateFormat dfDayStr = new SimpleDateFormat("EEE");		// Sun, e.g.
			Date dt = cal.getTime();								// get the time
			
			// then get the day based on the month		
			day = dfDayStr.format(dt).toString();					// get day String (EEE)
			
			Log.e("Visus", "Day: " + day);
			
			// if it it not Sat-urday - the beginning of the week
			if(!day.contains("Sat")) {
				// go back a day (or month and/or year)
				cal.add(Calendar.DATE, -1);		//
			}
			else {
				DateFormat dfMonth = new SimpleDateFormat("MMM");
				dt = cal.getTime();
				
				// assign results
				dayNoResult = cal.get(Calendar.DAY_OF_MONTH);	// get day of month - i.e., 31st
				monthResult = (cal.get(Calendar.MONTH) + 1);
				monthResultStr = dfMonth.format(dt).toString();	// get month - i.e., Sep
				yearResult = cal.get(Calendar.YEAR);			// get year - i.e., 2013
				
				this.dayNo = dayNoResult;
				this.month = monthResult;
				this.year = yearResult;
				
				// output results to log
				Log.e("Visus", "------------------");
				Log.e("Visus", "Sat-day found!");
				Log.e("Visus", "dayNoResult: " + dayNoResult);
				Log.e("Visus", "monthResult: " + monthResult);
				Log.e("Visus", "yearResult: " + yearResult);
				
				Log.e("Visus", "Month No. (Beginning): " + (cal.get(Calendar.MONTH) + 1) );
				
				Log.e("Visus", "Beginning of the week: " + dayNoResult + "-" + monthResult + "-" + yearResult);
				
				// terminate
				firstDayFound = true;
			}			
		} while(firstDayFound != true);
		
		Log.e("Visus", "Loop terminated");
		
		
		/*********************** 
		 * 	initialise results
		 */
		
		// year
		beginning = String.valueOf(year) + DASH;
		
		// month
		if(month < 10) {
			beginning += "0" + String.valueOf(month) + DASH;
		}
		else {
			beginning += String.valueOf(month) + DASH;
		}
		
		// day
		if(dayNo < 10) {
			beginning += "0" + String.valueOf(dayNo);
		}
		else {
			beginning += String.valueOf(dayNo);
		}
		
		Log.e("Visus", "Week beginning: " + beginning);
		
		// return the beginning date
		return beginning;
	}
	
	/**
	 * Finds the ending date of the present week
	 * @return The end of the week formatted as yyyy-mm-dd
	 */
	@SuppressLint("SimpleDateFormat")
	public String ending() {
		String day = null;
		int dayNoResult = 0;
		int monthResult = 0;
		int yearResult = 0;
		@SuppressWarnings("unused")
		String monthResultStr = null;
		boolean lastDayFound = false;
		
		
		// convert dayNo to day (String) rep
		// first get the current month		
		Log.e("Visus", "---------------------");
		Log.e("Visus", "findEndOfWeek()");
		Log.e("Visus", "---------------------");
		
		// get the current date
		Calendar cal = Calendar.getInstance();
		// set the time
		cal.setTime(new Date() );
				
		do {				
			// return today's day no. in the present month
			DateFormat dfDayStr = new SimpleDateFormat("EEE");		// Sun, e.g.
			Date dt = cal.getTime();								// get the time
			
			// then get the day based on the month		
			day = dfDayStr.format(dt).toString();					// get day String (EEE)
			
			Log.e("Visus", "Day: " + day);
			
			// if it it not Sat-urday - the beginning of the week
			if(!day.contains("Fri")) {
				// go back a day (or month and/or year)
				cal.add(Calendar.DATE, +1);		//
			}
			else {
				DateFormat dfMonth = new SimpleDateFormat("MMM");
				dt = cal.getTime();
				
				// assign results
				dayNoResult = cal.get(Calendar.DAY_OF_MONTH);	// get day of month - i.e., 31st
				monthResult = (cal.get(Calendar.MONTH) + 1);
				monthResultStr = dfMonth.format(dt).toString();	// get month - i.e., Sep
				yearResult = cal.get(Calendar.YEAR);			// get year - i.e., 2013
				
				this.dayNo = dayNoResult;
				this.month = monthResult;
				this.year = yearResult;
				
				// output results to log
				Log.e("Visus", "------------------");
				Log.e("Visus", "Fri-day found!");
				Log.e("Visus", "dayNoResult: " + dayNoResult);
				Log.e("Visus", "monthResult: " + monthResult);
				Log.e("Visus", "yearResult: " + yearResult);
				
				Log.e("Visus", "Month No. (End): " + (cal.get(Calendar.MONTH) + 1 ));
				
				Log.e("Visus", "End of the week: " + dayNoResult + "-" + monthResult + "-" + yearResult);
				
				// terminate
				lastDayFound = true;
			}
		} while(lastDayFound != true);
		
		Log.e("Visus", "Loop terminated");
		
		
		/***********************
		 * 	initialise results
		 */
		
		// year
		ending = String.valueOf(year) + DASH;
		
		// month
		if(month < 10) {
			ending += "0" + String.valueOf(month) + DASH;
		}
		else {
			ending += String.valueOf(month) + DASH;
		}
				
		// day
		if(dayNo < 10) {
			ending += "0" + String.valueOf(dayNo);
		}
		else {
			ending += String.valueOf(dayNo);
		}
		
		Log.e("Visus", "Week ending: " + ending);
		
		// return the ending date
		return ending;
	}
	
	/**
	 * Returns the date of the present week, both beginning and ending dates
	 * @return the date
	 */
	public Week date() {
		Week date = new Week();
		date.beginning = beginning();
		date.ending = ending();
		
		return date;
	}
}
