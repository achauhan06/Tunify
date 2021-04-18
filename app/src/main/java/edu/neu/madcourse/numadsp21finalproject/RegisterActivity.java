package edu.neu.madcourse.numadsp21finalproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import edu.neu.madcourse.numadsp21finalproject.users.UserItem;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class RegisterActivity extends AppCompatActivity {
    EditText dateEditText;
    Button reg_registration;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    EditText firstName;
    EditText lastName;
    EditText dob;
    EditText email;
    EditText password;
    EditText passwordConfirmation;
    TextView genres;
    TextView tvSelectedItemsPreview;
    String userId;
    List<String> selectedGenres;
    UserItem user;
    int counttracker = 0;
    int count = 0;
    MyFirebaseInstanceMessagingService firebaseInstanceMessagingService;
    //String token = "";
    private static final String TAG = RegisterActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        user = new UserItem();
        firebaseFirestore = FirebaseFirestore.getInstance();
        dateEditText = (EditText) findViewById(R.id.register_dob);
        reg_registration = findViewById(R.id.register);
        firstName = findViewById(R.id.register_first_name);
        lastName = findViewById(R.id.register_last_name);
        dob = findViewById(R.id.register_dob);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        passwordConfirmation = findViewById(R.id.register_passwordconfirmation);
        genres = findViewById(R.id.selectedItemPreview);
        auth = FirebaseAuth.getInstance();
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month,
                                          int day) {


                        String dateFormat = day+ "/" + (month + 1) +"/" +year;
                        dateEditText.setText("" + dateFormat);
                    }
                };
                final Calendar newCalendar = Calendar.getInstance();
                int[] dateArray = new int[]{newCalendar.get(Calendar.YEAR),
                        newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)};
                if(!dateEditText.getText().toString().isEmpty()) {
                    String[] dateInStringArray =
                            dateEditText.getText().toString().split("/");
                    dateArray = new int[]{Integer.parseInt(dateInStringArray[2]),
                            Integer.parseInt(dateInStringArray[1])-1,
                            Integer.parseInt(dateInStringArray[0])};
                }

                DatePickerDialog d = new DatePickerDialog(RegisterActivity.this, dpd,
                        dateArray[0], dateArray[1], dateArray[2]);
                d.show();
            }
        });



        EditText bOpenAlertDialog = findViewById(R.id.openAlertDialogButton);
        tvSelectedItemsPreview = (TextView) findViewById(R.id.selectedItemPreview);
        //tvSelectedItemsPreview.setOnClickListener(this);
        if (count == 0) tvSelectedItemsPreview.setVisibility(View.INVISIBLE);


        final String[] listItems = Helper.CATEGORY_LIST;
        final boolean[] checkedItems = new boolean[listItems.length];

        final List<String> selectedItems = Arrays.asList(listItems);

        bOpenAlertDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvSelectedItemsPreview.setText(null);

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

                builder.setTitle("Choose any 3 genres:");


                builder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    //int count = 0;
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked && count < 3) {
                            count++;
                            checkedItems[which] = isChecked;
                            String currentItem = selectedItems.get(which);
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Only 3 genres can be selected at this point", Toast.LENGTH_SHORT).show();
                            ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                            checkedItems[which]=false;
                        }
                        if (!isChecked) count--;
                    }
                });

                builder.setCancelable(false);

                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedGenres = new ArrayList<>();
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                selectedGenres.add(selectedItems.get(i));
                                bOpenAlertDialog.setVisibility(View.INVISIBLE);
                            }
                        }
                        if (selectedGenres.size() > 0) {
                            tvSelectedItemsPreview.setText(String.join(", ",selectedGenres));
                            tvSelectedItemsPreview.setVisibility(View.VISIBLE);
                        }
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                        }
                        count = 0;
                    }
                });

                builder.setNeutralButton("CLEAR ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                        }
                        count = 0;
                    }
                });

                builder.create();

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        tvSelectedItemsPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Toast.makeText(RegisterActivity.this, "Opening", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);

                builder1.setTitle("Choose any 3 genres:");

                builder1.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {


                    //tvSelectedItemsPreview.setVisibility(View.INVISIBLE);
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked && count < 3) {
                            count++;
                            checkedItems[which] = isChecked;
                            String currentItem = selectedItems.get(which);
                        }
                        else {
                            //Toast.makeText(RegisterActivity.this, "Only 3 genres can be selected at this point", Toast.LENGTH_SHORT).show();
                            ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                            checkedItems[which]=false;
                        }
                        if (!isChecked) {
                            count--;
                            if (count == 0) {
                                tvSelectedItemsPreview.setVisibility(View.INVISIBLE);
                                bOpenAlertDialog.setVisibility(View.VISIBLE);
                            }
                        }
                        Toast.makeText(RegisterActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();
                    }
                });

                builder1.setCancelable(false);

                builder1.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedGenres = new ArrayList<>();
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                selectedGenres.add(selectedItems.get(i));
                                bOpenAlertDialog.setVisibility(View.INVISIBLE);
                            }
                        }
                        tvSelectedItemsPreview.setText(String.join(", ",selectedGenres));
                    }
                });

                builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder1.setNeutralButton("CLEAR ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                        }
                    }
                });
                builder1.create();

                AlertDialog alertDialog = builder1.create();
                alertDialog.show();
            }
        });
        reg_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstName.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "Please enter your First Name", Toast.LENGTH_SHORT).show();

                } else if (email.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "Please type an email id", Toast.LENGTH_SHORT).show();

                } else if (dob.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "Please enter a DOB", Toast.LENGTH_SHORT).show();

                } else if (lastName.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "Please type a last name", Toast.LENGTH_SHORT).show();

                } else if (password.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this, "Please type a password", Toast.LENGTH_SHORT).show();

                } else if (!passwordConfirmation.getText().toString().equals(password.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Password mismatch", Toast.LENGTH_SHORT).show();

                } else {
                    auth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener((task) -> {
                        if (task.isSuccessful()) {
                            userId = auth.getCurrentUser().getUid();
                            DocumentReference reference1 = firebaseFirestore.collection("users").document(userId);
                            Map<String, Object> reg_entry = new HashMap<>();
                            reg_entry.put("First Name", firstName.getText().toString());
                            reg_entry.put("Last Name", lastName.getText().toString());
                            reg_entry.put("Email", email.getText().toString());
                            reg_entry.put("Genres", String.join(";",selectedGenres));
                            reg_entry.put("Password", password.getText().toString());
                            reg_entry.put("Date of Birth", dob.getText().toString());
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(task1 -> {
                                        if (!task1.isSuccessful()) {
                                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }
                                        // Get new FCM registration token
                                        String token = task1.getResult();
                                        user.setToken(token);
                                        Toast.makeText(RegisterActivity.this, token, Toast.LENGTH_SHORT).show();
                                        reg_entry.put("Mobile Token", user.getToken());
                                        reference1.set(reg_entry).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RegisterActivity.this, "User Successfully registered!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                        //sendRegistrationToServer(token,context);
                                        //reg_entry.put("Mobile Token", token);

                                        //openNewActivity("User created successfully", view, context);
                                    });
                            //Toast.makeText(RegisterActivity.this, "Token : " +user.getToken(), Toast.LENGTH_SHORT).show();
                            //firebaseInstanceMessagingService.sendRegistrationToServer(reg_entry);

                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}
