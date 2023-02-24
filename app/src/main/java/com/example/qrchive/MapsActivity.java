package com.example.qrchive;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.qrchive.databinding.ActivityMapsBinding;

// For Current Location Display: https://www.javatpoint.com/android-google-map-displaying-current-location
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ActivityMapsBinding binding;
    private static final int ZOOM_LEVEL = 16;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if ( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
             && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);

        }
        map.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_LEVEL));
        }

        /** HOW TO ADD A PIN AND MOVE LOCATION TO THAT PIN
         *  // Add a marker in Edmonton and move the camera
         *         LatLng csBuilding = new LatLng(53.526544, -113.527028);
         *         map.addMarker(new MarkerOptions().position(csBuilding).title("CS Building"));
         *         map.moveCamera(CameraUpdateFactory.newLatLngZoom(csBuilding, 16));
         *
         * */

    }


}