package com.example.ltm;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnection {
    Connection con;
    @SuppressLint("NewApi")
    public Connection conClass(){
            String ip="database-1.cxs642o42jdg.ap-southeast-2.rds.amazonaws.com", port = "1433",db ="CAR_PARKING",username="admin",password="Dangcongsan#123";
        StrictMode.ThreadPolicy a = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(a);
        String ConnectURL = null;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            // Trong phương thức conClass() trong class DbConnection:
            ConnectURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/"+ db + ";user=" + username + ";password=" + password + ";";

            con = DriverManager.getConnection(ConnectURL);
        }
        catch (Exception e ){
            Log.e("Khong co internet, vui long ket noi mang",e.getMessage());

        }
        return con;
    }

}
