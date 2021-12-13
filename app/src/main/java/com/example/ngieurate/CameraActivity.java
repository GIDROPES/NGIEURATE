package com.example.ngieurate;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    //private static final int PERMISSION_REQUEST_CAMERA = 83495;
    //ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    //YUVtoRGB translator = new YUVtoRGB();
    Bitmap photo;
    ImageView achImage;
    Button acceptImage;
    private static final int pic_id = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        achImage = findViewById(R.id.AchImage);
        acceptImage = findViewById(R.id.acceptImageButton);
        acceptImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);
                            if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            ==PackageManager.PERMISSION_GRANTED) {
                                //experimental
                                BitmapDrawable drawableBitmap =  (BitmapDrawable) achImage.getDrawable();
                                photo = drawableBitmap.getBitmap();
                                //experimental
                                createDirectoryAndSaveFile(photo,"bibibob1.jpg");
                                
                            }
                            else {
                                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);
                            }
            }
        });
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera_intent, pic_id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id){
            photo = (Bitmap)data.getExtras()
                    .get("data");
            achImage.setImageBitmap(photo);
        }
    }


    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {




            File direct = new File(Environment.getExternalStorageDirectory() + "/NGIEURATEPIC/");

            if (!direct.exists()) {
                File wallpaperDirectory = new File(Environment.getExternalStorageDirectory()+"/NGIEURATEPIC/");
                wallpaperDirectory.mkdirs();
            }

            File file = new File(Environment.getExternalStorageDirectory()+"/NGIEURATEPIC/",fileName);
            if (file.exists()) {
                file.delete();
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



    }
    /*
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            initCamera();
        }
    }
    /*
    /*
    private void initCamera(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider provider = cameraProviderFuture.get();
                    ImageAnalysis analysis = new ImageAnalysis.Builder()
                            .setTargetResolution(new Size(1080,1920))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();
                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();
                    analysis.setAnalyzer(ContextCompat.getMainExecutor(CameraActivity.this),
                            new ImageAnalysis.Analyzer() {
                                @Override
                                public void analyze(@NonNull ImageProxy image) {
                                    @SuppressLint("UnsafeOptInUsageError")
                                    Image img = image.getImage();
                                    Bitmap bitmap = translator.translateYUV(img, CameraActivity.this);
                                /*
                                    int size = bitmap.getWidth() * bitmap.getHeight();
                                    int[] pixels = new int[size];
                                    bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0,
                                            bitmap.getWidth(),bitmap.getHeight());
                                    for(int i = 0; i < size; i++){
                                        int color = pixels[i];
                                        int r = color >> 16 & 0xff;
                                        int g = color >> 8 & 0xff;
                                        int b = color & 0xff;
                                        int gray = (r + g + b) / 3;
                                        pixels[i] = 0xff000000 | gray << 16 | gray << 8 | gray;
                                    }
                                    bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0,
                                            bitmap.getWidth(),bitmap.getHeight());

                                    achImage.setRotation(image.getImageInfo().getRotationDegrees());
                                    achImage.setImageBitmap(bitmap);
                                    image.close();
                                }
                            });
                    provider.bindToLifecycle(CameraActivity.this,cameraSelector,analysis);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }
    */
}