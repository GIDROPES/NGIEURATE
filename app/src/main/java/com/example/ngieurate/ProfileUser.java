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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProfileUser extends AppCompatActivity {
    ImageView clickToUpload, images;
    TextView nameFio, groupNumber,
            positionOfAll, positionInGroup,
            allPoints;
    String fio, grNum;
    Integer posAll, posGr, allPnts;
    SharedPreferences myData;
    private Button checkRateBtn, nextBtn, prevBtn, deleteImageBtn;
    private int ownId;
    private int arrayIterator = 0;
    private ArrayList<byte[]> imageList;
    private ArrayList<String> ownImgCodes;
    private ArrayList<Integer> pointsList;
    private static final int PICKFILE_RESULT_CODE = 21;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);



        //сопоставляем кнопочки и другие нажималки
        nextBtn = findViewById(R.id.nextImage);
        prevBtn = findViewById(R.id.previousImage);
        checkRateBtn = findViewById(R.id.checkRateBtn);
        clickToUpload =  findViewById(R.id.clickToUpload);
        deleteImageBtn = findViewById(R.id.deleteImage);
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
        ownId = myData.getInt(LoginUser.APP_PREFERENCES_OWN_ID_ACHIEV,0);
        //работа с вьюшками
        nameFio.setText(fio); groupNumber.setText(grNum);
        allPoints.setText("ВСЕГО баллов: " + String.valueOf(allPnts));
        positionOfAll.setText("Поз. в общем рейтинге: "+  String.valueOf(posAll)); positionInGroup.setText("Поз. в рейтинге группы: "+ String.valueOf(posGr));

        imageList = fillArrayImages(ownId);
        ownImgCodes = getImageCodes(ownId);
        pointsList = getPointsImage(ownId);

        byte[] temporaryArrayBytes = imageList.get(0);
        Bitmap achievBitmap = BitmapFactory.decodeByteArray(temporaryArrayBytes, 0, temporaryArrayBytes.length);
        images.setImageBitmap(achievBitmap);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoViewer(true);
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoViewer(false);
            }
        });

        deleteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                AskUserDeleteDialog deleter = new AskUserDeleteDialog(ownImgCodes.get(arrayIterator));
                deleter.show(fragmentManager,"askDeleteDialog");
            }
        });
    }

    public static class AskUserDeleteDialog extends AppCompatDialogFragment{
        private final String codeForDelete;

        public AskUserDeleteDialog(String codeForDelete){
            this.codeForDelete = codeForDelete;
        }
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Осторожно!")
                    .setMessage("Вы действительно хотите удалить это фото? Баллы вычитаются")
                    .setIcon(R.drawable.basket_icon)
                    .setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("УДАЛИТЬ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteImageFromSQL(codeForDelete);
                            Intent intent = new Intent(getContext(), ProfileUser.class);
                            startActivity(intent);
                        }
                    });

            return builder.create();
        }
        private void deleteImageFromSQL(String code){
            SQLSenderConnector connector = new SQLSenderConnector();
            Connection connection = connector.toOwnConnection();
            try{
                Statement statement = connection.createStatement();
                statement.executeQuery("DELETE FROM ACHIEVMENTS WHERE IMG_CODE = "+"\'"+code+"\'"+";");
                Toast.makeText(getContext(),"УСПЕШНО УДАЛЕНО", Toast.LENGTH_LONG);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.wtf("Cant delete IMG SQL", throwables.getMessage());
                Log.wtf("Cant delete IMG SQL", throwables.getLocalizedMessage());
            }
        }
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }

    }

    private void photoViewer(boolean switchToNext){

        if (switchToNext && arrayIterator < imageList.size()) {
            arrayIterator++;
            if(arrayIterator == imageList.size()){
                arrayIterator = 0;
                showThisImage(arrayIterator);
            }
            else {
                showThisImage(arrayIterator);
            }
        }

        if(!switchToNext && arrayIterator >= 0) {
            if (arrayIterator == 0) {
                arrayIterator = imageList.size()-1;
                showThisImage(arrayIterator);
            } else {
                arrayIterator--;
                showThisImage(arrayIterator);
            }
        }

    }
    private void showThisImage(int iter){
        byte[] temporaryArrayBytes = imageList.get(iter);
        Bitmap achievBitmap = BitmapFactory.decodeByteArray(temporaryArrayBytes, 0, temporaryArrayBytes.length);
        images.setImageBitmap(achievBitmap);
    }
    public ArrayList<byte[]> fillArrayImages(int idForImages){
        ArrayList<byte[]> tempArr = new ArrayList<>();
        SQLSenderConnector connector = new SQLSenderConnector();
        Connection connection = connector.toOwnConnection();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Image FROM ACHIEVMENTS WHERE OWN_ID_FOR_SEARCH = "+idForImages+";");
            while (resultSet.next()){
                tempArr.add(resultSet.getBytes(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.wtf("Cant get IMG SQL", throwables.getMessage());
            Log.wtf("Cant get IMG SQL", throwables.getLocalizedMessage());
        }
        return tempArr;
    }
    protected ArrayList<String> getImageCodes(int idForSearchingCodes){
        ArrayList<String> gottenCodes = new ArrayList<>();
        SQLSenderConnector connector = new SQLSenderConnector();
        Connection connection = connector.toOwnConnection();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT IMG_CODE FROM ACHIEVMENTS WHERE OWN_ID_FOR_SEARCH = "+idForSearchingCodes+";");
            while (resultSet.next()){
                gottenCodes.add(resultSet.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.wtf("Cant get IMG_CODE SQL", throwables.getMessage());
            Log.wtf("Cant get IMG_CODE SQL", throwables.getLocalizedMessage());
        }
        return gottenCodes;
    }
    private ArrayList<Integer> getPointsImage(int idForSearchPoints){
        ArrayList<Integer> tempArr = new ArrayList<>();
        SQLSenderConnector connector = new SQLSenderConnector();
        Connection connection = connector.toOwnConnection();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Image FROM ACHIEVMENTS WHERE OWN_ID_FOR_SEARCH = "+idForSearchPoints+";");
            while (resultSet.next()){
                tempArr.add(resultSet.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.wtf("Cant get IMG POINTS SQL", throwables.getMessage());
            Log.wtf("Cant get IMG POINTS SQL", throwables.getLocalizedMessage());
        }
        return tempArr;
    }

}