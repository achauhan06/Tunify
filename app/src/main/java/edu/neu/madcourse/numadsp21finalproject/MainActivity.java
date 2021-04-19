package edu.neu.madcourse.numadsp21finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.LibraryActivity;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.LibraryItem;
import edu.neu.madcourse.numadsp21finalproject.model.User;
import edu.neu.madcourse.numadsp21finalproject.service.FirebaseInstanceMessagingService;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    Button register;

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
        register = findViewById(R.id.main_button_register);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startRegisterActivity();
            }
        }

        );

    }

    private void startRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
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
                    Helper.db.collection("users")
                            .whereEqualTo("Email", email)
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()) {
                                    QuerySnapshot documentSnapshot = task1.getResult();
                                    String username = documentSnapshot.getDocuments()
                                            .get(0).get("Username").toString();
                                    Helper.setEmailPassword(MainActivity.this,
                                            email, password, username);
                                    String mobileToken = documentSnapshot.getDocuments()
                                            .get(0).get("MobileToken") != null
                                            ? documentSnapshot.getDocuments()
                                            .get(0).get("MobileToken").toString()
                                            : null;
                                    UserService userService = new UserService() {
                                        @Override
                                        public void register(String userToken) {
                                        }

                                        @Override
                                        public void updateToken(String refreshToken) {
                                            documentSnapshot.getDocuments()
                                                    .get(0).getReference()
                                                    .update("MobileToken", refreshToken);
                                            Helper.setUserToken(MainActivity.this, refreshToken);
                                        }
                                    };
                                    FirebaseInstanceMessagingService.login(userService, mobileToken);
                                    Helper.setUserToken(MainActivity.this, mobileToken);
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                } else {
                                    Log.d("firebase", "Error getting library items", task1.getException());
                                }

                            });
                } else {
                    Toast.makeText(MainActivity.this, "Unable to log in.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}