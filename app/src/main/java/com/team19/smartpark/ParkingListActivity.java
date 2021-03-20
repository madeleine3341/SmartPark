package com.team19.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParkingListActivity extends AppCompatActivity {

    protected ListView parkingList;
    private DatabaseReference PNDatabase;
    List <String> parkings = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list);

        parkingList = findViewById(R.id.ParkingList);
        PNDatabase = FirebaseDatabase.getInstance().getReference(); //route



        loadListView();
        parkingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToParkingSpotsActivity();
            }
        });

    }


    protected void loadListView() {

            ArrayList<String> arrayList = new ArrayList<>();

            //static for now
            arrayList.add("parking1");
             arrayList.add("parking2");


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        parkingList.setAdapter(arrayAdapter);

    }

    private void goToParkingSpotsActivity() {
        Intent intent = new Intent(this, ParkingSpotsActivity.class);
        startActivity(intent);
    }

}