package com.visus.database;

public class UserTableHandler implements IDatabaseTableCreator {

	public static final String TABLE_USERS = "Users";
	
	public static final String KEY_ID       = "Id";
	public static final String KEY_ACTIVE   = "Active";
	public static final String KEY_NAME     = "Name";
	public static final String KEY_AGE      = "Age";
	public static final String KEY_GENDER   = "Gender";
	
	public static final int ACTIVE_USER = 1;
	public static final int NON_ACTIVE_USER = 0;
	
	private final String qryCreateTableUsers = "CREATE TABLE " + TABLE_USERS +
			                                          " ( " +
			                                          		KEY_ID + " INTEGER PRIMARY KEY, " +
			                                          		KEY_ACTIVE + " INTEGER, " +
			                                          		KEY_NAME + " TEXT, " +
			                                          		KEY_AGE + " INTEGER, " +
			                                          		KEY_GENDER + " TEXT " +
			                                          ")";
	
	public UserTableHandler() {
		super();
	}
	
	@Override
	public String getTableName() {
		return TABLE_USERS;
	}

	@Override
	public String getCreateTableQuery() {
		return qryCreateTableUsers;
	}

	@Override
	public String[] getColumnNames() {
		String [] columns = {
			KEY_ID,
			KEY_ACTIVE,
			KEY_NAME,
			KEY_AGE,
			KEY_GENDER
		};
		
		return columns;
	}
}
