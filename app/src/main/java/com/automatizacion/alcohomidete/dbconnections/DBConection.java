package com.automatizacion.alcohomidete.dbconnections;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConection {
    private String connectionlink="jdbc:sqlserver://alcoholmidete.database.windows.net:1433;database=AlcoholmideteDB;user=appUser@alcoholmidete;password=UsrPass1;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
    Connection conn=null;
    public Connection connection() {
        try {
            conn = DriverManager.getConnection(connectionlink);
        }catch (SQLException se){
            Log.e("Error",se.getMessage());
        }
        return conn;
    }
}
