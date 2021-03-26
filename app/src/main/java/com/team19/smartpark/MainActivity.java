package com.team19.smartpark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private Button Operator;
    private Button User;
    private Button Map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Operator=findViewById(R.id.Operator);
        User=findViewById(R.id.User);
        Map=findViewById(R.id.Map);


        Operator.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToParkingListActivity();
            }
        });


        User.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUserProfileActivity();
            }
        });


        Map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToMapsActivity();
            }
        });

    }

    private void goToParkingListActivity(){
        Intent intent = new Intent(this, ParkingListActivity.class);
        startActivity(intent);
    }
    private void goToUserProfileActivity(){
       // Intent intent = new Intent(this, ParkingListActivity.class);
       // startActivity(intent);
    }
    private void goToMapsActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}