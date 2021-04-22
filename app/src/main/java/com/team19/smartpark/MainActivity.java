package com.team19.smartpark;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private TextView fullName, email, phone;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phone = findViewById(R.id.profilePhone);
        fullName = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        TreeMap<String, String> value = new TreeMap<>();

        //get the current user information from firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(fAuth.getCurrentUser().getUid() + "/userInfo");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> infos = dataSnapshot.getChildren();
                for (DataSnapshot parking :
                        infos) {
                    parking.getKey();
                    value.put(parking.getKey(), String.valueOf(parking.getValue()));
                }
                //get user phone, full name and email address to show on the textview
                phone.setText(value.get("phone"));
                fullName.setText(value.get("fName"));
                email.setText(value.get("email"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });


    }

    public void logout(View view) {
        //using firebase to signout user and redirect user to login activity
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }


}