package com.example.qrchive.Fragments;


import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.qrchive.Activities.MainActivity;
import com.example.qrchive.Activities.MapsActivity;
import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.ScannedCode;
import com.example.qrchive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener;
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;


/**
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment {
    private static final int TARGET_FRAGMENT_REQUEST_CODE = 1;
    private static final String EXTRA_GREETING_MESSAGE = "EXTRA_PREFERENCES";
    private static final int REQUEST_CODE_FINE_LOCATION = 200;
    private static final double IMPERMISSIBLE_RADIUS = 1.0; // Radius under which we DONT allow the
    // same user to scan the same code
    private CodeScanner mCodeScanner;
    private FirebaseWrapper fbw;
    private FirebaseFirestore db;
    private GeoFirestore geoFirestore;
    private Result mResult;

    // The scanner view displays the camera preview on the screen.
    private CodeScannerView scannerView;
    // The reset button resets the scanner when clicked.
    private Button resetButton;
    private GeoPoint currentLocationGeopoint;

    // The flash button toggles the camera flash when clicked.
    private Button flashButton;
    boolean withinImpermissibleRadius;
    int docsWithinImpermissibleRadius = 0;



    public ScanFragment(FirebaseWrapper fbw) {
        this.fbw = fbw;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_scan, container, false);

        // Initialize the scanner view and code scanner objects.
        scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);



        db = fbw.db;


        geoFirestore = new GeoFirestore(db.collection("ScannedCodes"));
        // Check if the camera permission has been granted.
        if (ContextCompat.checkSelfPermission(
                activity, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {

            // Set a decode callback to handle scanned QR codes.
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mResult = result;
                            Toast.makeText(activity, mResult.getText(), Toast.LENGTH_SHORT).show();
                            scannerView.setForeground(new ColorDrawable(Color.TRANSPARENT));

                            Location currentLocation = getCurLocation();
                            currentLocationGeopoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());

                            geoFirestore.queryAtLocation(currentLocationGeopoint, IMPERMISSIBLE_RADIUS).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                                @Override
                                public void onDocumentEntered(@NonNull DocumentSnapshot documentSnapshot, @NonNull GeoPoint geoPoint) {
                                    if (Objects.equals(documentSnapshot.get("userDID"), fbw.getMyUserDID()) &&
                                            Objects.equals(documentSnapshot.get("hash"), Hashing.sha256().hashString(mResult.getText(), StandardCharsets.UTF_8).toString())) {
                                        docsWithinImpermissibleRadius++;
                                    }
                                }
                                @Override
                                public void onDocumentExited(@NonNull DocumentSnapshot documentSnapshot) {}
                                @Override
                                public void onDocumentMoved(@NonNull DocumentSnapshot documentSnapshot, @NonNull GeoPoint geoPoint) {}
                                @Override
                                public void onDocumentChanged(@NonNull DocumentSnapshot documentSnapshot, @NonNull GeoPoint geoPoint) {}
                                @Override
                                public void onGeoQueryReady() {
                                    if (docsWithinImpermissibleRadius > 0) {
                                        Toast.makeText(activity, "You have scanned this code within this area already. This QRCode will not be added", Toast.LENGTH_SHORT).show();
                                        docsWithinImpermissibleRadius = 0;
                                        return;
                                    }
                                    // !
                                    docsWithinImpermissibleRadius = 0;
                                    new ScanResultPopupFragment(fbw).show(getParentFragmentManager(), "popup");
                                }

                                @Override
                                public void onGeoQueryError(@NonNull Exception e) {}
                            });




                        }
                    });
                }
            });
            scannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeScanner.startPreview();
                }
            });
        }
        else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    android.Manifest.permission.CAMERA);
        }

        // Set a click listener for the flash button to toggle the camera flash.
        flashButton = root.findViewById(R.id.flash_button);
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCodeScanner.isFlashEnabled()) {
                    mCodeScanner.setFlashEnabled(false);
                    flashButton.setText(R.string.flash_button_label);
                } else {
                    mCodeScanner.setFlashEnabled(true);
                    flashButton.setText(R.string.flash_button_label_off);
                }
            }
        });


        // Set a click listener for the reset button to reset the scanner.
        resetButton = root.findViewById(R.id.fragment_scan_reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Called when the reset button is clicked. Resets the scanner and restores the
             * scanner view to its default state.
             */
            @Override
            public void onClick(View view) {
                scannerView.setForeground(new ColorDrawable(Color.TRANSPARENT));

                // If the code scanner object already exists, release its resources and start the preview.
                // Otherwise, create a new code scanner object and set its decode callback.
                if (mCodeScanner != null) {
                    mCodeScanner.releaseResources();
                    mCodeScanner.startPreview();
                } else {
                    mCodeScanner = new CodeScanner(activity, scannerView);
                    mCodeScanner.setDecodeCallback(new DecodeCallback() {
                        @Override
                        public void onDecoded(@NonNull final Result result) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                                    scannerView.setForeground(new ColorDrawable(Color.TRANSPARENT));

                                }
                            });
                        }
                    });
                    mCodeScanner.startPreview();
                }
            }
        });

        return root;
    }
    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }


    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }


    /**
     *
     * @param preferences
     * @return an intent with preferences as an extra
     */
    public static Intent newIntent(ArrayList<String> preferences){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_GREETING_MESSAGE, preferences);
        return intent;
    }


    /**
     *
     * code that executes after submit button is pressed on dialog fragment
     * - adds data according to chosen preferences
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode != Activity.RESULT_OK ) {
            return;
        }else {

            ArrayList<String> preferences = data.getStringArrayListExtra(EXTRA_GREETING_MESSAGE);

            String code = mResult.getText();
            String date = new Date().toString();
            String locationImg = "placeholder_img";
            ScannedCode scannedCodeToUpload;
            String scannedCodeDID = UUID.randomUUID().toString();
            if(preferences.contains("Allow use of photo")){
//                locationImg = ...
                // TODO: Add photo

            }

            if(preferences.contains("Allow use of geolocation")){
                scannedCodeToUpload = new ScannedCode(code, date, currentLocationGeopoint, locationImg, fbw.getMyUserDID(), scannedCodeDID);

            }
            else {
                scannedCodeToUpload = new ScannedCode(code, date, locationImg, fbw.getMyUserDID(), scannedCodeDID);
            }
            // At this point, scannedCode is ready!
            Map<String, Object> scannedCodeMap = new HashMap<>();
            scannedCodeMap.put("date", scannedCodeToUpload.getDate());
            scannedCodeMap.put("hasLocation", scannedCodeToUpload.getHasLocation());
            scannedCodeMap.put("hash", scannedCodeToUpload.getHash());
            scannedCodeMap.put("hashVal", scannedCodeToUpload.getHashVal());
            scannedCodeMap.put("location", scannedCodeToUpload.getLocation());
            scannedCodeMap.put("locationImage", scannedCodeToUpload.getLocationImage());
            scannedCodeMap.put("userDID", scannedCodeToUpload.getUserDID());
            fbw.db.collection("ScannedCodes").document(scannedCodeDID).set(scannedCodeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    fbw.refreshScannedCodesForUser(fbw.getMyUserDID());
                    // despite the user saying not to make Location public, we still record the `l` location
                    // this is to prevent the user to keep on scanning the code at the same location
                    // despite no public location data
                    geoFirestore.setLocation(scannedCodeDID, currentLocationGeopoint);
                    Toast.makeText(getContext(), "The code has been submitted!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // TODO: (IMPORTANT) MAKE THIS FUNCTION CHOOSE THE BEST LOCATION PROVIDER DURING RUNTIME
//    public Location getCurLocation(){
//
//            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
//                return null;
//            }
//            LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
//            // I could not get the request location updates to work in this fragment
////            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10F, (LocationListener) getContext());
//            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//            if (currentLocation == null) {
//                currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            }
//
//            return currentLocation;
//    }

    // ALTERNATE CODE WHICH WORKED
    public Location getCurLocation(){

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
            return null;
        }
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {}
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        // You can also use LocationManager.NETWORK_PROVIDER for a faster location fix, but it may not be as accurate
        Location currentLocation = null;
        while (currentLocation == null) {
            try {
                // Request location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                // Wait for location updates
                Thread.sleep(1000);
                // Get the current location
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return currentLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                Toast.makeText(getContext(), "Thank you for allowing location", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(getContext(), "Location permission is required for some features", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
