package com.example.mrozansk.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SimpleChatMainActivity extends AppCompatActivity {

    public static String IP="ip";
    public static String NICK="nick";

    Button startButton;
    EditText ipEditText, nickEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_chat_main);

        startButton = (Button) findViewById(R.id.startButton);
        ipEditText = (EditText) findViewById(R.id.ipEditText);
        nickEditText = (EditText) findViewById(R.id.nickEditText);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SimpleChatMainActivity.this, SimpleChatActivity.class);
                intent.putExtra(IP, ipEditText.getText().toString());
                intent.putExtra(NICK, nickEditText.getText().toString());
                startActivity(intent);
            }
        });

    }
}
