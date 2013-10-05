package com.visus.database;

import com.visus.entities.User;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public interface IDatabaseTable {

	// generic database operations
	public void open();
	public void close();
	
}
