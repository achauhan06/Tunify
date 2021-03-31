package edu.neu.madcourse.numadsp21finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
        logIn();

    }

    private void logIn() {

        Button login = findViewById(R.id.main_button_logIn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Helper.isOnline(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, Helper.NO_INTERNET,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                EditText userEmailInput = findViewById(R.id.main_input_email);
                EditText userPasswordInput = findViewById(R.id.main_input_password);
                String email = userEmailInput.getText().toString();
                String password = userPasswordInput.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your email.",
                            Toast.LENGTH_SHORT).show();
                } if (password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your password.",
                            Toast.LENGTH_SHORT).show();
                }if (password.length() < 6) {
                    Toast.makeText(MainActivity.this, "Password must at least have 6 characters.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Helper.setEmailPassword(MainActivity.this, email, password);
                    logInService(email, password);

                }
            }
        });
        if(!Helper.isOnline(this)) {
            Toast.makeText(this, Helper.NO_INTERNET,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void logInService(String email, String password){
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Logged in successfully.",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Unable to log in.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}