package com.example.qrchive;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// For Current Location Display: https://www.javatpoint.com/android-google-map-displaying-current-location

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final int REQUEST_PERMISSION_STATE = 1;
    private boolean locationPermissionGranted;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO: handle permission request
            Toast.makeText(this, "Location Permission is not enabled. Some features are not available", Toast.LENGTH_SHORT).show();

        } else {
            // Permission has already been granted
            locationPermissionGranted = true;
            initMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                initMap();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission is required for some features",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (locationPermissionGranted) {
            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        } else {
            // Add a marker in Timbuktu and move the camera
            LatLng Timbuktu = new LatLng(-54, 73);
            mMap.addMarker(new MarkerOptions().position(Timbuktu).title("Marker in Timbuktu"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Timbuktu));
        }

    }
}

//    /**
//     * @method: scatter some QR codes around the current location of the user.
//     * */
//    private void scatterQRLocations() {
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        double latitude = lastLocation.getLatitude();
//        double longitude = lastLocation.getLongitude();
//
//        LatLng pointZeroOneAway = new LatLng(latitude - 0.01, longitude -0.01);
//        LatLng pointOneAway = new LatLng(latitude - 0.1, longitude - 0.1);
//        LatLng oneAway = new LatLng(latitude - 1, longitude -1);
//        map.addMarker(new MarkerOptions().position(pointZeroOneAway).title("CS Building"));
//        map.addMarker(new MarkerOptions().position(pointOneAway).title("CS Building"));
//        map.addMarker(new MarkerOptions().position(oneAway).title("CS Building"));
//
//        Log.d("latitude: ", Double.toString(latitude));
//        Log.d("longitude:", Double.toString(longitude));
//
//        // RULE: increase num codes by factor of 10, increase scale by factor 10. Same for decreasing.
//        int numCodes = 100;
//        double scale = 0.01;
//        for (int i=0 ; i< numCodes; i++)  {
//
//            double final_lat;
//            double final_long;
//
//            boolean negativeLat = (int) (Math.random() * 1000) % 2 == 0;
//            boolean negativeLong = (int) (Math.random() * 1000) % 2 == 0;
//
//            double randLat  = Math.random() * scale;
//            double randLong = Math.random() * scale;
//
//            if (negativeLat) {
//                final_lat = ((-1 * randLat) + latitude);
//            }
//            else {
//                final_lat = (randLat + latitude);
//            }
//
//            if (negativeLong) {
//                final_long = ((-1 * randLong) + longitude);
//            }
//            else {
//                final_long = (randLong + longitude);
//            }
//
//            Log.d("negativeLat: ", Boolean.toString(negativeLat));
//            Log.d("lat: ", Double.toString(randLat));
//            Log.d("negativeLong: ", Boolean.toString(negativeLong));
//            Log.d("long: ", Double.toString(randLong));
//
//            LatLng randomQRMarker = new LatLng(final_lat, final_long);
//            map.addMarker(new MarkerOptions().position(randomQRMarker).title("QR Code!"));
//
//        }
//    }

//}