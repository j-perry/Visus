package com.visus.database;

public interface IDatabaseTable {

	// generic database operations
	public void open();
	public void close();

	// standardised operations 
	public int add();
	public int delete();	
	
}
