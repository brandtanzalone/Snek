package com.example.brand.snek2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

public class Main3Activity extends AppCompatActivity {
    public static int highscore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        TextView scoreTextView = (TextView)findViewById(R.id.score);
        scoreTextView.setText(String.valueOf(highscore));
    }
}
