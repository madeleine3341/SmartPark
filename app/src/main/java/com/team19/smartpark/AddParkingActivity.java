package com.team19.smartpark;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.team19.smartpark.models.FirebaseHelper;
import com.team19.smartpark.models.Parking;

import java.util.TreeMap;

public class AddParkingActivity extends AppCompatActivity {

    final String TAG = "AddParkingActivity";
    private Button saveBtn;
    private EditText nameTextView;
    private EditText addressTextView;
    private EditText latTextView;
    private EditText lngTextView;
    private EditText spotsTextView;

    private FloatingActionButton actionBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        saveBtn = findViewById(R.id.saveBtn);
        nameTextView = findViewById(R.id.nameTextView);
        addressTextView = findViewById(R.id.addressTextView);
        latTextView = findViewById(R.id.latTextView);
        lngTextView = findViewById(R.id.lngTextView);
        spotsTextView = findViewById(R.id.spotsTextView);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameTextView.getText().toString();
                String address = addressTextView.getText().toString();
                String strSpots = spotsTextView.getText().toString();
                strSpots = strSpots.replace(" ", "");
                String[] spots = strSpots.split(",");

                TreeMap<String, Boolean> hashspot = new TreeMap<>();
                for (String spot :
                        spots) {
                    hashspot.put(spot, true);
                }


                double lat = 0.0;
                double lng = 0.0;
                try {
                    lat = Double.valueOf(latTextView.getText().toString());
                    lng = Double.valueOf(lngTextView.getText().toString());

                } catch (NumberFormatException e) {

                }

                if (name != null && address != null && lat != 0.0 && lng != 0.0) {
                    Parking parking = new Parking();
                    parking.address = address;
                    parking.lat = lat;
                    parking.lng = lng;
                    parking.name = name;
                    parking.spots = hashspot;
                    FirebaseHelper.addParkingLots(parking);
                    Toast.makeText(getApplicationContext(), "Parking added successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
