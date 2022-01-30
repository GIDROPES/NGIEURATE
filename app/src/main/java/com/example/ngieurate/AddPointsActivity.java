package com.example.ngieurate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddPointsActivity extends AppCompatActivity {

    private EditText getStudentFioEDITTXT, setTeachersPointsEDITTXT, setGroupStudentPointsEDITTXT, setTeachersFioEDITTXT, setDescriptionEDITTXT;
    private Button addPointsBtn;
    private Integer studentSearchingID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_points);
        getStudentFioEDITTXT = findViewById(R.id.studentEditTextAddPoints);
        setTeachersFioEDITTXT = findViewById(R.id.teacherEditTextAddPoints);
        setTeachersPointsEDITTXT = findViewById(R.id.pointsAddEditText);
        setGroupStudentPointsEDITTXT = findViewById(R.id.groupEditTextAddPoints);
        setDescriptionEDITTXT = findViewById(R.id.descriptionPointsEditText);

        addPointsBtn = findViewById(R.id.addPointsBtn);

        addPointsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean studentIsExists = checkForStudentExisting(String.valueOf(getStudentFioEDITTXT.getText()));

                if(studentIsExists){
                    SQLSenderConnector senderConnector = new SQLSenderConnector();
                    Connection connection = senderConnector.toOwnConnection();
                    try {
                        String teacherFio = String.valueOf(setTeachersFioEDITTXT.getText());
                        Integer points = Integer.parseInt(String.valueOf(setTeachersPointsEDITTXT.getText()));
                        String description = String.valueOf(setDescriptionEDITTXT.getText());
                        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TEACHERS_ACHIEVMENTS VALUES (?,?,?,?);");
                        preparedStatement.setString(1, teacherFio);
                        preparedStatement.setInt(2,  points);
                        preparedStatement.setInt(3, studentSearchingID);
                        preparedStatement.setString(4, description);
                        boolean isDone =  preparedStatement.execute();
                        Statement statement = connection.createStatement();
                        statement.execute("UPDATE STUDENT_DATA SET POINTS_FROM_TEACHERS = POINTS_FROM_TEACHERS + "+Integer.parseInt(String.valueOf(setTeachersPointsEDITTXT.getText()))+ "WHERE ACHIEVMENTS_ID ="+studentSearchingID+ ";");
                        statement.execute("UPDATE STUDENT_DATA SET ALL_POINTS = ALL_POINTS + "+Integer.parseInt(String.valueOf(setTeachersPointsEDITTXT.getText()))+ "WHERE ACHIEVMENTS_ID ="+studentSearchingID+ ";");
                        Intent intent = new Intent(AddPointsActivity.this, ModerChoosingActivity.class);
                        startActivity(intent);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                else Toast.makeText(AddPointsActivity.this,"Такого студента не существует. Возможно, вы поставили пробел в конце ФИО", Toast.LENGTH_LONG);
            }
        });
    }

    private boolean checkForStudentExisting(String fio){
        boolean isReallyExists = false;
        SQLSenderConnector sqlSenderConnector = new SQLSenderConnector();
        Connection connection = sqlSenderConnector.toOwnConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT ACHIEVMENTS_ID FROM STUDENT_DATA WHERE FIO = \'"+fio+"\' AND GROUP_NUMBER = \'"+ setGroupStudentPointsEDITTXT.getText() +"\';");
            while (set.next()){
                studentSearchingID = set.getInt(1);
                isReallyExists = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isReallyExists;
    }


}