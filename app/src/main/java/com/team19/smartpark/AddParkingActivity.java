package com.team19.smartpark;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.team19.smartpark.models.FirebaseHelper;
import com.team19.smartpark.models.Parking;

import java.util.HashMap;

public class AddParkingActivity extends AppCompatActivity {

    final String TAG = "AddParkingActivity";
    private Button saveBtn;
    private EditText nameTextView;
    private EditText addressTextView;
    private EditText spotsTextView;
    private ImageButton selectLocationBtn;
    private FloatingActionButton actionBtn;
    private LatLng parkingLocation;
    private boolean locationSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        saveBtn = findViewById(R.id.saveBtn);
        nameTextView = findViewById(R.id.nameTextView);
        addressTextView = findViewById(R.id.addressTextView);
        spotsTextView = findViewById(R.id.spotsTextView);
        selectLocationBtn = findViewById(R.id.selectionLocationbtn);
        selectLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectionLocationMapFragment().show(getSupportFragmentManager(), null);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameTextView.getText().toString();
                String address = addressTextView.getText().toString();
                String strSpots = spotsTextView.getText().toString();
                strSpots = strSpots.replace(" ", "");
                String[] spots = strSpots.split(",");

                HashMap<String, Boolean> hashspot = new HashMap<>();
                for (String spot :
                        spots) {
                    hashspot.put(spot, true);
                }


                if (!name.isEmpty() && !address.isEmpty() && locationSelected) {
                    Parking parking = new Parking();
                    parking.address = address;
                    parking.name = name;
                    parking.spots = hashspot;
                    parking.setLatLng(parkingLocation);
                    FirebaseHelper.addParkingLots(parking);
                    Toast.makeText(getApplicationContext(), "Parking added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


    }

    public void setParkingLocation(LatLng latLng) {
        parkingLocation = latLng;
        locationSelected = true;
    }
}
