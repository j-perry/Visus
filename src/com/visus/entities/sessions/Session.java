package com.visus.entities.sessions;

public class Session {

	private StringBuilder duration;
	private StringBuilder time;
	private String date;
	
	// session date
	private int dayNo;
	private String day;
	private String month;	
	private int year;
	
	// session time
	private int hour;
	private int minutes;	
	private String dayPeriod;
	
	// session duration
	private int durationMinutes;
	private int durationSeconds;
	
	private int userId;
	private String type;
	
	// overview
	private int overviewHours;
	private int overviewSessions;
	private int overviewActivities;
	
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
		this.day = day;
	}
	
	public String getDay() {
		return day;
	}
	
	public void setMonth(String month) {
		// month - e.g., January (MMMM format)
		for(String m : months)
			if(m.equals(month))
				this.month = month;
	}
	
	public String getMonth() {
		return month;
	}
	
	/**
	 * Set the session date
	 * 
	 * Validates whether day, month and year are in the right format
	 * @param day
	 * @param month
	 * @param year
	 */
	public void setDate(String date) {
		this.date = date;
	}
	
	/**
	 * Return's the date
	 * @return date
	 */
	public String getDate() {
		return date;
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
	
	public void setOverviewHours(int overviewHours) {
		this.overviewHours = overviewHours;
	}
	
	public int getOverviewHours() {
		return overviewHours;
	}
	
	public void setOverviewNoSessions(int overviewSessions) {
		this.overviewSessions = overviewSessions;
	}
	
	public int getOverviewNoSessions() {
		return overviewSessions;
	}
	
	public void setOverviewNoActivities(int overviewActivities) {
		this.overviewActivities = overviewActivities;
	}
	
	public int getOverviewNoActivities() {
		return overviewActivities;
	}
	
	public static class SessionFormatter {
		
		public SessionFormatter() {
			super();
		}
		
		/**
		 * Formats the session duration just passed
		 * @return the session duration that's been formatted
		 */
		public double formatSessionDuration() {
			double duration = 0.0;
			
			return duration;
		}
		
		/**
		 * Updates the total duration for both past and present sessions
		 * @param past session duration
		 * @param present session duration that's just been passed
		 * @return the formatted duration of both products
		 */
		public double formatSessionDurations(double past, double present) {
			double duration = 0.0;
			
			return duration;
		}		
	}
}
