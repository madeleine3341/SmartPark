package com.team19.smartpark;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team19.smartpark.adapters.ParkingSpotAdapter;

import java.util.HashMap;
import java.util.TreeMap;

public class ParkingSpotsActivity extends AppCompatActivity {
    public static String parkingPath;
    RecyclerView parkingGrid;
    ParkingSpotAdapter adapter;
    GridLayoutManager gridLayoutManager;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        parkingPath = intent.getStringExtra("parkingId");
        setContentView(R.layout.activity_parking_spots);
        parkingGrid = findViewById(R.id.parkingGrid);
        gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        parkingGrid.setLayoutManager(gridLayoutManager);

        mDatabase = FirebaseDatabase.getInstance().getReference(parkingPath + "/spots"); //path to parking to be put here
        mDatabase.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TreeMap<String, Boolean> spots = new TreeMap<>((HashMap<String, Boolean>) snapshot.getValue());
                loadList(spots, getSupportFragmentManager());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadList(TreeMap<String, Boolean> spots, FragmentManager supportFragmentManager) {
        adapter = new ParkingSpotAdapter(this, spots, supportFragmentManager);
        parkingGrid.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_items, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_addParkingSpot) {
            /// User chose the "Settings" item, show the app settings UI...
            AddParkingSpotsFragment dialog = new AddParkingSpotsFragment();
            dialog.show(getSupportFragmentManager(), "AddParkingSpotFragment");
            return true;
        }// If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
}