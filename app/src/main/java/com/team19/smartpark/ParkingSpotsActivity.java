package com.team19.smartpark;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class ParkingSpotsActivity extends AppCompatActivity {
    RecyclerView parkingGrid;
    ParkingSpotAdapter adapter;
    GridLayoutManager gridLayoutManager;
    DatabaseReference mDatabase;
    String parkingpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_spots);
        parkingGrid = findViewById(R.id.parkingGrid);
        gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        parkingGrid.setLayoutManager(gridLayoutManager);

        mDatabase = FirebaseDatabase.getInstance().getReference("-MWTyaEntGmeUhbQfV8N" + "/spots"); //path to parking to be put here
        mDatabase.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TreeMap<String, Boolean> spots = new TreeMap<>((HashMap<String, Boolean>) snapshot.getValue());
//                LinkedHashMap<String, Boolean> spots = new LinkedHashMap<String, Boolean>(spotss);
                loadList(spots);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadList(TreeMap<String, Boolean> spots) {
        adapter = new ParkingSpotAdapter(this, spots);
        parkingGrid.setAdapter(adapter);
    }
}