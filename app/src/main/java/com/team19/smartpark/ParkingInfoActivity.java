package com.team19.smartpark;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team19.smartpark.adapters.ParkingInfoAdapter;
import com.team19.smartpark.adapters.ParkingListAdapter;
import com.team19.smartpark.models.Parking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ParkingInfoActivity  extends AppCompatActivity {


    private DatabaseReference mDatabase;
    ParkingInfoAdapter adapter;
    private RecyclerView infoList;
    LinearLayoutManager ly;
    String parkingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_list);

        parkingID = getIntent().getStringExtra("reference");

        infoList = findViewById(R.id.Info_RecyclerView);

        mDatabase = FirebaseDatabase.getInstance().getReference(); //route

        ly = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        infoList.addItemDecoration(new DividerItemDecoration(infoList.getContext(), DividerItemDecoration.VERTICAL));

        infoList.setLayoutManager(ly);

        readInfo();

    }

    public void readInfo() {

        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> parkings = dataSnapshot.getChildren();
                HashMap<String, Boolean> temp = new HashMap<String, Boolean>();
                Parking targetParking = new Parking("N/A",0,0,"N/A",temp,false);
                //array list to hold all parking lots
                LinkedHashMap<String, Parking> parkingsList = new LinkedHashMap<String, Parking>();
                for (DataSnapshot parking :
                        parkings) {
                    parking.getKey();
                    //put all parking lots in the array list
                    parkingsList.put(parking.getKey(), parking.getValue(Parking.class));
                }
                //for each parking create a marker and populate it with its stats, if marker already created, just updated
                for (Map.Entry<String, Parking> parkingSet :
                        parkingsList.entrySet()) {
                    if (parkingID.equals(parkingSet.getValue().name)){
                        targetParking = parkingSet.getValue();
                    }
                }

                loadParking(targetParking);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void loadParking(Parking parking) {
        adapter = new ParkingInfoAdapter(parking, this);
        infoList.setAdapter(adapter);
    }
}
