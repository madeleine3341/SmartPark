package com.team19.smartpark;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FirebaseHelper {

    public static ArrayList<Parking> getParkingLots() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Task<DataSnapshot> dataSnapshotTask = mDatabase.get();
        ArrayList<Parking> parkings = new ArrayList<Parking>();

        dataSnapshotTask.addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> parkingsData = dataSnapshot.getChildren();
                for (DataSnapshot parking :
                        parkingsData) {
                    parkings.add(parking.getValue(Parking.class));
                }
            }
        });

        return parkings;

    }

    public static LinkedHashMap<String, Boolean> getParkingSpots(Parking parking) {
        return parking.spots;
    }

    public static void addParkingLots(Parking parking) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.push().setValue(parking);
    }

    public static void addParkingSpot(Parking parking, String id) {
        Map<String, Object> spot = new LinkedHashMap<String, Object>();
        spot.put(id, true);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.orderByChild("name").equalTo(parking.name).addChildEventListener(new ChildEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                snapshot.getRef().child("spots").updateChildren(spot);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
