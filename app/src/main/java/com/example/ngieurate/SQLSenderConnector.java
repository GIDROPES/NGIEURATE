package com.example.ngieurate;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLSenderConnector {
    @SuppressLint("NewApi")
    private Connection connection;
    private String
            ip = "192.168.56.1",
            port = "1433",
            dbName = "NGIEURATE",
            username = "kir",
            password = "12332";

    /*private String connectionURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";"
            + "databasename=" + dbName + ";"
            + "integratedSecurity=true;user=gidropes;password=knyagidropesapp";
            //+ "password=" + password + ";"
            //+ "TRUSTED_CONNECTION = TRUE;";
     */
    private String connectionURL = "jdbc:jtds:sqlserver://" + ip + ":1433;databaseName=NGIEURATE;integratedSecurity=true;user=gidropes;password=knyagidropesapp";

    public Connection toOwnConnection() {

        new Runnable() {
            @Override
            public void run() {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    Log.d("ClassOfOwnConError: ", e.getMessage());
                    e.printStackTrace();
                }
                Log.d("Wow", "Мы хотя бы дошли досюда");
                try {
                    connection = DriverManager.getConnection(connectionURL);
                } catch (SQLException t) {
                    Log.d("!!OwnConnectorError1st", t.getMessage());
                    Log.d("!!OwnConnectorError2st", t.getSQLState());
                    Log.d("!!OwnConnectorError3st", String.valueOf(t.getErrorCode()));
                    // Log.d("!!OwnConnectorError4st",t.getLocalizedMessage());
                    t.printStackTrace();
                }
            }
        }.run();

        return connection;
    }

    public void sendQueryToSQLForView(String query, TextView v) {
        connection = toOwnConnection();

        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resSet = statement.executeQuery(query);
                while (resSet.next()) {
                    v.setText(resSet.getString(1));
                }
            } catch (SQLException throwables) {
                Log.d("CnctrSndrErr :", throwables.getMessage());
                Log.d("CnctrSndrErr :", throwables.getLocalizedMessage());
                throwables.printStackTrace();
            }
        }
    }

    public String sendQueryToSQLgetString(String query) {
        String result = null;
        connection = toOwnConnection();

        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resSet = statement.executeQuery(query);
                while (resSet.next()) {
                    result = resSet.getString(1);
                }
            } catch (SQLException throwables) {
                Log.d("CnctrSndrErr :", throwables.getMessage());
                Log.d("CnctrSndrErr :", throwables.getLocalizedMessage());
                throwables.printStackTrace();
            }
        }
        return result;
    }

    public void sendQueryCHANGING(String query) {
        connection = toOwnConnection();
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                statement.execute(query);

            } catch (SQLException throwables) {
                Log.d("CnctrSndrErr :", throwables.getMessage());
                Log.d("CnctrSndrErr :", throwables.getLocalizedMessage());
                throwables.printStackTrace();
            }
        }

    }
}
