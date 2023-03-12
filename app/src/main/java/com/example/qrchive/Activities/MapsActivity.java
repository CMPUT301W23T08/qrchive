package com.example.qrchive.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, onCodesGeoQueriedListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location currentLocation;
    private MapModel mapModel;
    private boolean fine_location_enabled = false;

    private static final String TAG = MapsActivity.class.getSimpleName();
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

        // Google Official Documentation for Map Styling
        // Source: https://developers.google.com/maps/documentation/android-sdk/styling

        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json ));
            if (!success) {
                Log.e(TAG, "Failed to parse style.json");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find the style json", e);
        }

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

    /** @method: handle location permission request. If granted, restart the activity, else warn the user
     * that features are limited.
     * */
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

    /** @method: update current location.
     * */
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    /** @method: Pan the Camera to the current location of the user.
     * */
    private void moveCameraToCurrentLocation() {

        // Website: JavaPoint
        // Answer: https://www.javatpoint.com/android-google-map-displaying-current-location

        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();

        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, ZOOM_LEVEL));
    }

    /** @method: Draw a code marker on the map with the name of the QR and
     * the points associated.
     * */
    private void drawCodeMarker(ScannedCode code) {
        GeoPoint location = code.getLocation();
        String name = code.getName();
        String points = String.valueOf(code.getPoints());
        Log.d("Location: ", String.valueOf(location.getLatitude()));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title(name)
                .snippet(points + " pts")
                .flat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
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

    /** @method: Draw a code marker on the map with the name of the QR and
     * the points associated.
     * */
    @Override
    public void onCodesGeoQueried(ScannedCode code) {
        GeoPoint location = code.getLocation();
        String name = code.getName();
        String points = String.valueOf(code.getPoints());
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title(name)
                .snippet(points + " pts")
                .flat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
    }

}