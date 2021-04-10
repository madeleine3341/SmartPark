package com.team19.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team19.smartpark.adapters.ParkingListAdapter;
import com.team19.smartpark.models.Parking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class ParkingListActivity extends AppCompatActivity {
    List<String> parkings = null;

    private DatabaseReference PNDatabase;

    ParkingListAdapter adapter;
    private RecyclerView plist;
    LinearLayoutManager ly;
    private FloatingActionButton addParkingFAB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list);
        addParkingFAB = findViewById(R.id.addParkingfab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addParkingFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddParkingActivity.class);
                startActivity(intent);
            }
        });

        plist = findViewById(R.id.Parking_RecyclerView);

        PNDatabase = FirebaseDatabase.getInstance().getReference(); //route

        ly = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        plist.addItemDecoration(new DividerItemDecoration(plist.getContext(), DividerItemDecoration.VERTICAL));

        plist.setLayoutManager(ly);

        readParkings();

    }



    public void readParkings() {

        PNDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> id = new ArrayList<>();
                ArrayList<String> name = new ArrayList<>();
                ArrayList<HashMap<String, Boolean>> spots = new ArrayList<>();

                TreeMap<String, Parking> keyParking = new TreeMap<>();
                //datasnap stores key and value of a node
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keyParking.put(keyNode.getKey(), keyNode.getValue(Parking.class));
                }
                loadlist(keyParking);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void loadlist(TreeMap<String, Parking> keyParking) {
        adapter = new ParkingListAdapter(keyParking, this);
        plist.setAdapter(adapter);
    }

}