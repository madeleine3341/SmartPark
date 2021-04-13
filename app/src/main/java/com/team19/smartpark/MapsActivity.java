package com.team19.smartpark;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team19.smartpark.adapters.filterListAdapter;
import com.team19.smartpark.models.GoogleMapsBottomSheetBehaviour;
import com.team19.smartpark.models.Parking;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//marker bottom sheet imports

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, OnStreetViewPanoramaReadyCallback {


    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final Map<String, Marker> mMarkerMap = new HashMap<>();
    private final LinkedHashMap<String, Parking> parkingsList = new LinkedHashMap<String, Parking>();
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    FirebaseAuth fAuth;
    FloatingActionButton floatingAb;
    private DatabaseReference mDatabase;
    private FirebaseFirestore fStore;
    private GoogleMap mMap;
    private SearchView searchView;
    private ImageView closeButton;
    private Context context;
    private LinearLayout linearLayout;
    private ListView filterListView;
    private LinearLayoutManager llm;
    private Button nearbyButton, clearButton;
    private ToggleButton sASButton, sFeesButton, openButton;
    private Spinner sDistanceButton;
    private TextView textView;
    private LatLng cameraLatLng;
    private BottomSheetBehavior mbottomSheetBehavior;
    private int spinnerPosition;
    private HorizontalScrollView horizontalScrollView;
    private Circle myCircle;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    //ArrayList to hold all parking objects
    private LinkedHashMap<String, Parking> parkings;
    private FloatingActionButton fab;
    private FloatingActionButton myLocationButton;
    private FloatingActionButton userButton;
    //for marker bottom sheet
    private GoogleMapsBottomSheetBehaviour behavior;
    private View parallax;
    //    private com.google.android.gms.maps.model.LatLng clickedMarkerCoordinates;
    private TextView vCoordinates;
    private TextView vName;
    private TextView vRecommended;
    private TextView vFee;
    private TextView vAddress;
    private TextView vRating;
    private TextView vStatus;
    private StreetViewPanorama streetPan;
    private ListView reviewListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        filterUISetup();
        fab = findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.GONE);
        myLocationButton = findViewById(R.id.myLocationButton);
        userButton = findViewById(R.id.userButton);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), Register.class));
                    finish();
                }
            }
        });
        if (fAuth.getCurrentUser() != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference(fAuth.getCurrentUser().getUid() + "/userInfo/type");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.getValue().equals("B")) {
                            fab.setVisibility(View.VISIBLE);
                        } else {
                            fab.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    fab.setVisibility(View.GONE);
                }
            });
        } else {
            fab.setVisibility(View.GONE);
        }
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MapsActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    getDeviceLocation();
                    updateLocationUI();

                } else {
                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
                //LatLng latLng = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ParkingListActivity.class);
                startActivity(intent);
            }
        });
        try {
            searchView = findViewById(R.id.search_bar);
            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = searchView.findViewById(id);
            textView.setTextColor(Color.BLACK);


        } catch (NullPointerException ignore) {

        }
        searchView.onWindowFocusChanged(false);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFilterUI(false);
                nearbyButton.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                cameraLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                updateFilterUI(false);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = null;
                    try {
                        address = addressList.get(0);

                    } catch (IndexOutOfBoundsException ignore) {

                    }
                    if (address != null) {
                        nearbyButton.setVisibility(View.VISIBLE);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        cameraLatLng = latLng;
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    } else {
                        Toast.makeText(getApplicationContext(), "Address not found", Toast.LENGTH_SHORT).show();
                    }
                    searchView.clearFocus();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateFilterUI(false);
                nearbyButton.setVisibility(View.GONE);
                return false;
            }
        });

        //Marker Bottom Sheet section********
