package edu.neu.madcourse.numadsp21finalproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class JamSessionActivity extends AppCompatActivity {

    private String userId;
    private String userName;
    private String groupName;
    private Map<String, String> friendsMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jam_session_layout);
        Toolbar toolbar = findViewById(R.id.toolbar_jam_session);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupName = getIntent().getStringExtra("groupName");
        TextView groupTitle = findViewById(R.id.group_title);
        groupTitle.setText(groupName);
        String[] membersAsString = getIntent().getStringExtra("members").split(";");
        friendsMap = new HashMap<>();
        for(String memberArr : membersAsString) {
            String[] member = memberArr.split(":");
            if (member[0].equals(userId)) {
                userName = member[1];
            } else {
                friendsMap.put(member[1],member[0]);
            }
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
