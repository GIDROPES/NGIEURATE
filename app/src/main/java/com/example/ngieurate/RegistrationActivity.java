package com.example.ngieurate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegistrationActivity extends AppCompatActivity {
    private EditText editRegLogin, editRegPassword, editRegFIO, editRegGroup;
    private Spinner registrationSpinner;
    private Button acceptRegistration;
    private String login, password, fio, group;
    private Integer instit_code,id_of_student;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registrationSpinner = findViewById(R.id.registrationSpinner);
        acceptRegistration = findViewById(R.id.acceptRegistration);
        editRegLogin = findViewById(R.id.editRegistrationLogin);
        editRegPassword = findViewById(R.id.editRegistrationPassword);
        editRegFIO = findViewById(R.id.editRegistrationFIO);
        editRegGroup = findViewById(R.id.editRegistrationGroup);

        ArrayAdapter<?> adapterInstitutions = ArrayAdapter.createFromResource(this, R.array.institutions,
                android.R.layout.simple_spinner_item);
        adapterInstitutions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        registrationSpinner.setAdapter(adapterInstitutions);

        acceptRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login = String.valueOf(editRegLogin.getText());
                password = String.valueOf(editRegPassword.getText());
                fio = String.valueOf(editRegFIO.getText());
                group = String.valueOf(editRegGroup.getText());
                instit_code = getInstitCode(String.valueOf(registrationSpinner.getSelectedItem()));
                boolean isExist = isExistingLogin(login);
                if(!isExist){
                    if(instit_code!=0){
                        SQLSenderConnector connector = new SQLSenderConnector();
                        connector.sendQueryCHANGING("INSERT INTO SIGNIN_DATA VALUES(\'"+login+"\',\'"+password+"\');");

                        id_of_student = Integer.parseInt(connector.sendQueryToSQLgetString("SELECT ID_Of_Student FROM SIGNIN_DATA WHERE Login = \'"+login+"\';"));

                        Connection connection = connector.toOwnConnection();
                        PreparedStatement preparedStatement = null;
                        try {
                            preparedStatement = connection
                                    .prepareStatement("INSERT INTO STUDENT_DATA (ID,FIO,GROUP_NUMBER,POINTS_FROM_ACHIEVS,POINTS_FROM_TEACHERS,ALL_POINTS,INSTIT_CODE) VALUES (?,?,?,?,?,?,?);");
                            preparedStatement.setInt(1,id_of_student);
                            preparedStatement.setString(2, fio);
                            preparedStatement.setString(3,group);
                            preparedStatement.setInt(4,0);
                            preparedStatement.setInt(5,0);
                            preparedStatement.setInt(6, 0);
                            preparedStatement.setInt(7, instit_code);
                            preparedStatement.execute();
                            Intent intent = new Intent(RegistrationActivity.this, ModerChoosingActivity.class);
                            startActivity(intent);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(RegistrationActivity.this,"Ошибка в получении кода института. Обратитесь к администратору приложения", Toast.LENGTH_LONG);
                    }
                }
                else {
                    Toast.makeText(RegistrationActivity.this,"Такой логин занят. Используйте другой", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private boolean isExistingLogin(String checkLogin){
        boolean existion = false;
        SQLSenderConnector connector = new SQLSenderConnector();
        Connection connection = connector.toOwnConnection();
        try{

            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT Password FROM SIGNIN_DATA WHERE Login =\'"+checkLogin+"\';");

            if (set.next()) { existion = true; }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return existion;
    }

    private int getInstitCode(String institName){
        int resCode = 0;
        SQLSenderConnector connector = new SQLSenderConnector();
        try {
            resCode = Integer.parseInt(connector.sendQueryToSQLgetString("SELECT INSTIT_NUMBER_KEY FROM INSTITUTIONS WHERE INSTITUTE =\'"+institName+"\';"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resCode;
    }
}