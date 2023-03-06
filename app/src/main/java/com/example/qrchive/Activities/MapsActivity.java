package com.example.qrchive.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.qrchive.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// For Current Location Display: https://www.javatpoint.com/android-google-map-displaying-current-location

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final int REQUEST_PERMISSION_STATE = 1;
    private final static int ZOOM_LEVEL = 16;
    private boolean locationPermissionGranted;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Location Permission is not enabled. Some features are not available", Toast.LENGTH_SHORT).show();
            requestUserLocationService();


            }else{
            // Permission has already been granted
            locationPermissionGranted = true; // might be ambiguous
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

        // Forced permission check;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        scatterQRLocations();
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

        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // ensures that lastLocation is a non-null object
        if (lastLocation == null) {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            lastLocation = locationManager.getLastKnownLocation(provider);
        }

        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();

        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, ZOOM_LEVEL));

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

    /**
     * @method: prompts user to change location preferences
     */
    private void requestUserLocationService(){
        // Inspiration from:
        //  Siddarth Nyati, Feb 28, 2018, How to prompt user to turn on location?, https://stackoverflow.com/questions/36681528/how-to-prompt-user-to-turn-on-location
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location permission is required for some features");
        builder.setMessage("Please enable Location Services to use this feature");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // Show location settings when the user acknowledges the alert dialog
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_CODE_LOCATION_SETTINGS);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MapsActivity.this, MainActivity.class));
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    /**
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            initMap();
        }
    }
}