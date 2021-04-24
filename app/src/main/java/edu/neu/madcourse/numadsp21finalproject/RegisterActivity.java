package edu.neu.madcourse.numadsp21finalproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.service.FirebaseInstanceMessagingService;
import edu.neu.madcourse.numadsp21finalproject.users.UserItem;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class RegisterActivity extends AppCompatActivity {
    EditText dateEditText;
    Button reg_registration;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    EditText userName;
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
    boolean answer = false;

    FirebaseInstanceMessagingService firebaseInstanceMessagingService;
    int count = 0;
    private BroadcastReceiver myBroadcastReceiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        user = new UserItem();
        firebaseFirestore = FirebaseFirestore.getInstance();
        dateEditText = findViewById(R.id.register_dob);
        reg_registration = findViewById(R.id.register);
        userName = findViewById(R.id.register_username);
        firstName = findViewById(R.id.register_first_name);
        lastName = findViewById(R.id.register_last_name);
        dob = findViewById(R.id.register_dob);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        passwordConfirmation = findViewById(R.id.register_passwordconfirmation);
        genres = findViewById(R.id.selectedItemPreview);
        auth = FirebaseAuth.getInstance();
        userName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newUserName = userName.getText().toString();
                if (newUserName.matches("[A-Za-z0-9_]+")) {
                    checkingIfUsernameExists(newUserName);
                } else {
                    CustomToast customToast = new CustomToast(RegisterActivity.this,
                            "Username can only consist of alphabets, digits and " +
                                    "underscore", Snackbar.LENGTH_SHORT);
                    customToast.makeCustomToast();
                    userName.setText("");
                }

            }
        });
        dateEditText.setOnClickListener(v -> {
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
        });



        EditText bOpenAlertDialog = findViewById(R.id.openAlertDialogButton);
        tvSelectedItemsPreview = (TextView) findViewById(R.id.selectedItemPreview);
        if (count == 0) tvSelectedItemsPreview.setVisibility(View.INVISIBLE);


        final String[] listItems = Helper.CATEGORY_LIST;
        final boolean[] checkedItems = new boolean[listItems.length];

        final List<String> selectedItems = Arrays.asList(listItems);

        bOpenAlertDialog.setOnClickListener(v -> {

            AlertDialog alertDialog = createCategoryDialog(listItems, checkedItems,
                    selectedItems, bOpenAlertDialog).create();
            alertDialog.show();
        });

        bOpenAlertDialog.setOnFocusChangeListener((view, hasFocus) -> {

            tvSelectedItemsPreview.setText(null);
            if (hasFocus) {
                AlertDialog alertDialog = createCategoryDialog(listItems, checkedItems,
                        selectedItems, bOpenAlertDialog).create();
                alertDialog.show();
            }


        });

        tvSelectedItemsPreview.setOnClickListener(view -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);

            builder1.setTitle("Choose any 3 genres:");

            builder1.setMultiChoiceItems(listItems, checkedItems, (dialog, which, isChecked) -> {
                if (isChecked && count < 3) {
                    count++;
                    checkedItems[which] = isChecked;
                }
                else {
                    Toast.makeText(RegisterActivity.this,
                            "Only 3 genres can be selected at this point", Toast.LENGTH_SHORT)
                            .show();
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
            });

            builder1.setCancelable(false);

            builder1.setPositiveButton("Done", (dialog, which) -> {
                selectedGenres = new ArrayList<>();
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        selectedGenres.add(selectedItems.get(i));
                        bOpenAlertDialog.setVisibility(View.INVISIBLE);
                    }
                }
                tvSelectedItemsPreview.setText(String.join(", ",selectedGenres));
            });

            builder1.setNegativeButton("CANCEL", (dialog, which) -> {

            });

            builder1.setNeutralButton("CLEAR ALL", (dialog, which) -> {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                }
            });
            builder1.create();

            AlertDialog alertDialog = builder1.create();
            alertDialog.show();
        });
        reg_registration.setOnClickListener(view -> {

            if (userName.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();

            } else if (firstName.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, "Please enter your First Name", Toast.LENGTH_SHORT).show();

            } else if (email.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, "Please type an email id", Toast.LENGTH_SHORT).show();

            } else if (dob.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, "Please enter a DOB", Toast.LENGTH_SHORT).show();

            } else if (genres.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, "Please select at least 1 genre", Toast.LENGTH_SHORT).show();

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
                        reg_entry.put("Username", userName.getText().toString());
                        reg_entry.put("First Name", firstName.getText().toString());
                        reg_entry.put("Last Name", lastName.getText().toString());
                        reg_entry.put("Email", email.getText().toString());
                        reg_entry.put("Genres", String.join(";",selectedGenres));
                        reg_entry.put("Password", password.getText().toString());
                        reg_entry.put("Date of Birth", dob.getText().toString());
                        reg_entry.put("currentLevel", 0);
                        reg_entry.put("currentScore", 0);
                        UserService registerUser = userToken -> {
                            reg_entry.put("MobileToken", userToken);
                            Helper.setEmailPassword(RegisterActivity.this,
                                    email.getText().toString(), password.getText().toString(),
                                    userName.getText().toString());
                            Helper.setUserToken(RegisterActivity.this, userToken);
                            reference1.set(reg_entry).addOnSuccessListener(aVoid -> {
                                Toast.makeText(RegisterActivity.this, "User Successfully registered!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                            });
                        };
                        firebaseInstanceMessagingService.register(registerUser);

                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    private boolean checkingIfUsernameExists(String usernameToCompare) {
        CollectionReference collectionReference = firebaseFirestore.collection("users");
        Query mQuery = collectionReference.whereEqualTo("Username", usernameToCompare);
        mQuery.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                for (DocumentSnapshot ds : task.getResult()) {
                    String userNames = ds.getString("Username");
                    if (userNames.equals(usernameToCompare)) {
                        CustomToast customToast = new CustomToast(RegisterActivity.this,
                                "Username already exists", Snackbar.LENGTH_SHORT);
                        customToast.makeCustomToast();
                        userName.setText("");
                    }
                }
            }
            if (task.getResult().size() == 0) {
                try {
                    answer = true;
                } catch (NullPointerException e) {
                }
            }
        });
        return answer;
    }

    private  AlertDialog.Builder createCategoryDialog(String[] listItems, boolean[] checkedItems, List<String> selectedItems, EditText bOpenAlertDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        builder.setTitle("Choose any 3 genres:");

        builder.setMultiChoiceItems(listItems, checkedItems, (dialog, which, isChecked) -> {
            if (isChecked && count < 3) {
                count++;
                checkedItems[which] = isChecked;
            }
            else {
                Toast.makeText(RegisterActivity.this,
                        "Only 3 genres can be selected at this point", Toast.LENGTH_SHORT)
                        .show();
                ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                checkedItems[which]=false;
            }
            if (!isChecked) count--;
        });

        builder.setCancelable(false);

        builder.setPositiveButton("Done", (dialog, which) -> {
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
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            for (int i = 0; i < checkedItems.length; i++) {
                checkedItems[i] = false;
            }
            count = 0;
        });

        builder.setNeutralButton("CLEAR ALL", (dialog, which) -> {
            for (int i = 0; i < checkedItems.length; i++) {
                checkedItems[i] = false;
            }
            count = 0;
        });

        builder.create();
        return builder;
    }

    public void broadcastIntent() {
        registerReceiver(myBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            //Register or UnRegister your broadcast receiver here
            unregisterReceiver(myBroadcastReceiver);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
