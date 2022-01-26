package com.example.ngieurate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ModerChoosingActivity extends AppCompatActivity {
    Button checkReports, registrationStudent,  addPointsStudent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moder_choosing);

        checkReports = findViewById(R.id.moderCheckReports);
        registrationStudent = findViewById(R.id.moderRegistration);
        addPointsStudent = findViewById(R.id.moderAddPoints);

        registrationStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModerChoosingActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
        checkReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModerChoosingActivity.this, CheckReportsActivity.class);
                startActivity(intent);
            }
        });
        addPointsStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModerChoosingActivity.this, AddPointsActivity.class);
                startActivity(intent);
            }
        });

    }
}