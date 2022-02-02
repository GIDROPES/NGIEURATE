package com.example.ngieurate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeachersAchievmentsActivity extends AppCompatActivity {
    private GridView gridViewTeachersPoints;
    private SimpleAdapter simpleAdapter;
    private TextView fioStudentShowing;
    private Integer ownIdAchiev; private String fioStudent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_achievments);

        Intent intent = getIntent();
        ownIdAchiev = intent.getIntExtra("idAchiev", 0);
        fioStudent = intent.getStringExtra("fio");

        gridViewTeachersPoints = findViewById(R.id.teachersGrid);
        fioStudentShowing = findViewById(R.id.showingStudentFioTeachersPointsTXT);

        fioStudentShowing.setText(fioStudent);

        SQLSenderConnector senderConnector = new SQLSenderConnector();
        Connection connection = senderConnector.toOwnConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT TEACHER_FIO, POINTS, DESCRIPTION FROM TEACHERS_ACHIEVMENTS WHERE ID_ACHIEV_STUDENT = "+ownIdAchiev+";");
            List<Map<String,String>> data = new ArrayList<Map<String,String>>();
            while (set.next()){
                Map<String, String> tab = new HashMap<String, String>();
                tab.put("TEACHER_FIO", set.getString("TEACHER_FIO"));
                tab.put("POINTS", set.getString("POINTS"));
                tab.put("DESCRIPTION", set.getString("DESCRIPTION"));
                data.add(tab);
            }
            String[] from = {"TEACHER_FIO","POINTS","DESCRIPTION"};
            int[] to = {R.id.teacherFIOTW,R.id.pointsFromTeacherTW,R.id.teachersPointDiscription};
            simpleAdapter = new SimpleAdapter(TeachersAchievmentsActivity.this, data, R.layout.teachers_gridview,from,to);
            gridViewTeachersPoints.setAdapter(simpleAdapter);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.wtf("CANT IMAGE T.POINTS", throwables.getMessage());
            Log.wtf("CANT IMAGE T.POINTS", throwables.getLocalizedMessage());
        }
    }
}