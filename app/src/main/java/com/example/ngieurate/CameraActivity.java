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

public class CameraActivity extends AppCompatActivity {

   // private Bitmap photo;
    private ImageView achImage;
    private Button acceptImage;
    private Spinner typesSpinner;
    private TextView youWillGetPoints;
    private SharedPreferences userData;
    private Integer ownCounter;
    private Integer points;
    private static final int pic_id = 1337;
    private Integer ownId; private String type;
    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        readFromPreferenceNow();

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
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);
                            if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            ==PackageManager.PERMISSION_GRANTED) {
                                insertImageToSql(type);
                                Intent intent = new Intent(CameraActivity.this, ProfileUser.class);
                                startActivity(intent);
                            }
                            else {
                                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);
                            }
            }
        });
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent camera_intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/pic.jpg"));
        //camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        camera_intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        mImageUri = Uri.fromFile(f);
        startActivityForResult(camera_intent, pic_id);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //  if(resultCode != RESULT_CANCELED) {
            if (requestCode == pic_id ) { //&& resultCode == RESULT_OK
                loadImageFromStorage(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/pic.jpg") , achImage);

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

    private void insertIntoSql(){
        SQLSenderConnector connector = new SQLSenderConnector();
        Connection connection = connector.toOwnConnection();
    }
    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(String.valueOf(Environment.DIRECTORY_PICTURES))));
        if(!tempDir.exists())
        {
            tempDir.mkdir();
        }
        File img = null;
        try {
            img = File.createTempFile(part, ext, tempDir);
        }
        catch (Exception ex){
            Log.d("temp file ERR log: ", ex.getMessage());
        }

        return img;
    }

    private void loadImageFromStorage(String path, ImageView img)
    {

        try {
            File f=new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imgPicker);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Log.d("Err loading IMG", e.getMessage());
        }

    }

    public void grabImage(ImageView imageView)
    {

        ContentResolver cr = this.getContentResolver();
        this.getContentResolver().notifyChange(mImageUri, null);
        Bitmap bitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            imageView.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("ERR LOADING IMG", "Failed to load", e);
        }
    }

    private void setTextViewAndPoints(String typeAchSelected){
        youWillGetPoints.setVisibility(View.VISIBLE);

        if (typeAchSelected.equals("Благодарственное письмо")){
            youWillGetPoints.setText("Вы получите 10 баллов");
            points = 10;
            type = "Благодарственное письмо";
        }
        if (typeAchSelected.equals("Сертификат")){
            youWillGetPoints.setText("Вы получите 15 баллов");
            points = 15;
            type = "Сертификат";
        }
        if (typeAchSelected.equals("Грамота")){
            youWillGetPoints.setText("Вы получите 15 баллов");
            points = 15;
            type = "Грамота";
        }
        if (typeAchSelected.equals("Диплом")){
            youWillGetPoints.setText("Вы получите 20 баллов");
            points = 20;
            type = "Диплом";
        }

    }

    private void readFromPreferenceNow(){
        ((Runnable) () -> {
            userData = getSharedPreferences(LoginUser.APP_PREFERENCES, MODE_PRIVATE);
            ownId = userData.getInt(LoginUser.APP_PREFERENCES_OWN_ID_ACHIEV, 0);
            //ownCounter = userData.getInt(LoginUser.APP_PREFERENCES_OWN_COUNTER, 0);
        }).run();
    }

    private File createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/NGIEURATEPIC/");

        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory()+"/NGIEURATEPIC/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(Environment.getExternalStorageDirectory()+"/NGIEURATEPIC/",fileName);
        if (file.exists()) {
            file.delete();
        }
        else{
            file.mkdir();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ErrorSave", e.getMessage());
        }

        return file;
    }

    private void insertImageToSql(String typeOfAch){
        //TODO:не забыть получать ID
        //TODO:реализовать назначение поинтов, а точнее их инициализацию во время выбора типа ачивки
        //TODO:не забыть включить поинты в запрос

        String myPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/pic.jpg";
        Bitmap bitmapToSql = BitmapFactory.decodeFile(myPath);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmapToSql.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imageToSql = outputStream.toByteArray();
        SQLSenderConnector senderConnector = new SQLSenderConnector();
        Connection connection = senderConnector.toOwnConnection();
        if(connection!=null){
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ACHIEVMENTS VALUES(?,?,?,?)");
                preparedStatement.setInt(1, ownId);
                preparedStatement.setString(2,typeOfAch);
                preparedStatement.setInt(3,points);
                preparedStatement.setBytes(4, imageToSql);
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.wtf("Cant INSERT to SQL", throwables.getMessage());
                Log.wtf("Cant INSERT to SQL", throwables.getLocalizedMessage());
            }
        }
        //senderConnector.sendQueryCHANGING("INSERT INTO ACHIEVMENTS VALUES("+ownId+",\'"+typeOfAch+"\',"+points+","+ownCounter+","+imageToSql+");");
    }

}