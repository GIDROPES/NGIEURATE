package com.example.ngieurate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    TextView myText, ngieuratingapp; Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        myText = findViewById(R.id.myText);
        ngieuratingapp = findViewById(R.id.ngieuratingapp);
        signUp = findViewById(R.id.signUp);
        signUp.setVisibility(View.INVISIBLE);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLSenderConnector connector = new SQLSenderConnector();
                connector.sendQueryCHANGING("UPDATE STUDENT_DATA SET ALL_POINTS = POINTS_FROM_ACHIEVS + POINTS_FROM_TEACHERS;");
                Intent intent = new Intent(MainActivity.this, LoginUser.class);
                MainActivity.this.startActivity(intent);
            }
        });
        AnimMainElements anim = new AnimMainElements();
        anim.execute();
    }
    class AnimMainElements extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Animation textFadeIn;
            textFadeIn = AnimationUtils.loadAnimation(MainActivity.this,R.anim.fadein);

            DelayedPrinter.Word word = new DelayedPrinter.Word(170, "XRAY");
            word.setOffset(100);
            DelayedPrinter.printText(word,myText);

            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DelayedPrinter.Word word1 = new DelayedPrinter.Word(170, "NGIEU RATING APP");
            word1.setOffset(100);
            DelayedPrinter.printText(word1,ngieuratingapp);

            signUp.startAnimation(textFadeIn);
            signUp.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}

class DelayedPrinter {

    public static void printText(final Word word, final TextView textView) {
        Random random = new Random(System.currentTimeMillis());

        int currentRandOffset = random.nextInt(word.offset);
        boolean addOrSubtract = random.nextBoolean();
        long finalDelay = addOrSubtract ? word.delayBetweenSymbols + currentRandOffset : word.delayBetweenSymbols - currentRandOffset;
        if (finalDelay < 0) finalDelay = 0;

        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                String charAt = String.valueOf(word.word.charAt(word.currentCharacterIndex));
                ++word.currentCharacterIndex;
                textView.setText(textView.getText() + charAt);
                if (word.currentCharacterIndex >= word.word.length()) {
                    word.currentCharacterIndex = 0;
                    return;
                }
                printText(word, textView);
            }
        }, finalDelay);
    }


    public static class Word {

        private long delayBetweenSymbols;
        private String word;
        private int offset;
        private int currentCharacterIndex;

        public Word(long delayBetweenSymbols, String word) {
            if (delayBetweenSymbols < 0) throw new IllegalArgumentException("Delay can't be < 0");
            this.delayBetweenSymbols = delayBetweenSymbols;
            this.word = word;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }
}