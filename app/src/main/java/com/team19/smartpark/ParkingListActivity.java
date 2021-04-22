package com.team19.smartpark;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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
    ParkingListAdapter adapter;
    LinearLayoutManager Linear;
    private FirebaseAuth fAuth;
    private RecyclerView plist;
    private FloatingActionButton addParkingFAB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list);

        addParkingFAB = findViewById(R.id.addParkingfab);
        addParkingFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddParkingActivity.class);
                startActivity(intent);
            }
        });
        //listview of parkinglist=plist
        plist = findViewById(R.id.Parking_RecyclerView);
        fAuth = FirebaseAuth.getInstance();
        //list is vertical
        Linear = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //divider
        plist.addItemDecoration(new DividerItemDecoration(plist.getContext(), DividerItemDecoration.VERTICAL));
        plist.setLayoutManager(Linear);

        readParkings();

    }

    public void readParkings() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(fAuth.getCurrentUser().getUid() + "/parkingLots");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<HashMap<String, Boolean>> spots = new ArrayList<>();
                TreeMap<String, Parking> keyParking = new TreeMap<>();
                Iterable<DataSnapshot> infos = dataSnapshot.getChildren();

                //get node and the parking object
                for (DataSnapshot keyNode : infos) {
                    keyParking.put(keyNode.getKey(), keyNode.getValue(Parking.class));
                }
                //setup in adapter
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