package com.findcab.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private final static String DB_NAME="POI.db";
	private final static String TABLE_NAME="POI_TABLE";
	private final static String CREATE_TABLE="create table "+TABLE_NAME+ 
							"(_id integer primary key autoincrement," +
							"POIName text," +
							"POIAddress text) ";
			
	private SQLiteDatabase db;  
	
	public DBHelper(Context c){
		super(c, DB_NAME, null, 2);
	}
	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 this.db = db; 
		 db.execSQL(CREATE_TABLE);  
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void insert(ContentValues values){
		
		SQLiteDatabase db = getWritableDatabase(); 
		db.insert(TABLE_NAME, null, values);
		db.close(); 
		
	}
	
	public Cursor query(){
		
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null); 
		return c;
	}
	
	public void close(){
		if(db!=null){
			db.close();
		}
	}
	

}
