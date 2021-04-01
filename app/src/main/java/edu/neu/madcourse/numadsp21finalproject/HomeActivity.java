package edu.neu.madcourse.numadsp21finalproject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.SearchView;
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
/*        if(item.getItemId() == R.id.search) {
            showExpandedMenu(item);
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void showExpandedMenu(MenuItem searchViewItem) {
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        /*searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");
        searchView.setSubmitButtonEnabled(true);
        Toolbar.LayoutParams layoutParams =
                new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        80,
                        Gravity.CENTER | Gravity.RIGHT);
        searchView.setLayoutParams(layoutParams);*/

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // This is your adapter that will be filtered
                Toast.makeText(getApplicationContext(),"textChanged :"+newText,Toast.LENGTH_LONG).show();
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                // **Here you can get the value "query" which is entered in the search box.**
                Toast.makeText(getApplicationContext(),"searchvalue :"+query,Toast.LENGTH_LONG).show();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_top_menu, menu);
        //showExpandedMenu(menu.findItem(R.id.search));
        return true;
    }
}
