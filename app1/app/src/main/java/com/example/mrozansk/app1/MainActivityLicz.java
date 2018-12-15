package com.example.mrozansk.app1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivityLicz extends AppCompatActivity {

    private int _counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_licz);
    }

    public void zwLicznik(View view){

        TextView tV = (TextView) findViewById(R.id.textView);
        _counter++;
        String s = String.valueOf(_counter);
        tV.setText(s);

    }

    public void zmLicznik(View view){

        TextView tV = (TextView) findViewById(R.id.textView);
        _counter--;
        String s = String.valueOf(_counter);
        tV.setText(s);

    }

    public void wyLicznik(View view){

        TextView tV = (TextView) findViewById(R.id.textView);
        _counter=0;
        String s = String.valueOf(_counter);
        tV.setText(s);

    }

}
