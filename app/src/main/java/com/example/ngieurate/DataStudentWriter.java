package com.example.ngieurate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public class DataStudentWriter extends AppCompatActivity {
    private int id;
    private String FIO;
    private String group_number;
    private Integer points;
    private Integer position_group, position_general;

    public static final String APP_PREFERENCES = "ngieustudentdata";              //название встроенного файла сохранения
    public static final String APP_PREFERENCES_USER_FIO = "UserFio";   //ФИО по айдишнику из SQL
    public static final String APP_PREFERENCES_USER_GROUP = "UserGroup";        //Номер группы студента
    public static final String APP_PREFERENCES_USER_POINTS = "AllUserPoints";     //Все баллы пользователя (сумм) TODO: Написать функцию для перезаписи и перепроверки
    public static final String APP_PREFERENCES_POSITION_GENERAL = "GeneralUserPosition";     //Место в общем списке
    public static final String APP_PREFERENCES_POSITION_GROUP = "GroupUserPosition";     //Место пользователя среди группы
    private SharedPreferences userData;

    public DataStudentWriter(int id) {
        this.id = id;
    }
    public void makeSave(){ initializeFromSQL(); }

    private void initializeFromSQL(){
        SQLSenderConnector connector = new SQLSenderConnector();
        FIO =  connector.sendQueryToSQLgetString("SELECT FIO FROM STUDENT_DATA WHERE ID=\'"+id+"\';");
        group_number =  connector.sendQueryToSQLgetString("SELECT GROUP_NUMBER FROM STUDENT_DATA WHERE ID=\'"+id+"\';");
        points = Integer.parseInt(connector.sendQueryToSQLgetString("SELECT ALL_POINTS FROM STUDENT_DATA WHERE ID=\'"+id+"\';"));
        position_general = Integer.parseInt(connector.sendQueryToSQLgetString("SELECT RATE FROM(" +
                "SELECT *, ROW_NUMBER() OVER (ORDER BY ALL_POINTS DESC) AS RATE FROM STUDENT_DATA) as RT " +
                "WHERE ID = " + id + " ;"));
        position_group = Integer.parseInt(connector.sendQueryToSQLgetString ("SELECT RATE FROM("+
                "SELECT *, ROW_NUMBER() OVER (ORDER BY ALL_POINTS DESC) AS RATE FROM STUDENT_DATA WHERE GROUP_NUMBER = \'"+group_number+"\') as RT " +
                "WHERE ID ="+id+";"));
        writeToPreferences(FIO, group_number, points, position_general, position_group);
    }

    private void writeToPreferences(String fio, String group,  Integer pnts, Integer pos_gen, Integer pos_gr){

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
            }
        }.run();

    }
}
