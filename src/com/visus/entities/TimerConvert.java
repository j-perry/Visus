package com.visus.entities;

/**
 * Converts minutes to milliseconds, and vice-versa
 * @author Jonathan Perry
 *
 */
public class TimerConvert {

	private int milliseconds;
	
	public TimerConvert() {
		super();
	}
	
	public void minutesToMilliseconds(int minutes) {
		int result = ((minutes * 60) * 1000);
		this.milliseconds = result;
	}
	
	public void secondsToMilliseconds(int seconds) {
		int result = (seconds * 1000);
		this.milliseconds = result;
	}
	
	public int minutesAndSecondsToMilliseconds(int minutes, int seconds) {
		int result = 0;
		
		// convert minutes to milliseconds and ...
		result = ((minutes * 60) * 1000);
		// ... seconds to milliseconds
		result += (seconds * 1000);
		
		return result;
	}
	
	public int getMillisecondsFromMinutes() {
		return milliseconds;
	}
	
	public int getMillisecondsFromSeconds() {
		return milliseconds;
	}	
}
