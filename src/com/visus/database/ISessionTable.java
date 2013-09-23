package com.visus.database;

public interface ISessionTable {

	public final static String KEY_ID       	 = "Id"; 	// All DB tables will share this property!
	
	public static final String TABLE_NAME    	 = "Sessions";
	
	// columns
	public static final String KEY_USER_ID       = "UserId";
	public static final String KEY_DAY_NO        = "DayNo";
	public static final String KEY_DATE          = "Date";
	public static final String KEY_DAY           = "Day";
	public static final String KEY_MONTH         = "Month";
	public static final String KEY_YEAR          = "Year";
	public static final String KEY_TIME_HOUR     = "Hour";
	public static final String KEY_TIME_MINS     = "Mins";
	public static final String KEY_TIMEZONE      = "TimeZone";
	public static final String KEY_DURATION_MINS = "DurationMinutes";
	public static final String KEY_DURATION_SECS = "DurationSeconds";	
	public static final String KEY_TYPE          = "Type";
	
}
