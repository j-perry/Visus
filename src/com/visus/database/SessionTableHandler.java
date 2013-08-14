package com.visus.database;

public class SessionTableHandler implements IDatabaseTableCreator {

	private final static String TABLE_SESSIONS  = "Sessions";
	
	// columns
	public final static String KEY_ID          = "Id";
	public final static String KEY_USER_ID     = "UserId";	
	public final static String KEY_DATE        = "Date";	
	public final static String KEY_TIME        = "Time";
	public final static String KEY_DURATION    = "Duration";
	public final static String KEY_TYPE        = "Type";
	
	private final static String qryCreateSessionsTable = "CREATE TABLE " + TABLE_SESSIONS + 
                                                         " ( " +
            	                                              KEY_ID + " INTEGER PRIMARY KEY, " + 
                                                              KEY_USER_ID + " INTEGER, " +
            	                                              KEY_DATE + " TEXT " +
//               										      KEY_TIME + " TEXT, " +
//              										      KEY_DURATION + " TEXT " +
//           											      KEY_TYPE + " TEXT" +
            										     " )";
	
	public SessionTableHandler() {
		super();
	}
	
	@Override
	public String getTableName() {		
		return TABLE_SESSIONS;
	}

	@Override
	public String getCreateTableQuery() {
		return qryCreateSessionsTable;
	}

	@Override
	public String[] getColumnNames() {
		String columns[] = {
			KEY_ID,
			KEY_USER_ID,
			KEY_DATE,
			KEY_TIME,
			KEY_DURATION,
			KEY_TYPE
		};
		
		return columns;
	}	
}
