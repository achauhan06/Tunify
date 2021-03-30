package edu.neu.madcourse.numadsp21finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class MainActivity extends AppCompatActivity {

    private EditText userNameInput;
    private EditText userPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = findViewById(R.id.main_button_logIn);
        if (Helper.getLoggedIn(this)) {
            logIn(login, Helper.getUserName(this), Helper.getPassword(this));
        }else {
            Toast.makeText(this, "Please register or sign in", Toast.LENGTH_SHORT).show();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Helper.isOnline(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, Helper.NO_INTERNET,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                userNameInput = findViewById(R.id.main_input_username);
                userPasswordInput = findViewById(R.id.main_input_password);
                String userName = userNameInput.getText().toString();
                String password = userPasswordInput.getText().toString();
                if (userName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your user name.",
                            Toast.LENGTH_SHORT).show();
                } if (password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your password.",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Helper.setUserNamePassword(MainActivity.this, userName, password);
                    logIn(view, userName, password);

                }
            }
        });
        if(!Helper.isOnline(this)) {
            Toast.makeText(this, Helper.NO_INTERNET,
                    Toast.LENGTH_SHORT).show();
        }


    }

    private void logIn(View view, String userName, String password) {

    }
}