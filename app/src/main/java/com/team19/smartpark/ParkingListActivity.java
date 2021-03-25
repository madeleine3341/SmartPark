package com.team19.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ParkingListActivity extends AppCompatActivity {
    List<String> parkings = null;

    private DatabaseReference PNDatabase;

    ParkingListAdapter adapter;
    private RecyclerView plist;
    LinearLayoutManager ly;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list);

        plist = (RecyclerView) findViewById(R.id.Parking_RecyclerView);

        PNDatabase = FirebaseDatabase.getInstance().getReference(); //route

        ly = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        plist.setLayoutManager(ly);

        readParkings();

    }



    public void readParkings() {

        PNDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String>  name=new ArrayList<>();
                ArrayList<HashMap<String, Boolean>> spots = new ArrayList<>();

                TreeMap<String, Parking> keyParking = new TreeMap<>();
                //datasnap stores key and value of a node
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keyParking.put(keyNode.getKey(), keyNode.getValue(Parking.class));
                }
                //for each node get name and
                for (Map.Entry<String, Parking> parking : keyParking.entrySet()) {
                    name.add(parking.getValue().getName());
                    spots.add(parking.getValue().getSpots());//get one spot

                }
                loadlist(name, spots);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


   private void loadlist(ArrayList<String> parkingname, ArrayList<HashMap<String, Boolean>> spots) {
    adapter = new ParkingListAdapter(parkingname, this,spots);
        plist.setAdapter(adapter);
    }

    private void goToParkingSpotsActivity() {
        //Intent intent = new Intent(this, ParkingSpotsActivity.class);
        //startActivity(intent);
    }
}