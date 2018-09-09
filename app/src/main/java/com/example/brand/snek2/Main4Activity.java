package com.example.brand.snek2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
    }

    public void backToGame(View view){
        Button button2 = (Button) view;
        startActivity(new Intent(getApplicationContext(), Main2Activity.class));
    }

}
