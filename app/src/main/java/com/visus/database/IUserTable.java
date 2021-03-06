package com.visus.database;

/***************************************
 * 				Users table
 */
public interface IUserTable {

	public static final String TABLE_NAME  	  	  = "Users";
	
	public final static String KEY_ID       	  = "Id"; 	// All DB tables will share this property!
	
	public static final String KEY_ACTIVE   	  = "Active";
	
	public static final String KEY_TARGET_DAY 	  = "TargetDay";
	public static final String KEY_TARGET_MONTH   = "TargetMonth";
	
	public static final String KEY_DURATION_TODAY = "DurationToday";
	public static final String KEY_DURATION_MONTH = "DurationMonth";
		
	public static final int ACTIVE_USER 		  = 1;
	public static final int NON_ACTIVE_USER 	  = 0;
	
	
	
}
