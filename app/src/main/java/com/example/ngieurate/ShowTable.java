package com.example.ngieurate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowTable extends AppCompatActivity {
    Button backBtn, updateBtn;
    GridView mainGridView;
    SimpleAdapter adapter;
    Connection connection;
    SQLSenderConnector sender;
    Spinner spinner;
    TextView help;
    List<Map<String,String>> listForNewIntent = new ArrayList<Map<String,String>>();
    Integer instit_code; String group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_table);
        spinner = findViewById(R.id.tableSpinner);
        backBtn = findViewById(R.id.buttonBack);
        mainGridView =  findViewById(R.id.mainGrid);
        updateBtn = findViewById(R.id.buttonUpdate);
        help = findViewById(R.id.helpUpdate);
        Intent intent = getIntent();
        group = intent.getStringExtra("group_number");
        instit_code = intent.getIntExtra("instit_code", -1);
        ArrayAdapter<?> adapterSpinner =
                ArrayAdapter.createFromResource(this, R.array.displayRating,
                        android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowTable.this, ProfileUser.class);
                startActivity(intent);
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listForNewIntent.clear();  //для избежания бага очищаем лист для намерений
                help.setVisibility(View.INVISIBLE);
                int position = spinner.getSelectedItemPosition();

                try{
                    String query = null;
                    sender = new SQLSenderConnector();
                    connection = sender.toOwnConnection();
                    if (connection!=null){
                        if (position == 0)
                            query = "SELECT ROW_NUMBER() OVER (ORDER BY ALL_POINTS DESC) AS RATE, FIO,GROUP_NUMBER,ALL_POINTS,ACHIEVMENTS_ID,INSTIT_CODE FROM STUDENT_DATA;";
                        if (position == 1)
                            query = "SELECT ROW_NUMBER() OVER (ORDER BY ALL_POINTS DESC) AS RATE, FIO,GROUP_NUMBER,ALL_POINTS,ACHIEVMENTS_ID,INSTIT_CODE FROM STUDENT_DATA WHERE  GROUP_NUMBER = \'" +group +"\';";
                        if (position == 2)
                            query = "SELECT ROW_NUMBER() OVER (ORDER BY ALL_POINTS DESC) AS RATE, FIO,GROUP_NUMBER,ALL_POINTS,ACHIEVMENTS_ID,INSTIT_CODE FROM STUDENT_DATA WHERE  INSTIT_CODE = "+instit_code+";";
                        Statement statement = connection.createStatement();
                        ResultSet set = statement.executeQuery(query);
                        List<Map<String,String>> data = new ArrayList<Map<String,String>>();
                        while (set.next()){
                            Map<String, String> tab = new HashMap<String, String>();
                            tab.put("RATE", set.getString("RATE"));
                            tab.put("EMPTY1","  ");
                            tab.put("FIO", set.getString("FIO"));
                            tab.put("EMPTY2","  ");
                            tab.put("GROUP_NUMBER", set.getString("GROUP_NUMBER"));
                            tab.put("EMPTY3","  ");
                            tab.put("ALL_POINTS", set.getString("ALL_POINTS"));
                            tab.put("EMPTY4","  ");
                            tab.put("ACHIEVMENTS_ID", set.getString("ACHIEVMENTS_ID"));
                            tab.put("EMPTY5","  ");
                            tab.put("INSTIT_CODE", set.getString("INSTIT_CODE"));
                            data.add(tab);
                            listForNewIntent.add(tab);
                        }
                        String[] from = {"RATE","EMPTY1","FIO","EMPTY2","GROUP_NUMBER","EMPTY3","ALL_POINTS"};
                        int[] to = {R.id.rate,R.id.empty1,R.id.fioTable,R.id.empty2,R.id.grNumTable,R.id.empty3,R.id.pointsTable};
                        adapter = new SimpleAdapter(ShowTable.this, data, R.layout.gridview_layout,from,to);
                        mainGridView.setAdapter(adapter);
                    }
                }
                catch (SQLException throwables) {
                    Log.d("SHOW ERROR: ", throwables.getMessage());
                    throwables.printStackTrace();
                }

            }
        });
        mainGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String,String> tempMap =  listForNewIntent .get(i);
                Intent intent = new Intent(ShowTable.this, ProfileUser.class);
                intent.putExtra("fio", tempMap.get("FIO"));
                intent.putExtra("group", tempMap.get("GROUP_NUMBER"));
                intent.putExtra("points", Integer.parseInt(tempMap.get("ALL_POINTS")));
                intent.putExtra("position_general", Integer.parseInt(tempMap.get("RATE")));
                intent.putExtra("idAchiev", Integer.parseInt(tempMap.get("ACHIEVMENTS_ID")));
                intent.putExtra("instit_code", Integer.parseInt(tempMap.get("INSTIT_CODE")));
                intent.putExtra("ownerOfAccount", false);
                startActivity(intent);
            }
        });



    }
}