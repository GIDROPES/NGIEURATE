package com.example.ngieurate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ModerChoosingActivity extends AppCompatActivity {
    Button checkReports, registrationStudent, addPointsStudent, clearAllRatingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moder_choosing);

        checkReports = findViewById(R.id.moderCheckReports);
        registrationStudent = findViewById(R.id.moderRegistration);
        addPointsStudent = findViewById(R.id.moderAddPoints);
        clearAllRatingBtn = findViewById(R.id.clearAllRating);

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


        clearAllRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AskClearAllRating askClearAllRating = new AskClearAllRating();
                askClearAllRating.show(getSupportFragmentManager(), "askClearing");
                Toast.makeText(ModerChoosingActivity.this, "Вы только что очистили весь рейтинг студентов и их достижения", Toast.LENGTH_LONG);
            }
        });
    }

    public static class AskClearAllRating extends AppCompatDialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Осторожно!")
                    .setMessage("ВНИМАНИЕ! Вы действительно хотите очистить рейтинг?")
                    .setIcon(R.drawable.basket_icon)
                    .setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("ДА, ОЧИСТИТЬ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SQLSenderConnector senderConnector = new SQLSenderConnector();
                            senderConnector.sendQueryCHANGING("DELETE FROM ACHIEVMENTS;");
                            senderConnector.sendQueryCHANGING("DELETE FROM TEACHERS_ACHIEVMENTS;");
                            senderConnector.sendQueryCHANGING("UPDATE STUDENT_DATA SET POINTS_FROM_ACHIEVS = 0, POINTS_FROM_TEACHERS = 0;");
                        }
                    });

            return builder.create();
        }
    }
}