//        clickedMarkerCoordinates = new com.google.android.gms.maps.model.LatLng(0,0);
        floatingAb = findViewById(R.id.fab);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.parallax);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        final View bottomsheet = findViewById(R.id.bottomsheet);
        behavior = GoogleMapsBottomSheetBehaviour.from(bottomsheet);
        parallax = findViewById(R.id.parallax);
        behavior.setParallax(parallax);
        behavior.anchorView(floatingAb);

        // wait for the bottomsheet to be laid out
        bottomsheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // set the height of the parallax to fill the gap between the anchor and the top of the screen
                CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(parallax.getMeasuredWidth(), behavior.getAnchorOffset() / 2);
                parallax.setLayoutParams(layoutParams);
                bottomsheet.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

//        behavior.setBottomSheetCallback(new GoogleMapsBottomSheetBehaviour.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, @GoogleMapsBottomSheetBehaviour.State int newState) {
//                // each time the bottomsheet changes position, animate the camera to keep the pin in view
//                // normally this would be a little more complex (getting the pin location and such),
//                // but for the purpose of an example this is enough to show how to stay centered on a pin
//                mMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLng(clickedMarkerCoordinates));
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });

    }


    //send to google map show directions
    public void DisplayTrack(String destination) {
        getDeviceLocation();
        Double la = lastKnownLocation.getLatitude();
        Double lo = lastKnownLocation.getLongitude();


        try {
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + la + "," + lo + "/" + destination);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        setMarkers();

        mMap.setOnMarkerClickListener(new com.google.android.gms.maps.GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                behavior.setState(GoogleMapsBottomSheetBehaviour.STATE_COLLAPSED);
                behavior.setHideable(false);
//                clickedMarkerCoordinates = new com.google.android.gms.maps.model.LatLng(0,0);
                mMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLng(marker.getPosition()));
//                clickedMarkerCoordinates = marker.getPosition();
                //finding the right parking in parking list using the marker title
                HashMap<String, Boolean> temp = new HashMap<String, Boolean>();
                Parking targetParking = new Parking("N/A", 0, 0, "N/A", temp, false, 0);
                for (Map.Entry<String, Parking> parkingSet :
                        parkingsList.entrySet()) {
                    if (marker.getTitle().equals(parkingSet.getValue().name)) {
                        targetParking = parkingSet.getValue();
                    }
                }


                vCoordinates = findViewById(R.id.markerSheetCoordinates);
                vName = findViewById(R.id.markerSheetName);
                vRecommended = findViewById(R.id.markerSheetRecommended);
                vFee = findViewById(R.id.markerSheetFee);
                vAddress = findViewById(R.id.markerSheetAddress);
                vRating = findViewById(R.id.markerSheetRating);
                vStatus = findViewById(R.id.markerSheetStatus);

                boolean availableExists = false;
                Iterator it = targetParking.getSpots().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    if ((Boolean) pair.getValue()) {
                        vRecommended.setText(pair.getKey().toString().replace("Id", ""));
                        vRecommended.setTextColor(Color.BLACK);
                        availableExists = true;
                        break;
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                    if (!availableExists) {
                        vRecommended.setText("FULL");
                        vRecommended.setTextColor(Color.BLACK);
                    }
                }

                if (targetParking.isStatus()) {
                    vStatus.setText("Open");
                    vStatus.setTextColor(Color.GREEN);
                } else {
                    vStatus.setText("Closed");
                    vStatus.setTextColor(Color.RED);
                }

                double rating = 4;
                int numOfRatings = 5;
                double[] ratings = new double[numOfRatings];
                ArrayList<String> reviews = new ArrayList<>();
                for (int i = 0; i < numOfRatings; i++) {
                    if (numOfRatings % 2 != 0 && i == 0) {
                        reviews.add("Dave " + rating + "/5");
                        reviews.add("It was exactly as expected, however i will look at the other parking lots on the app to see if there are any that are better for me.");
                        reviews.add(" ");
                    } else if (i % 2 == 0) {
                        reviews.add("Loretta " + (rating + 0.5) + "/5");
                        if (ratings[i] > 5) {
                            ratings[i] = 5;
                        }
                        reviews.add("This place was exceptional and cared to my needs, i hope i will be able to get some rewards soon!!!");
                        reviews.add(" ");
                    } else {
                        reviews.add("Tarek " + (rating - 0.5) + "/5");
                        reviews.add("This parking lot was not as expected and i will be going back to my other parking lot, however i will miss the smart functionality and hope it expands to all the parking lots.");
                        reviews.add(" ");
                    }
                }

                ListView lv = findViewById(R.id.markerSheetReviews);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        reviews);

                lv.setAdapter(arrayAdapter);

                vRating.setText("4/5");
                vRating.setTextColor(Color.BLACK);
                vCoordinates.setText(targetParking.getLat() + "," + targetParking.getLng());
                vCoordinates.setTextColor(Color.BLACK);
                vName.setText(targetParking.getName());
                vName.setTextColor(Color.BLACK);
                vFee.setText(targetParking.getfees() + "0$");
                vFee.setTextColor(Color.BLACK);
                vAddress.setText(targetParking.getAddress());
                vAddress.setTextColor(Color.BLACK);
                //marker.getPosition()
                streetPan.setPosition(marker.getPosition());

                return true;
                //finding the right reviews and invoking the bottom sheet content adapter

            }
        });
        //function when the map is clicked a coordinates "latLng". this closes marker bottom sheet
        mMap.setOnMapClickListener(new com.google.android.gms.maps.GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(com.google.android.gms.maps.model.LatLng latLng) {
                behavior.setHideable(true);
                behavior.setState(GoogleMapsBottomSheetBehaviour.STATE_HIDDEN);
            }
        });

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetPan = streetViewPanorama;
        streetViewPanorama.setUserNavigationEnabled(false);

    }

    private void setMarkers() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> parkingList = dataSnapshot.getChildren();
                for (DataSnapshot keyNode : parkingList) {
                    Iterable<DataSnapshot> e = keyNode.getChildren();
                    for (DataSnapshot parkingList12 : e) {
                        if (parkingList12.getKey().equals("parkingLots")) {
                            Iterable<DataSnapshot> e2 = parkingList12.getChildren();
                            for (DataSnapshot listofParking : e2) {
                                Log.i("Bug: ", String.valueOf(listofParking.getValue()));
                                parkingsList.put(String.valueOf(listofParking.getKey()), listofParking.getValue(Parking.class));
                            }
                        }
                    }
                }
                for (Map.Entry<String, Parking> parkingSet :
                        parkingsList.entrySet()) {
                    Parking parking = parkingSet.getValue();
                    int count = 0;
                    int available = 0;
                    String parkingId = null;
                    Marker previousMarker = null;
                    if (parking.spots != null && (parking.name != null || parking.name != "") && (parking.address != null || parking.address != "")) {
                        count = parking.spots.size();
                        available = Collections.frequency(parking.spots.values(), true);
                        parkingId = parkingSet.getKey();
                        previousMarker = mMarkerMap.get(parkingId);
                    }
                    // if previous marker already exists just update its availability
                    if (previousMarker != null) {
                        Log.d(TAG, "onDataChange: previous marker exists, update availability");
                        previousMarker.setSnippet(available + "/" + count + " available");
                        if (previousMarker.isInfoWindowShown()) {
                            previousMarker.hideInfoWindow();
                            previousMarker.showInfoWindow();
                        }
                    }
                    // else its a new marker to be added (which means a new parking lot has been added)
                    else {
                        Log.d(TAG, "onDataChange: create new marker");
                        LatLng parkingLocation = new LatLng(parking.lat, parking.lng);
                        MarkerOptions parkingMarker = new MarkerOptions().position(parkingLocation).title(parking.name).snippet(available + "/" + count + " available").icon(getBitmapDescriptor(R.drawable.ic_parking_icon));
//                    parkingMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_icon));
                        //put the new marker on the map to display it
                        Marker marker = mMap.addMarker(parkingMarker);
//                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_icon));
                        // add the market to the hashmap to remember it next time (keep track of it)
                        mMarkerMap.put(parking.name, marker);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private BitmapDescriptor getBitmapDescriptor(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.getTitle();
                        marker.getSnippet();
                        return false;
                    }
                });
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
//                            double lat = lastKnownLocation.getLatitude();
//                            double lon = lastKnownLocation.getLongitude();

                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                cameraLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void filterUISetup() {
        horizontalScrollView = findViewById(R.id.scrollViewFilter);
        linearLayout = findViewById(R.id.bottomSheet);
        filterListView = findViewById(R.id.filterListView);
        mbottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        nearbyButton = findViewById(R.id.nearbyButton);
        textView = findViewById(R.id.sortTextView);
        sDistanceButton = findViewById(R.id.sortDistanceButton);
        sASButton = findViewById(R.id.sortASButton);
        clearButton = findViewById(R.id.clearButton);
        openButton = findViewById(R.id.openButton);
        sFeesButton = findViewById(R.id.sortFeesButton);
        updateFilterUI(false);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.string, R.layout.spinner_custom_drop_down_menu);
        adapter.setDropDownViewResource(R.layout.spinner_custom_drop_down_menu);
        sDistanceButton.setAdapter(adapter);
        sDistanceButton.setOnItemSelectedListener(this);
        sASButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sASButton.isChecked() && spinnerPosition == 0 && !openButton.isChecked() && !sFeesButton.isChecked()) {
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    openButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (!sASButton.isChecked()) {
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sortAlgorithm();
                } else {
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(235, 236, 246)));
                    sortAlgorithm();
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sASButton.isChecked() && spinnerPosition == 0 && !openButton.isChecked()) {
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    openButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (!openButton.isChecked()) {
                    openButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sortAlgorithm();
                } else {
                    openButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(235, 236, 246)));
                    sortAlgorithm();
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        sFeesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sASButton.isChecked() && spinnerPosition == 0 && !openButton.isChecked() && !sFeesButton.isChecked()) {
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    openButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (!sFeesButton.isChecked()) {
                    sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sortAlgorithm();
                } else {
                    sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(235, 236, 246)));
                    sortAlgorithm();
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        nearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFilterUI(true);
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCircle != null) {
                    myCircle.remove();
                    myCircle = null;
                }
                updateFilterUI(false);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                sDistanceButton.setSelection(0);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerPosition = position;
        if (parent.getItemAtPosition(position) != null && position != 0) {
            String text = parent.getItemAtPosition(position).toString();
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            sortAlgorithm();
            mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (position == 0 && !sASButton.isChecked() && !sFeesButton.isChecked() && !openButton.isChecked()) {
            if (myCircle != null) {
                myCircle.remove();
                myCircle = null;
            }
            mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (spinnerPosition == 0) {
            if (myCircle != null) {
                myCircle.remove();
                myCircle = null;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void updateFilterUI(Boolean state) {
        if (state) {
            nearbyButton.setVisibility(View.GONE);
            horizontalScrollView.setVisibility(View.VISIBLE);
            clearButton.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);

        } else {
            mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            nearbyButton.setVisibility(View.VISIBLE);
            horizontalScrollView.setVisibility(View.GONE);
            clearButton.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            sASButton.setChecked(false);
            openButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            openButton.setChecked(false);
            sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            sFeesButton.setChecked(false);
        }
    }

    private void sortAlgorithm() {
        cameraLatLng = mMap.getCameraPosition().target;
        //Create two float to store distance between two locations
        float[] result1 = new float[1];
        float[] result2 = new float[1];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm");
        String currentTime = simpleDateFormat.format(calendar.getTime());
        float currentHour = Float.valueOf(currentTime.substring(0, currentTime.indexOf(":"))) + Float.valueOf(currentTime.substring(currentTime.indexOf(":") + 1)) / 60;
        int radius;
        switch (spinnerPosition) {
            case 1:
                radius = 100;
                break;
            case 2:
                radius = 200;
                break;
            case 3:
                radius = 300;
                break;
            case 4:
                radius = 400;
                break;
            case 5:
                radius = 500;
                break;
            default:
                if (sASButton.isChecked() || openButton.isChecked() || sFeesButton.isChecked()) {
                    radius = 2000;
                } else {
                    radius = 0;
                }
                break;
        }
        //Create an ArrayList of Parking object
        ArrayList<Parking> filter = new ArrayList<Parking>();
        //Create an ArrayList of String to store distance information
        ArrayList<String> distance = new ArrayList<String>();
        for (Map.Entry<String, Parking> parkingSet : parkingsList.entrySet()) {
            Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, parkingSet.getValue().lat, parkingSet.getValue().lng, result1);
            if (result1[0] < radius) {
                filter.add(parkingSet.getValue());
            }
        }
        if (myCircle != null) {
            myCircle.remove();
            myCircle = null;
        }
        if (spinnerPosition > 0) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(cameraLatLng)   //set center
                    .radius(radius)   //set radius in meters
                    .fillColor(Color.argb(150, 235, 236, 246))  //default
                    .strokeColor(Color.BLUE)
                    .strokeWidth(5);
            myCircle = mMap.addCircle(circleOptions);
        }
        // Bubble Sorting
        for (int i = 0; i < filter.size(); i++) {
            for (int j = i + 1; j < filter.size(); j++) {
                float spot1 = 0;
                float spot2 = 0;
                double fee1 = 0;
                double fee2 = 0;
                float operatingHour1 = 0;
                float operatingHour2 = 0;
                if (sASButton.isChecked()) {
                    spot1 = Collections.frequency(filter.get(i).spots.values(), true) / filter.get(i).spots.size();
                    spot2 = Collections.frequency(filter.get(j).spots.values(), true) / filter.get(j).spots.size();
                } else {
                    spot1 = 0;
                    spot2 = 0;
                }
                if (sFeesButton.isChecked()) {
                    fee1 = filter.get(i).fees;
                    fee2 = filter.get(j).fees;
                }
                if (openButton.isChecked()) {
                    String a = filter.get(i).operatingHour;
                    String b = filter.get(j).operatingHour;
                    float ch1 = Float.valueOf(a.substring(a.indexOf("-") + 1, a.indexOf(":", a.indexOf("-")))) + Float.valueOf(a.substring(a.indexOf(":", a.indexOf("-")) + 1)) / 60;
                    float ch2 = Float.valueOf(b.substring(b.indexOf("-") + 1, b.indexOf(":", b.indexOf("-")))) + Float.valueOf(b.substring(b.indexOf(":", b.indexOf("-")) + 1)) / 60;
                    operatingHour1 = (ch1 - currentHour) / currentHour;
                    operatingHour2 = (ch2 - currentHour) / currentHour;
                    Log.i("Spot1 Hour: ", String.valueOf(operatingHour1));
                    Log.i("Spot2 Hour: ", String.valueOf(operatingHour2));
                }
                if (spinnerPosition > 0) {
                    Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, filter.get(i).lat, filter.get(i).lng, result1);
                    Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, filter.get(j).lat, filter.get(j).lng, result2);
                } else {
                    result1[0] = 0;
                    result2[0] = 0;
                }
                float score1 = (float) (0.7 * (result1[0] / 2000) + 0.1 * spot1 + 0.1 * (1 - operatingHour1) + 0.1 * (fee1));
                float score2 = (float) (0.7 * (result2[0] / 2000) + 0.1 * spot2 + 0.1 * (1 - operatingHour2) + 0.1 * (fee2));
                if (score1 > score2) {
                    Collections.swap(filter, j, i);
                }
            }
        }
        //Get the distance information of the sorted parking array list
        for (int i = 0; i < filter.size(); i++) {
            Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, filter.get(i).lat, filter.get(i).lng, result1);
            distance.add(String.format("%.0f", result1[0]));
        }
        //Set the adapter for the List View
        filterListAdapter adapter = new filterListAdapter(this, R.layout.adapter_bottom_sheet_list_view, filter, distance);
        filterListView.setAdapter(adapter);
    }

    public void updateview(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
    }

}

