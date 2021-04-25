package edu.neu.madcourse.numadsp21finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.LibraryActivity;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.LibraryItem;
import edu.neu.madcourse.numadsp21finalproject.model.User;
import edu.neu.madcourse.numadsp21finalproject.service.FirebaseInstanceMessagingService;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    Button register;

    String microphonePermission = Manifest.permission.RECORD_AUDIO;
    String readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE;
    boolean openAppSettings = false;
    private BroadcastReceiver myBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        boolean permitted = getPermissionsForApp();
        if (permitted) {
            setLogin();
        }
    }


    private void setLogin() {
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

    private boolean getPermissionsForApp() {

        if ((ContextCompat.checkSelfPermission(MainActivity.this,
                microphonePermission) == PackageManager.PERMISSION_DENIED)
                || (ContextCompat.checkSelfPermission(MainActivity.this,
                readExternalStorage) == PackageManager.PERMISSION_DENIED)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{microphonePermission, readExternalStorage},
                    100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(MainActivity.this,
                            "Permission granted",
                            Toast.LENGTH_SHORT)
                            .show();
                    setLogin();

                } else {
                    String permissionString = "Please allow audio permissions and storage " +
                            "permissions as they will be required for smooth functionality of app";
                    if (Helper.getApplicationLaunchedFirstTime(MainActivity.this)) {
                        Helper.setApplicationLaunchedFirstTime(MainActivity.this);
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this
                                , microphonePermission)
                                ||
                                !ActivityCompat.shouldShowRequestPermissionRationale(
                                        MainActivity.this
                                        , readExternalStorage)
                        ) {

                            String permissionType = "";
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this
                                    , microphonePermission)
                                    &&
                                    !ActivityCompat.shouldShowRequestPermissionRationale(
                                            MainActivity.this
                                            , readExternalStorage)) {
                                permissionType += "Audio and Storage permissions have";
                            } else if (
                                    !ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this
                                            , microphonePermission)
                            ) {
                                permissionType += "Audio permission has";
                            } else {
                                permissionType += "Storage permission has";
                            }
                            permissionString = " " + permissionType + " been denied. " +
                                    "It is required for using certain app features." +
                                    "Please allow this in the app settings in your device.";
                            openAppSettings = true;
                        }
                    }
                    setPermissionDialog(permissionString);

                }
                return;
        }
    }

    private void setPermissionDialog(String permissionString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(permissionString).setTitle("Permissions Required!")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (openAppSettings) {
                            openAppSettings = false;
                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                        } else {
                            getPermissionsForApp();
                        }
                    }
                });

        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Permissions");
        alert.show();

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
                                            .get(0).get("Username") != null ?
                                            documentSnapshot.getDocuments()
                                                    .get(0).get("Username").toString() :
                                            "username_" + email;
                                    Helper.setEmailPassword(MainActivity.this,
                                            email, password, username);
                                    String mobileToken = documentSnapshot.getDocuments()
                                            .get(0).get("MobileToken") != null
                                            ? documentSnapshot.getDocuments()
                                            .get(0).get("MobileToken").toString()
                                            : null;
                                    String refreshToken = FirebaseInstanceMessagingService
                                            .getMobileRefreshToken();
                                    if (mobileToken == null
                                            || mobileToken.isEmpty()
                                            || !mobileToken.equals(refreshToken)) {
                                        FirebaseMessaging.getInstance().getToken()
                                                .addOnCompleteListener(tokenItem -> {
                                                    if (!tokenItem.isSuccessful()) {
                                                        //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                                        return;
                                                    }
                                                    FirebaseInstanceMessagingService
                                                            .setMobilTokenConstant(tokenItem.getResult());
                                                    documentSnapshot.getDocuments()
                                                            .get(0).getReference()
                                                            .update("MobileToken",
                                                                    FirebaseInstanceMessagingService
                                                                            .getMobileRefreshToken() );
                                                    Helper.setUserToken(MainActivity.this,
                                                            FirebaseInstanceMessagingService
                                                                    .getMobileRefreshToken());

                                                });

                                    }
                                    //NOT NEEDED
                                    /*UserService userService = new UserService() {
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
                                    };*/
                                    //FirebaseInstanceMessagingService.login(userService, mobileToken);
                                    Helper.setUserToken(MainActivity.this, mobileToken);
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.putExtra("email", email);
                                    subscribeToNews();
                                    startActivity(intent);
                                    finish();
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

    private void subscribeToNews(){

        FirebaseMessaging.getInstance().subscribeToTopic("news")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "subscribed";
                        if (!task.isSuccessful()) {
                            msg = "subscription failed";
                        }
                        Log.d("SUBSCRIBE", msg);
                        // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
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

}