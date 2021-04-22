package com.team19.smartpark.models;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class FirebaseHelper {


    /**
     * @return
     * @deprecated Not working, only here for reference purposes on how to return a list of Parking from the Firebase async API
     */
    @Deprecated
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


    /**
     * Add a parking object to the Firebase RTDB
     *
     * @param parking
     */
    public static void addParkingLots(Parking parking) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        // in the parking lots database reference, push a new node for a new parking
        FirebaseDatabase.getInstance().getReference(fAuth.getCurrentUser().getUid() + "/parkingLots").push().setValue(parking);

    }

    /**
     * Add parking spots to a particular Parking lot
     *
     * @param parkingId
     * @param spots
     */
    public static void addParkingSpot(String parkingId, Map<String, Object> spots) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        // in the node reference of the parking, update the children by adding the new spots
        FirebaseDatabase.getInstance().getReference(fAuth.getCurrentUser().getUid() + "/parkingLots/" + parkingId).child("spots").updateChildren(spots);
        Log.d("TAG", "addParkingSpot: ");

    }

    /**
     * Remove a particular Parking lot by ID
     *
     * @param parkingLotId
     */
    public static void removeParkingLot(String parkingLotId) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        // remove the node of the parking
        FirebaseDatabase.getInstance().getReference(fAuth.getCurrentUser().getUid() + "/parkingLots/" + parkingLotId).removeValue();

    }
}
