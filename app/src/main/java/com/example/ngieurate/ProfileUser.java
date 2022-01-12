package com.example.ngieurate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileUser extends AppCompatActivity {
    ImageView clickToUpload, images;
    TextView nameFio, groupNumber,
            positionOfAll, positionInGroup,
            allPoints;
    String fio, grNum;
    Integer posAll, posGr, allPnts;
    SharedPreferences myData;
    Button checkRateBtn;
    private static final int PICKFILE_RESULT_CODE = 21;



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
        private Bitmap bitmapFromGallery;
        String filePath = "";
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
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(
                                    Intent.createChooser(
                                            intent,
                                            "Select Image from here..."),
                                    PICKFILE_RESULT_CODE);
                        }
                    });

            return builder.create();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1)
                if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null) {
                    Uri path =  data.getData();
                    try {
                        bitmapFromGallery = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), path);
                        uploadImageToServer(bitmapFromGallery);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
        private void uploadImageToServer(Bitmap currentBitmap){
            File file = new File(filePath);
            Retrofit retrofit = RetroClient.getRetrofit();
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part parts = MultipartBody.Part.createFormData("newimage",file.getName(),requestBody);
            RequestBody someData = RequestBody.create(MediaType.parse("text/plain"),"Новая картинка");
            Api api = retrofit.create(Api.class);
            Call call = api.uploadImage(parts, someData);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {

                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            currentBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte[] imgByteArr = byteArrayOutputStream.toByteArray();

            String encodedImg =  Base64.encodeToString(imgByteArr, Base64.DEFAULT);

        }
    }




}