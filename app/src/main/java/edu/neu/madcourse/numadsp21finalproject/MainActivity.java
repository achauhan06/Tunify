package edu.neu.madcourse.numadsp21finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = findViewById(R.id.login_btn);
        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), HomeActivity.class);
            startActivity(intent);
        });
    }
}