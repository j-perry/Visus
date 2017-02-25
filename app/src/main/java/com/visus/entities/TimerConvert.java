package com.visus.entities;

import java.math.BigDecimal;

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
	
	public float minutesAccumulatedToHoursAccumulated(int minutes) {
		float result = 0.0f;
		float hours = 0.0f;
		
		result = (float) minutes / 100;
		
		while(result > 0.6) {
			hours += 1.0;
			result = (result - 0.6f);
			BigDecimal dc = new BigDecimal(result).setScale(2, BigDecimal.ROUND_HALF_UP);
			result = dc.floatValue();
		}
		
		result = hours + result; // hours + any remaining product in minutes
		
		return result;
	}
}
