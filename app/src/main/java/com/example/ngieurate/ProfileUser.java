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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProfileUser extends AppCompatActivity {
    ImageView images;
    //видно всегда
    TextView nameFio, groupNumber,
            positionOfAll,
            allPoints, watchAchType;
    //видно не всегда
    TextView txtUpload, formatsTxtV, myAccount;
    ImageView clickToUpload;

    private boolean isOwnerOfAccount;
    public static String fio, grNum, institution_str;
    public static Integer posAll, posGr, allPnts, instit_code;
    private SharedPreferences myData;
    private Button checkRateBtn, nextBtn, prevBtn, deleteImageBtn, sendReportBtn, watchTeachersPoints;
    public static Integer ownId;
    private int arrayIterator = 0;
    private ArrayList<byte[]> imageList;
    private ArrayList<String> ownImgCodes;
    private ArrayList<Integer> pointsList;
    private  ArrayList<String> achievTypes;
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
        sendReportBtn = findViewById(R.id.sendReportBTN);
        watchTeachersPoints = findViewById(R.id.watchTeachersPoints);
        images = findViewById(R.id.images);
        //сопоставляем текстовые штуки
        nameFio = findViewById(R.id.nameFio);
        groupNumber = findViewById(R.id.groupNumber);
        allPoints = findViewById(R.id.allPoints);
        positionOfAll = findViewById(R.id.positionOfAll);
        txtUpload = findViewById(R.id.txtUpload);
        formatsTxtV = findViewById(R.id.formatsTxtV);
        myAccount = findViewById(R.id.myAccountTXT);
        watchAchType = findViewById(R.id.txtWatchMyAch);
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
                intent.putExtra( "group_number",grNum);
                intent.putExtra("instit_code",instit_code);
                startActivity(intent);
            }
        });
        //myData = getSharedPreferences(LoginUser.APP_PREFERENCES, MODE_PRIVATE);
        getIntentInfo();
        if (!isOwnerOfAccount){
            deleteImageBtn.setVisibility(View.INVISIBLE);
            sendReportBtn.setVisibility(View.VISIBLE);
            txtUpload.setText("");
            clickToUpload.setVisibility(View.INVISIBLE);
            formatsTxtV.setText("Выберите фотографию и отправьте репорт, если фото не соответствует требованиям");
            checkRateBtn.setClickable(false); checkRateBtn.setAlpha(0.4f);
            SQLSenderConnector connector = new SQLSenderConnector();
            institution_str = connector.sendQueryToSQLgetString("SELECT INSTITUTE FROM INSTITUTIONS WHERE INSTIT_NUMBER_KEY = "+instit_code+";");
            myAccount.setText("Студент(-ка) "+ institution_str); myAccount.setTextSize(12f); myAccount.setTextColor(getResources().getColor(R.color.dark_ocean));
        }
        else{
            sendReportBtn.setVisibility(View.INVISIBLE);
        }
        //работа с вьюшками
        nameFio.setText(fio); groupNumber.setText(grNum);
        allPoints.setText("ВСЕГО баллов: " + allPnts);
        positionOfAll.setText("Поз. в общем рейтинге: "+ posAll);

        imageList = fillArrayImages(ownId);
        ownImgCodes = getImageCodes(ownId);
        pointsList = getPointsImage(ownId);
        achievTypes = getTypesImage(ownId);

        if(!imageList.isEmpty()) {
            byte[] temporaryArrayBytes = imageList.get(0);
            Bitmap achievBitmap = BitmapFactory.decodeByteArray(temporaryArrayBytes, 0, temporaryArrayBytes.length);
            images.setImageBitmap(achievBitmap);
            watchAchType.setText(achievTypes.get(arrayIterator) + ": " + pointsList.get(arrayIterator));
        }
        else {
            images.setImageResource(R.drawable.images_empty);
            sendReportBtn.setClickable(false); sendReportBtn.setAlpha(0.4f);
            watchAchType.setText("Пусто");
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imageList.isEmpty())
                photoViewer(true);
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imageList.isEmpty())
                photoViewer(false);
            }
        });

        deleteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                AskUserDeleteDialog deleter = new AskUserDeleteDialog(ownImgCodes.get(arrayIterator), ownId);
                deleter.show(fragmentManager,"askDeleteDialog");
            }
        });

        sendReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReportToModer(imageList.get(arrayIterator), ownImgCodes.get(arrayIterator), ownId," ");
                Toast.makeText(ProfileUser.this,"Жалоба отправлена и будет рассмотрена в скором времени!", Toast.LENGTH_LONG);
            }
        });

        watchTeachersPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToTeachers = new Intent(ProfileUser.this, TeachersAchievmentsActivity.class);
                intentToTeachers.putExtra("idAchiev", ownId);
                intentToTeachers.putExtra("fio", fio);
                startActivity(intentToTeachers);
            }
        });

    }

    private void sendReportToModer(byte[] arrReportedImg, String code, int id_for_search, String description){
            SQLSenderConnector connector = new SQLSenderConnector();
            String type = connector.sendQueryToSQLgetString("SELECT TYPE FROM ACHIEVMENTS WHERE IMG_CODE = \'"+code+"\';");
            Connection connection = connector.toOwnConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO REPORTS VALUES (?,?,?,?,?);");
                preparedStatement.setInt(1,id_for_search);
                preparedStatement.setString(2,type);
                preparedStatement.setBytes(3,arrReportedImg);
                preparedStatement.setString(4,description);
                preparedStatement.setString(5,code);
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.wtf("CANT SEND REPORT", throwables.getMessage());
                Log.wtf("CANT SEND REPORT", throwables.getLocalizedMessage());
            }
    }


    private void getIntentInfo(){
        Intent intent = getIntent();
        //строковые
        fio = intent.getStringExtra("fio");
        grNum =  intent.getStringExtra("group");
        //числовые
        posAll = intent.getIntExtra("position_general",-1);
        allPnts = intent.getIntExtra("points", -1);
        ownId = intent.getIntExtra("idAchiev", -1);
        instit_code = intent.getIntExtra("instit_code", -1);
        //логические
        isOwnerOfAccount = intent.getBooleanExtra("ownerOfAccount", false);

    }

    public static class AskUserDeleteDialog extends AppCompatDialogFragment{
        private final String codeForDelete;
        private final int ownIdForUpdating;
        public AskUserDeleteDialog(String codeForDelete, int ownIdForUpdating){
            this.codeForDelete = codeForDelete;
            this.ownIdForUpdating = ownIdForUpdating;
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
                            deleteImageFromSQLandDeletePoints(codeForDelete, ownIdForUpdating);
                            SQLSenderConnector senderConnector = new SQLSenderConnector();
                            int current_points = Integer.parseInt(senderConnector.sendQueryToSQLgetString("SELECT ALL_POINTS FROM STUDENT_DATA WHERE ACHIEVMENTS_ID = \'"+ownId+"\';"));
                            int position_general = Integer.parseInt(senderConnector.sendQueryToSQLgetString("SELECT RATE FROM(" +
                                    "SELECT *, ROW_NUMBER() OVER (ORDER BY ALL_POINTS DESC) AS RATE FROM STUDENT_DATA) as RT " +
                                    "WHERE ACHIEVMENTS_ID = " + ownId + " ;"));
                            Intent intent = new Intent(getContext(), ProfileUser.class);
                            intent.putExtra("fio", fio);
                            intent.putExtra("group", grNum);
                            intent.putExtra("points", current_points);
                            intent.putExtra("position_general", position_general);
                            intent.putExtra("idAchiev", ownId);
                            intent.putExtra("instit_code", instit_code);
                            intent.putExtra("ownerOfAccount", true);
                            startActivity(intent);
                        }
                    });

            return builder.create();
        }
        private void deleteImageFromSQLandDeletePoints(String code, int ownId){
            int pointsTODEL = checkPointsForDeleting(code);
            SQLSenderConnector connector = new SQLSenderConnector();
            Connection connection = connector.toOwnConnection();
            try{

                Statement statement = connection.createStatement();
                if (pointsTODEL > 0) {
                    CameraActivity tempClass = new CameraActivity();
                    tempClass.updateAllUserPoints(-pointsTODEL, ownId);
                    tempClass.updatePointsFromAchievs(-pointsTODEL, ownId);
                    statement.execute("DELETE FROM ACHIEVMENTS WHERE IMG_CODE = " + "\'" + code + "\'" + ";");
                    Toast.makeText(getContext(), "УСПЕШНО УДАЛЕНО", Toast.LENGTH_LONG);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.wtf("Cant delete IMG SQL", throwables.getMessage());
                Log.wtf("Cant delete IMG SQL", throwables.getLocalizedMessage());
            }
        }
        private int checkPointsForDeleting(String code){
            int pointsFromImg = 0;
            String typeOfAch = null;
            SQLSenderConnector connector = new SQLSenderConnector();
            Connection connection = connector.toOwnConnection();
            try{
                Statement statement = connection.createStatement();
                String query = "SELECT TYPE FROM ACHIEVMENTS WHERE IMG_CODE = "+"\'"+code+"\'"+";";
                ResultSet inf = statement.executeQuery(query);
                while (inf.next()) {
                    inf.getString(1);
                    typeOfAch = inf.getString("TYPE");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.wtf("Cant get IMG TYPE SQL", throwables.getMessage());
                Log.wtf("Cant get IMG TYPE SQL", throwables.getLocalizedMessage());
            }
            if(typeOfAch != null) {
                if (typeOfAch.equals("Благодарственное письмо")) {
                    pointsFromImg = 10;
                } else if (typeOfAch.equals("Сертификат")) {
                    pointsFromImg = 15;
                } else if (typeOfAch.equals("Грамота")) {
                    pointsFromImg = 15;
                } else if (typeOfAch.equals("Диплом")) {
                    pointsFromImg = 20;
                }
            }
            return pointsFromImg;
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
                            intent.putExtra("fio", fio);
                            intent.putExtra("group", grNum);
                            intent.putExtra("points", allPnts);
                            intent.putExtra("position_general", posAll);
                            intent.putExtra("idAchiev", ownId);
                            intent.putExtra("instit_code", instit_code);
                            intent.putExtra("ownerOfAccount", true);
                            startActivity(intent);
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
                watchAchType.setText(achievTypes.get(arrayIterator) + ": " + pointsList.get(arrayIterator));
            }
            else {
                showThisImage(arrayIterator);
                watchAchType.setText(achievTypes.get(arrayIterator) + ": " + pointsList.get(arrayIterator));
            }
        }

        if(!switchToNext && arrayIterator >= 0) {
            if (arrayIterator == 0) {
                arrayIterator = imageList.size()-1;
                showThisImage(arrayIterator);
                watchAchType.setText(achievTypes.get(arrayIterator) + ": " + pointsList.get(arrayIterator));

            } else {
                arrayIterator--;
                showThisImage(arrayIterator);
                watchAchType.setText(achievTypes.get(arrayIterator) + ": " + pointsList.get(arrayIterator));
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
    private ArrayList<String> getImageCodes(int idForSearchingCodes){
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
            ResultSet resultSet = statement.executeQuery("SELECT POINTS FROM ACHIEVMENTS WHERE OWN_ID_FOR_SEARCH = "+idForSearchPoints+";");
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
    private ArrayList<String> getTypesImage(int idForSearchPoints){
        ArrayList<String> tempArr = new ArrayList<>();
        SQLSenderConnector connector = new SQLSenderConnector();
        Connection connection = connector.toOwnConnection();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT TYPE FROM ACHIEVMENTS WHERE OWN_ID_FOR_SEARCH = "+idForSearchPoints+";");
            while (resultSet.next()){
                tempArr.add(resultSet.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.wtf("Cant get IMG POINTS SQL", throwables.getMessage());
            Log.wtf("Cant get IMG POINTS SQL", throwables.getLocalizedMessage());
        }
        return tempArr;
    }

}