package com.example.rahmaniansyahdp.lokassiv3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Intent intent ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void klikButton(View v){
        intent = new Intent(this, Main2Activity.class) ;
        startActivity(intent);
        finish();
    }

}
