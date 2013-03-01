package com.sdpd.flashrace;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class QuestionDataSource 
{
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	Context context;
	private String[] cols = {MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_QNUM,
			MySQLiteHelper.COLUMN_QUESTION,
			MySQLiteHelper.COLUMN_ANSWER,
			MySQLiteHelper.COLUMN_POINTS,
			MySQLiteHelper.COLUMN_STATUS,
			MySQLiteHelper.COLUMN_LAT,
			MySQLiteHelper.COLUMN_LONG,
			MySQLiteHelper.COLUMN_HINT};
	
	public QuestionDataSource(Context context)
	{
		this.context = context;
		dbHelper = new MySQLiteHelper(context);
	}
	public void open() throws SQLException
	{
		database = dbHelper.getWritableDatabase();
	}
	public void close()
	{
		dbHelper.close();
		database.close();
	}
	public Question createQuestion(String qNum,String qs,String answer,String points,int status,String latitute,String longitute,String hint)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_QUESTION,qs);
		values.put(MySQLiteHelper.COLUMN_ANSWER,answer);
		values.put(MySQLiteHelper.COLUMN_POINTS, points);
		values.put(MySQLiteHelper.COLUMN_STATUS, Integer.toString(status));
		values.put(MySQLiteHelper.COLUMN_QNUM, qNum);
		values.put(MySQLiteHelper.COLUMN_LAT, latitute);
		values.put(MySQLiteHelper.COLUMN_LONG, longitute);
		values.put(MySQLiteHelper.COLUMN_HINT, hint);
		long insertId =  database.insert(MySQLiteHelper.TABLE_QUESTIONS, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_QUESTIONS, cols, MySQLiteHelper.COLUMN_ID+" = "+insertId, null, null, null, null);
		cursor.moveToFirst();
		Question newq = cursorToQuestion(cursor);
		cursor.close();
		//Toast.makeText(context, newq.getQuestion(), Toast.LENGTH_SHORT).show();
		return newq;
		
	}
	public void deleteQuestion(Question question)
	{
		long id = question.getId();
		database.delete(MySQLiteHelper.TABLE_QUESTIONS, MySQLiteHelper.COLUMN_ID+" = "+id,null);
	}
	public List<Question> getAllQuestions()
	{
		List<Question> qslist = new ArrayList<Question>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_QUESTIONS, cols, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast())
		{
			Question q = cursorToQuestion(cursor);
			qslist.add(q);
			cursor.moveToNext();
		}
		cursor.close();
		return qslist;
	}
	public Question getQsAtId(int pos)
	{
		Cursor cursor = database.query(MySQLiteHelper.TABLE_QUESTIONS, cols, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast())
		{
			Question q = cursorToQuestion(cursor);
			if(q.getId() == pos+1)
				return q;
			cursor.moveToNext();
		}
		cursor.close();
		
		return null;
	}
	public ArrayList<Long> getAllIds()
	{
		ArrayList<Long> qslist = new ArrayList<Long>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_QUESTIONS, cols, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast())
		{
			Question q = cursorToQuestion(cursor);
			qslist.add(q.getId());
			//qslist.add("Task #"+q.getqNum());
			cursor.moveToNext();
		}
		cursor.close();
		return qslist;
	}
	public void updateStatus(Question q)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_QUESTION, q.getQuestion());
		values.put(MySQLiteHelper.COLUMN_ANSWER, q.getAnswer());
		values.put(MySQLiteHelper.COLUMN_POINTS, q.getPoints());
		values.put(MySQLiteHelper.COLUMN_STATUS, String.valueOf(q.getStatus()));
		values.put(MySQLiteHelper.COLUMN_QNUM, q.getqNum());
		values.put(MySQLiteHelper.COLUMN_LAT, q.getLatitude());
		values.put(MySQLiteHelper.COLUMN_LONG, q.getLongitute());
		values.put(MySQLiteHelper.COLUMN_HINT, q.getHint());
		database.update(MySQLiteHelper.TABLE_QUESTIONS, values, MySQLiteHelper.COLUMN_ID+" = "+String.valueOf(q.getId()), null);
	}
	private Question cursorToQuestion(Cursor cr)
	{
		Question q = new Question();
		q.setId(cr.getLong(0));
		q.setqNum(cr.getString(1));
		q.setQuestion(cr.getString(2));
		q.setAnswer(cr.getString(3));
		q.setPoints(cr.getString(4));
		q.setStatus(Integer.parseInt(cr.getString(5)));
		q.setLatitude(cr.getString(6));
		q.setLongitute(cr.getString(7));
		q.setHint(cr.getString(8));
		return q;
	}
	public void deleteAll()
	{
		 database.execSQL("DROP TABLE IF EXISTS " + MySQLiteHelper.TABLE_QUESTIONS);
		 dbHelper.onCreate(database);
	}
}
