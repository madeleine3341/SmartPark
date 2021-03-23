package com.team19.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ParkingListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    protected ListView parkingList;
    private DatabaseReference PNDatabase;
    List <String> parkings = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list);

        parkingList = findViewById(R.id.ParkingList);
        PNDatabase = FirebaseDatabase.getInstance().getReference(); //route

           readParking();



        parkingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToParkingSpotsActivity();
            }
        });

    }

    public void readParking(){

        PNDatabase.addValueEventListener(new ValueEventListener() {
            ArrayList<String> arrayList = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TreeMap <String,Parking> keyParking = new TreeMap<>();
                //datasnap stores key and value of a node
                for(DataSnapshot keyNode: dataSnapshot.getChildren()){
                    keyParking.put(keyNode.getKey(),keyNode.getValue(Parking.class));
                }
                for (Map.Entry<String, Parking> parking :keyParking.entrySet()) {
                    arrayList.add(parking.getValue().getName());
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
                parkingList.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void goToParkingSpotsActivity() {
        //Intent intent = new Intent(this, ParkingSpotsActivity.class);
        //startActivity(intent);
    }

}