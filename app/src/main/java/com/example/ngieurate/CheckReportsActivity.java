package com.example.ngieurate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CheckReportsActivity extends AppCompatActivity {
    private ImageView imagesReported;
    private Button prevReportedIMG, nextReportedIMG, blockReported, checkReportedInfo, denyReport;
    private TextView reportedType;
    private ArrayList<byte[]> reportedArray;
    private int arrayIterator = 0;
    private ArrayList<String> reportedTypes = new ArrayList<>(), descriptions = new ArrayList<>(), reportedIMGCODES = new ArrayList<>();
    private ArrayList<Integer> idReportedStudents = new ArrayList<>();
    private int pointsOfReported;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_reports);

        prevReportedIMG = findViewById(R.id.previousReportedImage);
        nextReportedIMG = findViewById(R.id.nextReportedImage);
        blockReported = findViewById(R.id.blockReported);
        checkReportedInfo = findViewById(R.id.checkReportedInfo);
        denyReport = findViewById(R.id.denyReport);

        imagesReported = findViewById(R.id.reportedImages);

        reportedType = findViewById(R.id.reportedType);

        getOtherReportedInfo();
        reportedArray = fillReportedImagesArray();

        if (reportedArray.isEmpty() && reportedTypes.isEmpty()) {
            imagesReported.setImageResource(R.drawable.images_empty);
        } else {
            showReportedImage(arrayIterator);
            textViewTypeSetter(reportedTypes.get(arrayIterator));
        }

        nextReportedIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!reportedArray.isEmpty()) {
                    reportedImagesViewer(true);
                }
            }
        });
        prevReportedIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!reportedArray.isEmpty()) {
                    reportedImagesViewer(false);
                }
            }
        });
        blockReported.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reportedArray.isEmpty()) {
                    AskDeleteReportedRow delete = new AskDeleteReportedRow(reportedIMGCODES.get(arrayIterator), idReportedStudents.get(arrayIterator), pointsOfReported);
                    delete.show(getSupportFragmentManager(),"askDeleteReportDialog");
                }
            }
        });
        denyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                denyReport(idReportedStudents.get(arrayIterator), reportedIMGCODES.get(arrayIterator));
            }
        });

    }

    private void denyReport(int id_for_deleting, String img_code){
        SQLSenderConnector senderConnector = new SQLSenderConnector();
        senderConnector.sendQueryCHANGING("DELETE FROM REPORTS WHERE ID_REPORTED_STUDENT = "+id_for_deleting+" AND REPORTED_IMG_CODE = \'"+img_code+"\';");
    }

    private void showReportedImage(int iterator) {
        byte[] forShowingArr = reportedArray.get(iterator);
        Bitmap bitmap = BitmapFactory.decodeByteArray(forShowingArr, 0, forShowingArr.length);
        imagesReported.setImageBitmap(bitmap);
    }

    private ArrayList<byte[]> fillReportedImagesArray() {
        ArrayList<byte[]> tempArr = new ArrayList<>();
        SQLSenderConnector connector = new SQLSenderConnector();
        Connection connection = connector.toOwnConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT REPORTED_IMAGE FROM REPORTS;");
            while (resultSet.next()) {
                tempArr.add(resultSet.getBytes(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.wtf("Cant get IMG SQL", throwables.getMessage());
            Log.wtf("Cant get IMG SQL", throwables.getLocalizedMessage());
        }
        return tempArr;
    }

    private void reportedImagesViewer(boolean switchToNext) {
        if (switchToNext && arrayIterator < reportedArray.size()) {
            arrayIterator++;
            if (arrayIterator == reportedArray.size()) {
                arrayIterator = 0;
                showReportedImage(arrayIterator);
                textViewTypeSetter(reportedTypes.get(arrayIterator));
            } else {
                showReportedImage(arrayIterator);
                textViewTypeSetter(reportedTypes.get(arrayIterator));
            }
        }

        if (!switchToNext && arrayIterator >= 0) {
            if (arrayIterator == 0) {
                arrayIterator = reportedArray.size() - 1;
                showReportedImage(arrayIterator);
                textViewTypeSetter(reportedTypes.get(arrayIterator));
            } else {
                arrayIterator--;
                showReportedImage(arrayIterator);
                textViewTypeSetter(reportedTypes.get(arrayIterator));
            }
        }
    }

    private void getOtherReportedInfo() {
        SQLSenderConnector connector = new SQLSenderConnector();
        Connection connection = connector.toOwnConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT ID_REPORTED_STUDENT FROM REPORTS;");
            while (resultSet.next()) {
                idReportedStudents.add(resultSet.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.wtf("CANT GET FROM REPORTS", throwables.getMessage());
            Log.wtf("CANT GET FROM REPORTS", throwables.getLocalizedMessage());
        }
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT REPORTED_TYPE FROM REPORTS;");
            while (resultSet.next()) {
                reportedTypes.add(resultSet.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.wtf("CANT GET FROM REPORTS", throwables.getMessage());
            Log.wtf("CANT GET FROM REPORTS", throwables.getLocalizedMessage());
        }
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DESCRIPTION FROM REPORTS;");
            while (resultSet.next()) {
                descriptions.add(resultSet.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.wtf("CANT GET FROM REPORTS", throwables.getMessage());
            Log.wtf("CANT GET FROM REPORTS", throwables.getLocalizedMessage());
        }
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT REPORTED_IMG_CODE FROM REPORTS;");
            while (resultSet.next()) {
                reportedIMGCODES.add(resultSet.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.wtf("CANT GET FROM REPORTS", throwables.getMessage());
            Log.wtf("CANT GET FROM REPORTS", throwables.getLocalizedMessage());
        }
    }

    private void textViewTypeSetter(String typeOfAch){
        if(typeOfAch != null) {
            if (typeOfAch.equals("Благодарственное письмо")) {
                reportedType.setText(reportedTypes.get(arrayIterator) +": "+10);
                pointsOfReported = 10;
            } else if (typeOfAch.equals("Сертификат")) {
                reportedType.setText(reportedTypes.get(arrayIterator) +": "+15);
                pointsOfReported = 15;
            } else if (typeOfAch.equals("Грамота")) {
                reportedType.setText(reportedTypes.get(arrayIterator) +": "+15);
                pointsOfReported = 15;
            } else if (typeOfAch.equals("Диплом")) {
                reportedType.setText(reportedTypes.get(arrayIterator) +": "+20);
                pointsOfReported = 20;
            }
        }
    }




    public static class AskDeleteReportedRow extends AppCompatDialogFragment {
        private final String reportedImageCode;
        private final int ownIdToDeletePoints;
        private final int pointsToDelete;
        public AskDeleteReportedRow(String reportedImageCode, int ownIdToDeletePoints, int pointsToDelete){
            this.reportedImageCode = reportedImageCode;
            this.ownIdToDeletePoints = ownIdToDeletePoints;
            this.pointsToDelete = pointsToDelete;
        }
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Принять жалобу")
                    .setMessage("Вы действительно хотите удалить это фото и лишить студента(-ку) баллов?")
                    .setIcon(R.drawable.basket_icon)
                    .setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("ПРИНЯТЬ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteImageFromSQLandDeletePoints(reportedImageCode, ownIdToDeletePoints, pointsToDelete);
                            Intent intent = new Intent(getContext(), CheckReportsActivity.class);
                            startActivity(intent);
                        }
                    });

            return builder.create();
        }
        private void deleteImageFromSQLandDeletePoints(String code, int ownId, int pointsDEL){
            int pointsTODEL = pointsDEL;
            SQLSenderConnector connector = new SQLSenderConnector();
            Connection connection = connector.toOwnConnection();
            try{
                Statement statement = connection.createStatement();
                if (pointsTODEL > 0) {
                    CameraActivity tempClass = new CameraActivity();
                    tempClass.updateAllUserPoints(-pointsDEL, ownId);
                    tempClass.updatePointsFromAchievs(-pointsDEL, ownId);
                    statement.execute("DELETE FROM ACHIEVMENTS WHERE IMG_CODE = " + "\'" + code + "\'" + ";");
                    statement.execute("DELETE FROM REPORTS WHERE REPORTED_IMG_CODE = \'"+code+"\';");
                    Toast.makeText(getContext(), "УСПЕШНО УДАЛЕНО", Toast.LENGTH_LONG);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.wtf("Cant delete IMG SQL", throwables.getMessage());
                Log.wtf("Cant delete IMG SQL", throwables.getLocalizedMessage());
            }
        }

    }


}