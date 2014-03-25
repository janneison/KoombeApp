package com.jannesion.koombeaapp;

import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Almacenamiento  extends SQLiteOpenHelper{


	private SQLiteDatabase db;
	
	public Almacenamiento(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE USUARIOS " 
				+ "("
				+ "id INTEGER PRIMARY KEY,"
				+ "id_user VARCHAR(250),"
				+ "passwd VARCHAR(50),"
				+ "usuario VARCHAR(50)"
				+ ")"
				);
		this.db=db;
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}
	/************************************************************************/
	
	
	public void recordarUsuario(String usuario, String passwd,String id_user) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM `USUARIOS` WHERE `id` = '1'");
		db.execSQL("INSERT INTO USUARIOS VALUES ( '1', '"+ id_user + "', '"+ passwd +  "', '"+ usuario +"')");
	}
	
	public void eliminarUsuario(){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM `USUARIOS` WHERE `id` = '1'");
	}
	
	
	public Vector<String> recuperarUsuario(){
		Vector<String> result = new Vector<String>();
		db = getReadableDatabase();
		String[] CAMPOS = {"usuario", "passwd","id_user"};
		Cursor cursor=db.query("USUARIOS", CAMPOS, null, null, null, null, null);
		if(cursor.getCount()==0){
			return null;
		}else{
			cursor.moveToFirst();
			result.add(cursor.getString(0));
			result.add(cursor.getString(1));
			result.add(cursor.getString(2));
			cursor.close();
			return result;
		}
	}
}