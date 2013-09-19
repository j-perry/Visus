package com.visus.database;

import android.content.Context;

public class Target {
	
	private int userId;
	private SessionHandler dbSession;
	private UserHandler dbUser;
	
	public Target(Context context, int userId) {
		super();
		this.userId = userId;
		dbSession = new SessionHandler(context);
		dbUser = new UserHandler(context);
	}
	
	/**
	 * Checks whether the user has met their daily target
	 * @param userId
	 * @return
	 */
	public boolean checkTargetDay() {
		boolean targetDay = false;
		
		return targetDay;
	}
	
	/**
	 * Checks whether the user has met their monthly target
	 * @param userId
	 * @return
	 */
	public boolean checkTargetMonth() {
		boolean targetMonth = false;
		
		return targetMonth;
	}

}
