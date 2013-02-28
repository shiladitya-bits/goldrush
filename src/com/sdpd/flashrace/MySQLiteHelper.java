package com.sdpd.flashrace;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "questions.db";
	private static final int DATABASE_VERSION = 2 ;
	public static final String TABLE_QUESTIONS = "quest";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_QUESTION = "question";
	public static final String COLUMN_ANSWER = "answer";
	public static final String COLUMN_POINTS = "points";
	public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_QNUM = "qnum";
	public static final String COLUMN_LAT = "latitude";
	public static final String COLUMN_LONG = "longitute";		
	public static final String COLUMN_HINT = "hint";		
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
      + TABLE_QUESTIONS + "(" 
      + COLUMN_ID + " integer primary key autoincrement, " 
      + COLUMN_QNUM+" text not null, "
      +COLUMN_QUESTION+ " text not null,"
      +COLUMN_ANSWER+" text not null,"
      +COLUMN_POINTS+" text not null,"
      +COLUMN_STATUS+" ,"
      +COLUMN_LAT+" ,"
      +COLUMN_LONG+" ,"
      +COLUMN_HINT+");";
public MySQLiteHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL(DATABASE_CREATE);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
	    onCreate(db);	
	}
}
