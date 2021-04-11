package edu.neu.madcourse.numadsp21finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsActivity;
import edu.neu.madcourse.numadsp21finalproject.navigation.ProfileActivity;

public class HomeActivity extends AppCompatActivity {

   private DrawerLayout drawer;
   private ActionBarDrawerToggle toggle;
   private ImageButton searchButton;
   private EditText searchTextBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setNavigationListener();
        setBottomNavigationListener();
        setSearchComponent();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.practice_btn:
                Intent intent = new Intent(this,CategoryListActivity.class);
                startActivity(intent);
                break;
            case R.id.jam_btn:
                break;
            case R.id.meet_btn:
                break;
            default:
                return;

        }
    }
    private void setSearchComponent() {
        searchButton = findViewById(R.id.search_icon);
        searchButton.setOnClickListener(v ->
                Toast.makeText(HomeActivity.this,"search clicked", Toast.LENGTH_SHORT)
                        .show());
        searchTextBox = findViewById(R.id.search_box);
   /*     searchTextBox.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                searchTextBox.setCursorVisible(false);
            }
        });*/


    }

    private void setBottomNavigationListener() {
       BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
       bottomNavigationView.setOnNavigationItemSelectedListener(item->{
           int id = item.getItemId();
           switch(id) {
               case R.id.navigation_home:
                   Toast.makeText(HomeActivity.this, "My Home",Toast.LENGTH_SHORT).show();
                   break;
               case R.id.navigation_friends:
                   // Toast.makeText(HomeActivity.this, "My Friends",Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(this, FriendsActivity.class);
                   startActivity(intent);
                   break;
               case R.id.navigation_library:
                   Toast.makeText(HomeActivity.this, "My Library",Toast.LENGTH_SHORT).show();
                   break;
               case R.id.navigation_notifications:
                   Toast.makeText(HomeActivity.this, "My Notifications",Toast.LENGTH_SHORT).show();
                   break;
               default:
                   return true;
           }
           return true;

       });
    }

    private void setNavigationListener() {

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch(id)
            {
                case R.id.account_title:
                    Toast.makeText(HomeActivity.this, "My Account",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.profile_title:
                    Toast.makeText(HomeActivity.this, "My Profile",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this,ProfileActivity.class);
                    startActivity(intent);
                    break;
                case R.id.settings_title:
                    Toast.makeText(HomeActivity.this, "My Settings",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.blogs_title:
                    Toast.makeText(HomeActivity.this, "My Blogs",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.about_title:
                    Toast.makeText(HomeActivity.this, "About",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.logout_title:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Toast.makeText(HomeActivity.this, "You've logged out",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    return true;
            }
            drawer.close();
            return true;

        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_top_menu, menu);
        return true;
    }

    //citation: https://stackoverflow.com/a/39212571
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                EditText edit = ((EditText) v);
                Rect outR = new Rect();
                edit.getGlobalVisibleRect(outR);
                Boolean isKeyboardOpen = !outR.contains((int)ev.getRawX(), (int)ev.getRawY());
                if (isKeyboardOpen) {
                    edit.clearFocus();
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                }
                edit.setCursorVisible(!isKeyboardOpen);

            }
        }
        return super.dispatchTouchEvent(ev);
    }


}
