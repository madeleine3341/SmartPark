package com.team19.smartpark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team19.smartpark.adapters.ParkingSpotAdapter;
import com.team19.smartpark.models.FirebaseHelper;

import java.util.HashMap;
import java.util.TreeMap;

public class ParkingSpotsActivity extends AppCompatActivity {
    public static String parkingPath;
    RecyclerView parkingGrid;
    ParkingSpotAdapter adapter;
    GridLayoutManager gridLayoutManager;
    private FirebaseAuth fAuth;
    private Button removeParkingLotButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        parkingPath = intent.getStringExtra("parkingId");
        setContentView(R.layout.activity_parking_spots);
        parkingGrid = findViewById(R.id.parkingGrid);
        gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        parkingGrid.setLayoutManager(gridLayoutManager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        removeParkingLotButton = findViewById(R.id.removeParkingLotButton);
        removeParkingLotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper.removeParkingLots(parkingPath);
                startActivity(new Intent(getApplicationContext(), ParkingListActivity.class));
                finish();
            }
        });


        fAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(fAuth.getCurrentUser().getUid() + "/parkingLots/" + parkingPath + "/spots");
        ref.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.i("Spots:", String.valueOf(snapshot.getValue()));
                if (snapshot.getValue() != null) {
                    TreeMap<String, Boolean> spots = new TreeMap<>((HashMap<String, Boolean>) snapshot.getValue());
                    loadList(spots, getSupportFragmentManager(), false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadList(TreeMap<String, Boolean> spots, FragmentManager supportFragmentManager, Boolean deleteMode) {
        adapter = new ParkingSpotAdapter(this, spots, supportFragmentManager, deleteMode);
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
        } else if (item.getItemId() == R.id.action_removeParkingSpot) {
//             If we got here, the user's action was not recognized.
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference(fAuth.getCurrentUser().getUid() + "/parkingLots/" + parkingPath + "/spots");
            ref.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.i("snapshot value", String.valueOf(snapshot.getValue()));
                    if (snapshot.getValue() != null) {
                        TreeMap<String, Boolean> spots = new TreeMap<>((HashMap<String, Boolean>) snapshot.getValue());
                        loadList(spots, getSupportFragmentManager(), true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
}