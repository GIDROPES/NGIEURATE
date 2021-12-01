package com.example.ngieurate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginUser extends AppCompatActivity {
    private EditText editLogin, editPassword;
    private String  currentLogin, currentPassword, loginFromDB, passwordFromDB;
    private Button nextBtn;
    private TextView txtNeverno;

    private String ip = "192.168.43.118", port = "1433";
    private String aClass = "net.sourceforge.jtds.jdbc.Driver";
    private String databaseName = "NGIEURATE";
    private  String login = "gidropes", password = "knyagidropesapp";
    private  String url = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + databaseName;

    private Connection connection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        txtNeverno = (TextView) findViewById(R.id.txtVNEVERNO);
        txtNeverno.setVisibility(View.INVISIBLE);
        editLogin = (EditText) findViewById(R.id.EditLogin);
        editPassword = (EditText) findViewById(R.id.EditPassword);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(view -> {
            SQLSenderConnector sender = new SQLSenderConnector();
            String login, password = null;

            login = String.valueOf(editLogin.getText());
            password = sender.sendQueryToSQLgetString("SELECT Password FROM SIGNIN_DATA WHERE Login = \'"+login+"\';");

            if (String.valueOf(editPassword.getText()).equals(password)){
                txtNeverno.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(LoginUser.this, ProfileUser.class);
                startActivity(intent);
            }
            else {
                txtNeverno.setVisibility(View.VISIBLE);
                editPassword.clearComposingText();
            }
        });

    }

    }