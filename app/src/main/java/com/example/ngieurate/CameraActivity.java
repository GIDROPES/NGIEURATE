package com.example.ngieurate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class CameraActivity extends AppCompatActivity {

    // private Bitmap photo;
    private ImageView achImage;
    private Button acceptImage;
    private Spinner typesSpinner;
    private TextView youWillGetPoints;
    private SharedPreferences userData;
    private Integer points;
    private static final int pic_id = 1337;
    private Integer ownId;
    private String type;
    private Uri mImageUri;
    private  String fio, grNum;
    private  Integer instit_code, allPnts, posAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        readFromIntent();

        achImage = findViewById(R.id.AchImage);
        acceptImage = findViewById(R.id.acceptImageButton);
        typesSpinner = findViewById(R.id.typesSpinner);
        youWillGetPoints = findViewById(R.id.youWillGetPoints);

        youWillGetPoints.setVisibility(View.INVISIBLE);
        //код для выпадающего списка
        ArrayAdapter<?> adapterSpinner =
                ArrayAdapter.createFromResource(this, R.array.achievTypes,
                        android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typesSpinner.setAdapter(adapterSpinner);
        type = String.valueOf(typesSpinner.getSelectedItem());
        typesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_item = adapterView.getSelectedItem().toString();
                setTextViewAndPoints(selected_item); //своя функция для уточнения баллов
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //кликаем на "Подтвердить", логика только для щелчка!!!
        acceptImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    insertImageToSql(type);
                    updateAllUserPoints(points,ownId);
                    updatePointsFromAchievs(points,ownId);

                    SQLSenderConnector connector = new SQLSenderConnector();
                    allPnts = Integer.parseInt(connector.sendQueryToSQLgetString("SELECT ALL_POINTS FROM STUDENT_DATA WHERE ACHIEVMENTS_ID = "+ownId+";"));
                    posAll = Integer.parseInt(connector.sendQueryToSQLgetString("SELECT RATE FROM(" +
                            "SELECT *, ROW_NUMBER() OVER (ORDER BY ALL_POINTS DESC) AS RATE FROM STUDENT_DATA) as RT " +
                            "WHERE ACHIEVMENTS_ID = " + ownId + " ;"));
                    Intent intent = new Intent(CameraActivity.this, ProfileUser.class);
                    intent.putExtra("fio", fio);
                    intent.putExtra("group", grNum);
                    intent.putExtra("points", allPnts);
                    intent.putExtra("position_general", posAll);
                    intent.putExtra("idAchiev", ownId);
                    intent.putExtra("instit_code", instit_code);
                    intent.putExtra("ownerOfAccount", true);
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                }
      }
        });
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent camera_intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/pic.jpg"));
        //camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        camera_intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        mImageUri = Uri.fromFile(f);
        startActivityForResult(camera_intent, pic_id);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  if(resultCode != RESULT_CANCELED) {
        if (requestCode == pic_id) { //&& resultCode == RESULT_OK
            loadImageFromStorage(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/pic.jpg"), achImage);

                /*achImage.setImageDrawable(null);
                achImage.destroyDrawingCache();
                Bundle extras = data.getExtras();
                Bitmap imagebitmap = (Bitmap) extras.get("data");
                achImage.setImageBitmap(imagebitmap);
                */
            //Bitmap currentImage = (Bitmap) data.getExtras().get("data");
            //achImage.setImageBitmap(currentImage);
            //новый код
                /*
                try {
                    // place where to store camera taken picture
                    //photo = createTemporaryFile("picture", ".jpg");
                    //photo = createDirectoryAndSaveFile(currentImage, "hellopic.png"); //TODO: поработать над именем
                    //mImageUri = fromFile(photo);
                    //grabImage(achImage);
                    //loadImageFromStorage(mImageUri.getPath(), achImage);
                    //photo.delete();
                } catch (Exception e) {
                    Log.v("Err create temporary", "Can't create file to take picture!");
                    Log.v("Err create temporary", e.getMessage());

                    Toast.makeText(CameraActivity.this, "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG);
                    //return false;
                }
            */
        }
        //   }
    }

    private void loadImageFromStorage(String path, ImageView img) {

        try {
            File f = new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imgPicker);
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("Err loading IMG", e.getMessage());
        }

    }

    public void grabImage(ImageView imageView) {

        ContentResolver cr = this.getContentResolver();
        this.getContentResolver().notifyChange(mImageUri, null);
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("ERR LOADING IMG", "Failed to load", e);
        }
    }

    private void setTextViewAndPoints(String typeAchSelected) {
        youWillGetPoints.setVisibility(View.VISIBLE);

        if (typeAchSelected.equals("Благодарственное письмо")) {
            youWillGetPoints.setText("Вы получите 10 баллов");
            points = 10;
            type = "Благодарственное письмо";
        }
        if (typeAchSelected.equals("Сертификат")) {
            youWillGetPoints.setText("Вы получите 15 баллов");
            points = 15;
            type = "Сертификат";
        }
        if (typeAchSelected.equals("Грамота")) {
            youWillGetPoints.setText("Вы получите 15 баллов");
            points = 15;
            type = "Грамота";
        }
        if (typeAchSelected.equals("Диплом")) {
            youWillGetPoints.setText("Вы получите 20 баллов");
            points = 20;
            type = "Диплом";
        }

    }

    private void readFromIntent() {
        Intent intent = getIntent();
        fio = intent.getStringExtra("fio");
        grNum =  intent.getStringExtra("group");
        //числовые
        ownId = intent.getIntExtra("idAchiev", -1);
        instit_code = intent.getIntExtra("instit_code", -1);
        //логические
    }

    private void insertImageToSql(String typeOfAch) {
        //TODO:не забыть получать ID
        //TODO:реализовать назначение поинтов, а точнее их инициализацию во время выбора типа ачивки
        //TODO:не забыть включить поинты в запрос
        char[] alphabet = {'A', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        Random random = new Random();
        String code = String.valueOf(random.nextInt(999))
                + String.valueOf(alphabet[random.nextInt(alphabet.length - 1)])
                + String.valueOf(alphabet[random.nextInt(alphabet.length - 1)])
                + String.valueOf(random.nextInt(999))
                + String.valueOf(alphabet[random.nextInt(alphabet.length - 1)])
                + String.valueOf(alphabet[random.nextInt(alphabet.length - 1)]);
        //
        String myPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/pic.jpg";
        Bitmap bitmapToSql = BitmapFactory.decodeFile(myPath);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmapToSql.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageToSql = outputStream.toByteArray();
        SQLSenderConnector senderConnector = new SQLSenderConnector();
        Connection connection = senderConnector.toOwnConnection();
        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ACHIEVMENTS VALUES(?,?,?,?,?)");
                preparedStatement.setInt(1, ownId);
                preparedStatement.setString(2, typeOfAch);
                preparedStatement.setInt(3, points);
                preparedStatement.setBytes(4, imageToSql);
                preparedStatement.setString(5, code);
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.wtf("Cant INSERT to SQL", throwables.getMessage());
                Log.wtf("Cant INSERT to SQL", throwables.getLocalizedMessage());
            }
        }
        //senderConnector.sendQueryCHANGING("INSERT INTO ACHIEVMENTS VALUES("+ownId+",\'"+typeOfAch+"\',"+points+","+ownCounter+","+imageToSql+");");
    }

    public boolean updateAllUserPoints(int plusPoints, int ownAchievID) {
        boolean result = false;
        SQLSenderConnector senderConnector = new SQLSenderConnector();
        Connection connection = senderConnector.toOwnConnection();
        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE STUDENT_DATA SET ALL_POINTS = ALL_POINTS + "+plusPoints+ "WHERE ACHIEVMENTS_ID ="+ownAchievID+ ";");
                preparedStatement.executeQuery();
                result = true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.wtf("Cant UPDATE POINTS", throwables.getMessage());
                Log.wtf("Cant UPDATE POINTS", throwables.getLocalizedMessage());
                result = false;
            }
        }
        return result;
    }
    public boolean updatePointsFromAchievs(int plusPoints, int ownAchievID){
        boolean result = false;
        SQLSenderConnector senderConnector = new SQLSenderConnector();
        Connection connection = senderConnector.toOwnConnection();
        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE STUDENT_DATA SET POINTS_FROM_ACHIEVS = POINTS_FROM_ACHIEVS + "+plusPoints+ "WHERE ACHIEVMENTS_ID ="+ownAchievID+ ";");
                preparedStatement.executeQuery();
                result = true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.wtf("Cant UPDATE POINTS", throwables.getMessage());
                Log.wtf("Cant UPDATE POINTS", throwables.getLocalizedMessage());
                result = false;
            }
        }
        return result;
    }
}