package com.example.qrchive.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;
import com.example.qrchive.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location currentLocation;

    private static final int REQUEST_CODE_FINE_LOCATION = 200;
    private static final int REQUEST_CODE_LOCATION_SERVICES = 1;
    private final static int ZOOM_LEVEL = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);

        // Permission Check;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request Permissions from User.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
            return;
        }

        // Get device location
        mMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10F, (LocationListener) this);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        scatterQRLocations();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                Toast.makeText(this, "Thank you for allowing location", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission is required for some features", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        moveCameraToCurrentLocation();
    }

    /** Move camera to current location. May be null if Location Services is not enabled.
     * This is a separate permission from FINE_LOCATION.
     * */
    private void moveCameraToCurrentLocation() {

        // Website: JavaPoint
        // Answer: https://www.javatpoint.com/android-google-map-displaying-current-location

        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();

        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, ZOOM_LEVEL));
    }

    /**
     * @method: scatter some QR codes around the current location of the user.
     * */
    private void scatterQRLocations() {

        // Forced permission check;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        moveCameraToCurrentLocation();
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();

        int numCodes = 100;
        double scale = 0.01;

        for (int i=0 ; i< numCodes; i++)  {
            //calculate some values
            double final_lat;
            double final_long;
            boolean negativeLat = (int) (Math.random() * 1000) % 2 == 0;
            boolean negativeLong = (int) (Math.random() * 1000) % 2 == 0;
            double randLat  = Math.random() * scale;
            double randLong = Math.random() * scale;

            if (negativeLat) {
                final_lat = ((-1 * randLat) + latitude);
            } else {
                final_lat = (randLat + latitude);
            } if (negativeLong) {
                final_long = ((-1 * randLong) + longitude);
            } else {
                final_long = (randLong + longitude);
            }

            LatLng randomQRMarker = new LatLng(final_lat, final_long);
            mMap.addMarker(new MarkerOptions().position(randomQRMarker).title("QR Code!"));
        }
    }
}