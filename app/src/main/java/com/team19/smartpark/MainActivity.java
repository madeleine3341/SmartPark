package com.team19.smartpark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.TreeMap;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private TextView fullName,email,phone,verifyMsg;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phone = findViewById(R.id.profilePhone);
        fullName = findViewById(R.id.profileName);
        email    = findViewById(R.id.profileEmail);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        TreeMap<String, String> value = new TreeMap<>();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(fAuth.getCurrentUser().getUid()+"/userInfo");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> infos = dataSnapshot.getChildren();
                for (DataSnapshot parking :
                        infos) {
                    parking.getKey();
                    value.put(parking.getKey(), String.valueOf(parking.getValue()));
                }
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
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }


}