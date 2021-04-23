package edu.neu.madcourse.numadsp21finalproject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class CustomToast {

    private String message;
    private int duration;
    Context context;


    public CustomToast(Context context, String message, int duration ) {
        this.message = message;
        this.duration = duration;
        this.context = context;
    }

    public void makeCustomToast() {

        Snackbar snack = Snackbar.make(((Activity)context).findViewById(android.R.id.content),
                message, duration);
        View view = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snack.show();
    }


}
