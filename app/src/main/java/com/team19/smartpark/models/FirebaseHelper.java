package com.team19.smartpark.models;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.team19.smartpark.models.Parking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
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

    public static HashMap<String, Boolean> getParkingSpots(Parking parking) {
        return parking.spots;
    }

    public static void addParkingLots(Parking parking) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.push().setValue(parking);
    }

    public static void addParkingSpot(String parkingId, Map<String, Object> spots) {

        FirebaseDatabase.getInstance().getReference(parkingId).child("spots").updateChildren(spots);
        Log.d("TAG", "addParkingSpot: ");

    }
}
