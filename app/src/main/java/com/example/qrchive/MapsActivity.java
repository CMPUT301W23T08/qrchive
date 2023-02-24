package com.example.qrchive;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

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
    private LocationManager locationManager;

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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);

        }

        map.setMyLocationEnabled(true);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_LEVEL));
        }

        scatterQRLocations();

        /** HOW TO ADD A PIN AND MOVE LOCATION TO THAT PIN
         *  // Add a marker in Edmonton and move the camera
         *         LatLng csBuilding = new LatLng(53.526544, -113.527028);
         *         map.addMarker(new MarkerOptions().position(csBuilding).title("CS Building"));
         *         map.moveCamera(CameraUpdateFactory.newLatLngZoom(csBuilding, 16));
         *
         * */
    }

    /**
     * @method: scatter some QR codes around the current location of the user.
     * */
    private void scatterQRLocations() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();

        LatLng pointZeroOneAway = new LatLng(latitude - 0.01, longitude -0.01);
        LatLng pointOneAway = new LatLng(latitude - 0.1, longitude - 0.1);
        LatLng oneAway = new LatLng(latitude - 1, longitude -1);
        map.addMarker(new MarkerOptions().position(pointZeroOneAway).title("CS Building"));
        map.addMarker(new MarkerOptions().position(pointOneAway).title("CS Building"));
        map.addMarker(new MarkerOptions().position(oneAway).title("CS Building"));

        Log.d("latitude: ", Double.toString(latitude));
        Log.d("longitude:", Double.toString(longitude));

        // RULE: increase num codes by factor of 10, increase scale by factor 10. Same for decreasing.
        int numCodes = 100;
        double scale = 0.01;
        for (int i=0 ; i< numCodes; i++)  {

            double final_lat;
            double final_long;

            boolean negativeLat = (int) (Math.random() * 1000) % 2 == 0;
            boolean negativeLong = (int) (Math.random() * 1000) % 2 == 0;

            double randLat  = Math.random() * scale;
            double randLong = Math.random() * scale;

            if (negativeLat) {
                final_lat = ((-1 * randLat) + latitude);
            }
            else {
                final_lat = (randLat + latitude);
            }

            if (negativeLong) {
                final_long = ((-1 * randLong) + longitude);
            }
            else {
                final_long = (randLong + longitude);
            }

            Log.d("negativeLat: ", Boolean.toString(negativeLat));
            Log.d("lat: ", Double.toString(randLat));
            Log.d("negativeLong: ", Boolean.toString(negativeLong));
            Log.d("long: ", Double.toString(randLong));

            LatLng randomQRMarker = new LatLng(final_lat, final_long);
            map.addMarker(new MarkerOptions().position(randomQRMarker).title("QR Code!"));

        }
    }


}