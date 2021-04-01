package edu.neu.madcourse.numadsp21finalproject;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    EditText edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        edittext = (EditText) findViewById(R.id.dob);

        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month,
                                          int day) {

                        String a = day+ "/" + (month + 1) +"/" +year;
                        edittext.setText(""+a);
                    }
                };
                Time date = new Time();
                DatePickerDialog d = new DatePickerDialog(RegisterActivity.this, dpd, date.year ,date.month, date.monthDay);
                d.show();
            }
        });
    }
}
