package com.visus.database;

public interface IDatabaseTableCreator {

	// getters
	public String getTableName();
	public String getCreateTableQuery();
	public String[] getColumnNames();
	
	
}
