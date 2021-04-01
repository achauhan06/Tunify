package edu.neu.madcourse.numadsp21finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity  extends AppCompatActivity {

   private DrawerLayout drawer;
   private ActionBarDrawerToggle toggle;
   private EditText searchText;

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

    private void setSearchComponent() {
        searchText = findViewById(R.id.search_box);
        searchText.setOnClickListener(v -> Toast.makeText(HomeActivity.this,"search clicked", Toast.LENGTH_SHORT).show());

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*System.out.println("In Before");
                System.out.println(s.toString());*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*System.out.println("In OnText");
                System.out.println(s.toString());*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*System.out.println("In After");
                System.out.println(s.toString());*/
            }
        });
        searchText.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                Toast.makeText(HomeActivity.this,"search focus", Toast.LENGTH_SHORT).show();
            }
        });
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
                   Toast.makeText(HomeActivity.this, "My Friends",Toast.LENGTH_SHORT).show();
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
}
