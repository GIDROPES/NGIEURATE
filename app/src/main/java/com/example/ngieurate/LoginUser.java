package com.example.ngieurate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginUser extends AppCompatActivity {
    private EditText editLogin, editPassword;
    private String  currentLogin, currentPassword, loginFromDB, passwordFromDB;
    private Button nextBtn;
    private TextView txtNeverno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        txtNeverno = (TextView) findViewById(R.id.txtVNEVERNO);
        txtNeverno.setVisibility(View.INVISIBLE);

        editLogin = (EditText) findViewById(R.id.EditLogin);
        editPassword = (EditText) findViewById(R.id.EditPassword);

        nextBtn = (Button) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginUser.this, ProfileUser.class);
                startActivity(intent);
            }
        });
    }

    private class GetPrivateInfo extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}