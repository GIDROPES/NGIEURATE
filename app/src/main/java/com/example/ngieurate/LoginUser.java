package com.example.ngieurate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginUser extends AppCompatActivity {
    private EditText editLogin, editPassword;
    private Button nextBtn;
    private TextView txtNeverno;
    private CheckBox checkBoxLogin, checkBoxModerator;

    private String ip = "192.168.43.118", port = "1433";
    private String aClass = "net.sourceforge.jtds.jdbc.Driver";
    private String databaseName = "NGIEURATE";
    private  String login = "gidropes", password = "knyagidropesapp";
    private  String url = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + databaseName;

    private Connection connection = null;

    public static final String APP_PREFERENCES = "ngieustudentdata";              //название встроенного файла сохранения
    public static final String APP_PREFERENCES_USER_FIO = "UserFio";   //ФИО по айдишнику из SQL
    public static final String APP_PREFERENCES_USER_GROUP = "UserGroup";        //Номер группы студента
    public static final String APP_PREFERENCES_USER_POINTS = "AllUserPoints";     //Все баллы пользователя (сумм)
    public static final String APP_PREFERENCES_POSITION_GENERAL = "GeneralUserPosition";     //Место в общем списке
    public static final String APP_PREFERENCES_POSITION_GROUP = "GroupUserPosition";     //Место пользователя среди группы
    public static final String APP_PREFERENCES_CHECKBOX_CHECKED = "CheckBoxBoolean";     //True False проверка чекбокса
    public static final String APP_PREFERENCES_OWN_LOGIN = "UserLogin";     //Логин
    public static final String APP_PREFERENCES_OWN_PASSWORD = "UserPassword";     //Пароль
    public static final String APP_PREFERENCES_OWN_COUNTER = "UserOwnCounterOfAchievments";     //ЛИЧНЫЙ СЧЕТЧИК
    public static final String APP_PREFERENCES_OWN_ID_ACHIEV = "UserOwnIdAchiev";     //ЛИЧНЫЙ АЙДИШНИК ДЛЯ СВЯЗИ С АЧИВКАМИ

    private int idAchiev, count;
    private String FIO;
    private String group_number;
    private Integer points;
    private Integer position_group, position_general, instit_code;

    private SharedPreferences userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        txtNeverno = (TextView) findViewById(R.id.txtVNEVERNO);
        txtNeverno.setVisibility(View.INVISIBLE);
        editLogin = (EditText) findViewById(R.id.EditLogin);
        editPassword = (EditText) findViewById(R.id.EditPassword);
        checkBoxLogin = findViewById(R.id.checkBoxLogin);
        checkBoxModerator = findViewById(R.id.checkBoxModerator);

        userData = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        boolean isChecked = userData.getBoolean(APP_PREFERENCES_CHECKBOX_CHECKED, false);
        String savedLogin = userData.getString(APP_PREFERENCES_OWN_LOGIN,"");
        String savedPassword = userData.getString(APP_PREFERENCES_OWN_PASSWORD,"");

        if (isChecked){
            checkBoxLogin.setChecked(true);
            editLogin.setText(savedLogin);
            editPassword.setText(savedPassword);
        }
        else checkBoxLogin.setChecked(false);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(view -> {
            SQLSenderConnector sender = new SQLSenderConnector();
            String login, password = null;

            if(checkBoxModerator.isChecked()){
                login = String.valueOf(editLogin.getText());
                password = sender.sendQueryToSQLgetString("SELECT PASSWORD_KEY FROM MODER_DATA WHERE LOGIN_KEY = \'" + login + "\';");
                if (String.valueOf(editPassword.getText()).equals(password)) {
                    txtNeverno.setVisibility(View.INVISIBLE);

                    if (checkBoxLogin.isChecked()) {
                        new Runnable() {
                            @Override
                            public void run() {
                                userData = getSharedPreferences(LoginUser.APP_PREFERENCES, MODE_PRIVATE);
                                SharedPreferences.Editor editor = userData.edit();
                                editor.putBoolean(LoginUser.APP_PREFERENCES_CHECKBOX_CHECKED, true);
                                editor.apply();
                                editor.putString(LoginUser.APP_PREFERENCES_OWN_LOGIN, String.valueOf(editLogin.getText()));
                                editor.apply();
                                editor.putString(LoginUser.APP_PREFERENCES_OWN_PASSWORD, String.valueOf(editPassword.getText()));
                                editor.apply();
                            }
                        }.run();
                    } else {
                        new Runnable() {
                            @Override
                            public void run() {
                                userData = getSharedPreferences(LoginUser.APP_PREFERENCES, MODE_PRIVATE);
                                SharedPreferences.Editor editor = userData.edit();
                                editor.putBoolean(LoginUser.APP_PREFERENCES_CHECKBOX_CHECKED, false);
                                editor.apply();
                                editor.remove(LoginUser.APP_PREFERENCES_OWN_LOGIN);
                                editor.apply();
                                editor.remove(LoginUser.APP_PREFERENCES_OWN_PASSWORD);
                                editor.apply();
                            }
                        }.run();
                    }

                    Intent intent = new Intent(LoginUser.this, ModerChoosingActivity.class); //TODO:ВВЕСТИ НОВУЮ АКТИВИТИ ДЛЯ МОДЕРАТОРА
                    startActivity(intent);
                }
                else {txtNeverno.setVisibility(View.VISIBLE);}
            }
            else {
                login = String.valueOf(editLogin.getText());
                password = sender.sendQueryToSQLgetString("SELECT Password FROM SIGNIN_DATA WHERE Login = \'" + login + "\';");

                if (String.valueOf(editPassword.getText()).equals(password)) {
                    txtNeverno.setVisibility(View.INVISIBLE);
                    if (checkBoxLogin.isChecked()) {
                        new Runnable() {
                            @Override
                            public void run() {
                                userData = getSharedPreferences(LoginUser.APP_PREFERENCES, MODE_PRIVATE);
                                SharedPreferences.Editor editor = userData.edit();
                                editor.putBoolean(LoginUser.APP_PREFERENCES_CHECKBOX_CHECKED, true);
                                editor.apply();
                                editor.putString(LoginUser.APP_PREFERENCES_OWN_LOGIN, String.valueOf(editLogin.getText()));
                                editor.apply();
                                editor.putString(LoginUser.APP_PREFERENCES_OWN_PASSWORD, String.valueOf(editPassword.getText()));
                                editor.apply();
                            }
                        }.run();
                    } else {
                        new Runnable() {
                            @Override
                            public void run() {
                                userData = getSharedPreferences(LoginUser.APP_PREFERENCES, MODE_PRIVATE);
                                SharedPreferences.Editor editor = userData.edit();
                                editor.putBoolean(LoginUser.APP_PREFERENCES_CHECKBOX_CHECKED, false);
                                editor.apply();
                                editor.remove(LoginUser.APP_PREFERENCES_OWN_LOGIN);
                                editor.apply();
                                editor.remove(LoginUser.APP_PREFERENCES_OWN_PASSWORD);
                                editor.apply();
                            }
                        }.run();
                    }
                    Integer id = Integer.parseInt(sender.sendQueryToSQLgetString("SELECT [NGIEURATE].dbo.SIGNIN_DATA.ID_Of_Student FROM [NGIEURATE].dbo.SIGNIN_DATA WHERE Login = \'" + login + "\';"));
                    initializeFromSQL(id);
                    //makeSave(id);
                    Intent intent = new Intent(LoginUser.this, ProfileUser.class);
                    intent.putExtra("fio", FIO);
                    intent.putExtra("group", group_number);
                    intent.putExtra("points", points);
                    intent.putExtra("position_general", position_general);
                    intent.putExtra("position_group", position_group);
                    intent.putExtra("idAchiev", idAchiev);
                    intent.putExtra("instit_code", instit_code);
                    intent.putExtra("ownerOfAccount", true);
                    startActivity(intent);
                } else {
                    txtNeverno.setVisibility(View.VISIBLE);
                    editPassword.clearComposingText();
                }
            }
        });

    }





        public void makeSave(int id){ initializeFromSQL(id); }

        private void initializeFromSQL(int id){
            SQLSenderConnector connector = new SQLSenderConnector();
            FIO =  connector.sendQueryToSQLgetString("SELECT FIO FROM STUDENT_DATA WHERE ID=\'"+id+"\';");
            group_number =  connector.sendQueryToSQLgetString("SELECT GROUP_NUMBER FROM STUDENT_DATA WHERE ID=\'"+id+"\';");
            points = Integer.parseInt(connector.sendQueryToSQLgetString("SELECT ALL_POINTS FROM STUDENT_DATA WHERE ID=\'"+id+"\';"));
            position_general = Integer.parseInt(connector.sendQueryToSQLgetString("SELECT RATE FROM(" +
                    "SELECT *, ROW_NUMBER() OVER (ORDER BY ALL_POINTS DESC) AS RATE FROM STUDENT_DATA) as RT " +
                    "WHERE ID = " + id + " ;"));  //TODO: ПЕРЕНЕСТИ КУСОК КОДА В КЛАСС PROFILEUSER
            position_group = Integer.parseInt(connector.sendQueryToSQLgetString ("SELECT RATE FROM("+
                    "SELECT *, ROW_NUMBER() OVER (ORDER BY ALL_POINTS DESC) AS RATE FROM STUDENT_DATA WHERE GROUP_NUMBER = \'"+group_number+"\') as RT " +
                    "WHERE ID ="+id+";")); //TODO: ПЕРЕНЕСТИ КУСОК КОДА В КЛАСС PROFILEUSER
            idAchiev = Integer.parseInt(connector.sendQueryToSQLgetString("SELECT ACHIEVMENTS_ID FROM STUDENT_DATA WHERE ID=\'"+id+"\';"));
            //count = Integer.parseInt(connector.sendQueryToSQLgetString("SELECT COUNT(*) FROM ACHIEVMENTS WHERE  OWN_ID_FOR_SEARCH ="+idAchiev+";"));
            //writeToPreferences(FIO, group_number, points, position_general, position_group, idAchiev,count);
            instit_code = Integer.parseInt(connector.sendQueryToSQLgetString("SELECT INSTIT_CODE FROM STUDENT_DATA WHERE ID=\'"+id+"\';"));
        }

        private void writeToPreferences(String fio, String group,  Integer pnts, Integer pos_gen, Integer pos_gr,int idAchievment, int ownCount){

            new Runnable(){
                @Override
                public void run() {
                    userData = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
                    SharedPreferences.Editor editor = userData.edit();
                    editor.putString(APP_PREFERENCES_USER_FIO, fio);
                    editor.apply();
                    editor.putString(APP_PREFERENCES_USER_GROUP, group);
                    editor.apply();
                    editor.putInt(APP_PREFERENCES_USER_POINTS, pnts);
                    editor.apply();
                    editor.putInt(APP_PREFERENCES_POSITION_GENERAL,pos_gen);
                    editor.apply();
                    editor.putInt(APP_PREFERENCES_POSITION_GROUP,pos_gr);
                    editor.apply();
                    editor.putInt(APP_PREFERENCES_OWN_ID_ACHIEV,idAchievment);
                    editor.apply();
                    editor.putInt(APP_PREFERENCES_OWN_COUNTER,ownCount);
                    editor.apply();
                }
            }.run();

        }

    }