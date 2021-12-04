package com.example.ngieurate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowTable extends AppCompatActivity {
    Button backBtn;
    GridView mainGridView;
    SimpleAdapter adapter;
    Connection connection;
    SQLSenderConnector sender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_table);
        backBtn = findViewById(R.id.buttonBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowTable.this, ProfileUser.class);
                startActivity(intent);
            }
        });
        mainGridView =  findViewById(R.id.mainGrid);

        List<Map<String,String>> data = new ArrayList<Map<String,String>>();
        try{
            sender = new SQLSenderConnector();
            connection = sender.toOwnConnection();
            if (connection!=null){
                String query = "SELECT ROW_NUMBER() OVER (ORDER BY ALL_POINTS DESC) AS RATE, FIO,GROUP_NUMBER,ALL_POINTS FROM STUDENT_DATA;";
                Statement statement = connection.createStatement();
                ResultSet set = statement.executeQuery(query);
                    while (set.next()){
                        Map<String, String> tab = new HashMap<String, String>();
                        tab.put("RATE", set.getString("RATE"));
                        tab.put("EMPTY1","  ");
                        tab.put("FIO", set.getString("FIO"));
                        tab.put("EMPTY2","  ");
                        tab.put("GROUP_NUMBER", set.getString("GROUP_NUMBER"));
                        tab.put("EMPTY3","  ");
                        tab.put("ALL_POINTS", set.getString("ALL_POINTS"));
                        data.add(tab);
                    }
                    String[] from = {"RATE","EMPTY1","FIO","EMPTY2","GROUP_NUMBER","EMPTY3","ALL_POINTS"};
                    int[] to = {R.id.rate,R.id.empty1,R.id.fioTable,R.id.empty2,R.id.grNumTable,R.id.empty3,R.id.pointsTable};
                    adapter = new SimpleAdapter(ShowTable.this, data, R.layout.gridview_layout,from,to);
                    mainGridView.setAdapter(adapter);
            }
        } catch (SQLException throwables) {
            Log.d("SHOW ERROR: ", throwables.getMessage());
            throwables.printStackTrace();
        }

    }
}