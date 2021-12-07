package com.example.ngieurate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.Image;
import android.os.Bundle;
import android.util.Size;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class ProfileUser extends AppCompatActivity {
    ImageView clickToUpload, images;
    TextView nameFio, groupNumber,
            positionOfAll, positionInGroup,
            allPoints;
    String fio, grNum;
    Integer posAll, posGr, allPnts;
    SharedPreferences myData;
    Button checkRateBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);



        //сопоставляем кнопочки и другие нажималки
        checkRateBtn = findViewById(R.id.checkRateBtn);
        clickToUpload =  findViewById(R.id.clickToUpload);
        images = findViewById(R.id.images);
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
        checkRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileUser.this, ShowTable.class);
                startActivity(intent);
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
                            //Код для запуска камеры
                            Intent intent = new Intent(getActivity(), CameraActivity.class);
                            startActivity(intent);
                            ///////
                            //Код для сохранения фотки
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