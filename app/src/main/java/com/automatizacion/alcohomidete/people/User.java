package com.automatizacion.alcohomidete.people;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.automatizacion.alcohomidete.dbconnections.DBCConnectionHelper;

import static java.security.AccessController.getContext;

public class User extends People{
    private String userName="";
    private String password="";
    private String scoreID="";

    public User(String userName, Context context) {
        this.userName=userName;
        getData(context);
    }

    @SuppressLint("Range")
    public void getData(Context context) {
        try {
            DBCConnectionHelper conn = new DBCConnectionHelper(context);
            SQLiteDatabase db = conn.getWritableDatabase();
            Cursor userData = db.rawQuery("SELECT P_NAME,SURNAME,AGE,GENDER,PASS,RECORD_ID FROM People INNER JOIN Users ON People.USER_NAME=Users.USER_NAME WHERE People.USER_NAME='" + userName + "';", null);
            userData.moveToFirst();
            do {
                setName(userData.getString(userData.getColumnIndex("P_NAME")));
                setSurname(userData.getString(userData.getColumnIndex("SURNAME")));
                setAge(userData.getString(userData.getColumnIndex("AGE")));
                setGender(userData.getString(userData.getColumnIndex("GENDER")));
                setPassword(userData.getString(userData.getColumnIndex("PASS")));
                setScoreID(userData.getString(userData.getColumnIndex("RECORD_ID")));
            } while (userData.moveToNext());
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateData(Context context,String userName,String password) {
        try {
            DBCConnectionHelper conn = new DBCConnectionHelper(context);
            SQLiteDatabase db = conn.getWritableDatabase();
            db.execSQL("UPDATE Users SET USER_NAME='"+userName+"',PASS='"+password+"' " +
                    "WHERE USER_NAME=(SELECT USER_NAME FROM People WHERE P_NAME='"+getName()+"' AND SURNAME='"+getSurname()+"');");
            db.execSQL("UPDATE People SET USER_NAME=(SELECT USER_NAME FROM Users WHERE USER_NAME='"+userName+"') " +
                    "WHERE P_NAME='"+getName()+"' AND SURNAME='"+getSurname()+"'");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUserName(userName);
        setPassword(password);
    }


    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getScoreID() {
        return scoreID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setScoreID(String scoreID) {
        this.scoreID = scoreID;
    }

}
