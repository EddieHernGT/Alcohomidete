package com.automatizacion.alcohomidete.dbconnections;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBCConnectionHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Alcoholmidete.db";
    public static final String CREATION_TABLE1="CREATE TABLE  People (" +
            "P_NAME TEXT PRIMARY KEY," +
            "SURNAME TEXT," +
            "AGE TEXT ," +
            "GENDER  TEXT ," +
            "USER_NAME TEXT ," +
            "FOREIGN KEY (USER_NAME) REFERENCES Users(USER_NAME)) ;";
    public static final String CREATION_TABLE2="CREATE TABLE Users (" +
            "USER_NAME TEXT ," +
            "PASS  TEXT ,"+
            "RECORD_ID TEXT,"+
            "FOREIGN KEY (RECORD_ID) REFERENCES Score(RECORD_ID)) ;";
    public static final String CREATION_TABLE3="CREATE TABLE Score (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "RECORD_ID TEXT," +
            "ALCOHOLIC_GRADE  TEXT," +
            "REGISTER_DATE TEXT," +
            "REGISTER_TIME TEXT);";


    public DBCConnectionHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATION_TABLE1);
        db.execSQL(CREATION_TABLE2);
        db.execSQL(CREATION_TABLE3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Users;");
        db.execSQL("DROP TABLE IF EXISTS People;");
        db.execSQL("DROP TABLE IF EXISTS Score;");
        onCreate(db);
    }
}
