package com.example.mrozansk.kalkulator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    LogicService logicService;
    boolean mBound = false;
    private ServiceConnection logicConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            LogicService.LocalBinder binder = (LogicService.LocalBinder) service;
            logicService = binder.getService();
            mBound = true;
            Toast.makeText(MainActivity.this, "Logic Service Connected!",
                    Toast.LENGTH_SHORT).show();
        }
        public void onServiceDisconnected(ComponentName className) {
            logicService = null;
            mBound = false;
            Toast.makeText(MainActivity.this, "Logic Service Disconnected!",
                    Toast.LENGTH_SHORT).show();
        }
    };

    EditText n1EditText, n2EditText, resEditText;
    Button addButton, subButton, mulButton, divButton;
    Double n1 = 0.0;
    Double n2 = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        n1EditText = (EditText) findViewById(R.id.n1EditText);
        n2EditText = (EditText) findViewById(R.id.n2EditText);
        //n1 = Double.parseDouble(n1EditText.getText().toString());
        //n2 = Double.parseDouble(n1EditText.getText().toString());

        resEditText = (EditText) findViewById(R.id.resEditText);

        addButton = (Button) findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){
                    n1 = Double.parseDouble(n1EditText.getText().toString());
                    n2 = Double.parseDouble(n2EditText.getText().toString());
                    double add = logicService.add(n1,n2);

                    resEditText.setText(String.valueOf(add));

                }
            }
        });

        subButton = (Button) findViewById(R.id.subButton);

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){

                    n1 = Double.parseDouble(n1EditText.getText().toString());
                    n2 = Double.parseDouble(n2EditText.getText().toString());
                    double sub = logicService.sub(n1,n2);

                    resEditText.setText(String.valueOf(sub));

                }
            }
        });

        mulButton = (Button) findViewById(R.id.mulButton);

        mulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){

                    n1 = Double.parseDouble(n1EditText.getText().toString());
                    n2 = Double.parseDouble(n2EditText.getText().toString());
                    double mul = logicService.mul(n1,n2);

                    resEditText.setText(String.valueOf(mul));
                }
            }
        });

        divButton = (Button) findViewById(R.id.divButton);

        divButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){

                    n1 = Double.parseDouble(n1EditText.getText().toString());
                    n2 = Double.parseDouble(n2EditText.getText().toString());
                    double div = logicService.div(n1,n2);

                    resEditText.setText(String.valueOf(div));
                }
            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            this.bindService(new Intent(MainActivity.this,LogicService.class),
                    logicConnection,Context.BIND_AUTO_CREATE);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            mBound = false;
            this.unbindService(logicConnection);
        }
    }

}
