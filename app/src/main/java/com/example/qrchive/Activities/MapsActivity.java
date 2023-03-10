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
import android.util.Log;
import android.widget.Toast;
import com.example.qrchive.Classes.MapModel;
import com.example.qrchive.Classes.ScannedCode;
import com.example.qrchive.Classes.onCodesGeoQueriedListener;
import com.example.qrchive.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, onCodesGeoQueriedListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location currentLocation;
    private MapModel mapModel;
    private boolean fine_location_enabled = false;

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
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (currentLocation == null) {
            Toast.makeText(this, "Navigate to settings to enable location services", Toast.LENGTH_SHORT).show();
            return;
        }

        moveCameraToCurrentLocation();
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
                fine_location_enabled = true;
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
        // if we want to update camera always to current location
//         moveCameraToCurrentLocation();
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

    private void drawCodeMarker(ScannedCode code) {
        String location = code.getLocation();
        Log.d("Location: ", location);
        mMap.addMarker(new MarkerOptions().position(new LatLng(1, 1)).title("QR Code!"));
    }

    /**
     * @method: scatter some QR codes around the current location of the user.
     * */
    private void scatterQRLocations() {
        Log.d("SCATTER QR LOCATIONS ", "HERE");
        double longitude = currentLocation.getLongitude();
        double latitude = currentLocation.getLatitude();
        mapModel = new MapModel(latitude, longitude);
        mapModel.setNearbyQRCodes(this);
    }

    @Override
    public void onCodesGeoQueried(List<ScannedCode> nearbyCodes) {
        Log.d("SCATTER QR LOCATIONS ", String.valueOf(mapModel.getNearbyQRCodes()));

        for (ScannedCode code : mapModel.getNearbyQRCodes()) {
            drawCodeMarker(code);
        }
    }
}