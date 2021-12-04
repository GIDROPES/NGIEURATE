package com.example.ngieurate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileUser extends AppCompatActivity {
    ImageView clickToUpload;
    TextView nameFio, groupNumber,
            positionOfAll, positionInGroup,
            allPoints;
    String fio, grNum;
    Integer posAll, posGr, allPnts;
    SharedPreferences myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        //сопоставляем кнопочки
        clickToUpload =  findViewById(R.id.clickToUpload);
        //сопоставляем текстовые штуки
        nameFio = findViewById(R.id.nameFio);
        groupNumber = findViewById(R.id.groupNumber);
        allPoints = findViewById(R.id.allPoints);
        positionOfAll = findViewById(R.id.positionOfAll);
        positionInGroup = findViewById(R.id.positionInGroup);
        //действия
        clickToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                AskUserUploadDialog askDialog = new AskUserUploadDialog();
                askDialog.show(manager, "askUserUploadDialog");
            }
        });
        myData = getSharedPreferences(LoginUser.APP_PREFERENCES, MODE_PRIVATE);
        //строковые
        fio = myData.getString(LoginUser.APP_PREFERENCES_USER_FIO,"No data");
        grNum =  myData.getString(LoginUser.APP_PREFERENCES_USER_GROUP,"");
        //числовые
        posAll = myData.getInt(LoginUser.APP_PREFERENCES_POSITION_GENERAL, 0);
        posGr = myData.getInt(LoginUser.APP_PREFERENCES_POSITION_GROUP,0);
        allPnts = myData.getInt(LoginUser.APP_PREFERENCES_USER_POINTS, 0);
        //работа с вьюшками
        nameFio.setText(fio); groupNumber.setText(grNum);
        allPoints.setText("ВСЕГО баллов: " + String.valueOf(allPnts));
        positionOfAll.setText("Поз. в общем рейтинге: "+  String.valueOf(posAll)); positionInGroup.setText("Поз. в рейтинге группы: "+ String.valueOf(posGr));

    }
    public static class AskUserUploadDialog extends AppCompatDialogFragment{
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Выберите способ загрузки")
                    .setMessage("Сфотографируйте достижение или загрузите готовое фото")
                    .setIcon(R.drawable.upload_image)
                    .setNegativeButton("КАМЕРА", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("ГАЛЕРЕЯ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

            return builder.create();
        }
    }
}