package com.example.qrchive.Fragments;


import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    private CodeScannerView scannerView;
    private Button resetButton;
    private GeoPoint currentLocationGeopoint;
    private Button flashButton;
    boolean withinImpermissibleRadius;

    public ScanFragment(FirebaseWrapper fbw) {
        this.fbw = fbw;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_scan, container, false);

        scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);

        db = fbw.db;
        geoFirestore = new GeoFirestore(db.collection("ScannedCodes"));
        if (ContextCompat.checkSelfPermission(
                activity, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {

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

                            geoFirestore.queryAtLocation(currentLocationGeopoint, IMPERMISSIBLE_RADIUS).addGeoQueryEventListener(new GeoQueryEventListener() {
                                @Override
                                public void onKeyEntered(@NonNull String key, @NonNull GeoPoint geoPoint) {
                                }
                                @Override
                                public void onKeyExited(@NonNull String s) {}
                                @Override
                                public void onKeyMoved(@NonNull String s, @NonNull GeoPoint geoPoint) {}
                                @Override
                                public void onGeoQueryReady() {
                                    db.collection("ScannedCodes")
                                            .whereEqualTo("userDID", fbw.getMyUserDID())
                                            .whereEqualTo("hash", Hashing.sha256().hashString(mResult.getText(), StandardCharsets.UTF_8).toString())
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    // number of SAME qr codes by the same user within the impermissible radius
                                                    int docsNumber = task.getResult().getDocuments().size();
                                                    withinImpermissibleRadius = docsNumber != 0;
                                                    if (withinImpermissibleRadius) {
                                                        Toast.makeText(activity, "You have scanned this code within this area already. This QRCode will not be added", Toast.LENGTH_SHORT).show();
                                                        return; // TODO test this
                                                    }
                                                    new ScanResultPopupFragment().show(getChildFragmentManager(), "popup");
                                                }
                                            });
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


        resetButton = root.findViewById(R.id.fragment_scan_reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scannerView.setForeground(new ColorDrawable(Color.TRANSPARENT));
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
            if(preferences.contains("Allow use of photo")){
//                locationImg = ...
                // TODO: Add photo

            }

            if(preferences.contains("Allow use of geolocation")){
                // TODO: add location
                scannedCodeToUpload = new ScannedCode(code, date, currentLocationGeopoint, locationImg, fbw.getMyUserDID());

            }
            else {
                scannedCodeToUpload = new ScannedCode(code, date, locationImg, fbw.getMyUserDID());
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
            fbw.db.collection("ScannedCodes").document().set(scannedCodeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    fbw.refreshScannedCodesForUser(fbw.getMyUserDID());
                }
            });
        }
    }

    // TODO: (IMPORTANT) MAKE THIS FUNCTION CHOOSE THE BEST LOCATION PROVIDER DURING RUNTIME
    public Location getCurLocation(){

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
                return null;
            }
            LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            // I could not get the request location updates to work in this fragment
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10F, (LocationListener) getContext());
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            return currentLocation;
    }

    // ALTERNATE CODE WHICH WORKED
//    public Location getCurLocation(){
//
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
//            return null;
//        }
//        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
//
//        if (locationManager != null) {
//            LocationListener locationListener = new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) {}
//                @Override
//                public void onStatusChanged(String provider, int status, Bundle extras) {}
//                @Override
//                public void onProviderEnabled(String provider) {}
//                @Override
//                public void onProviderDisabled(String provider) {}
//            };
//
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//            // You can also use LocationManager.NETWORK_PROVIDER for a faster location fix, but it may not be as accurate
//        }
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        return location;
//    }

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
