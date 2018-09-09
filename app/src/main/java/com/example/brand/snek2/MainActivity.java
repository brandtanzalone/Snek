package com.example.brand.snek2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void play_game(View view){
        Button button = (Button) view;
        startActivity(new Intent(getApplicationContext(), Main2Activity.class));
    }

    public void high_scores(View view){
        Button b1 = (Button) view;
        startActivity(new Intent(getApplicationContext(), Main3Activity.class));
    }

}
