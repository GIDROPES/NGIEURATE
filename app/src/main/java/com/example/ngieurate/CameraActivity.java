package com.example.ngieurate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Size;
import android.widget.ImageView;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 83495;
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    YUVtoRGB translator = new YUVtoRGB();
    ImageView achImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        achImage = findViewById(R.id.AchImage);
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        }
        else { initCamera(); }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            initCamera();
        }
    }
    private void initCamera(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider provider = cameraProviderFuture.get();
                    ImageAnalysis analysis = new ImageAnalysis.Builder()
                            .setTargetResolution(new Size(768,1024))
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
}