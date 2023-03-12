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
import com.example.qrchive.R;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;



/**
 * The ScanFragment class is responsible for scanning QR codes using the device camera.
 * It displays a preview of the camera feed on the screen, and decodes any QR codes that
 * are detected. The class also provides buttons for resetting the scanner and toggling
 * the camera flash.
 */

public class ScanFragment extends Fragment {

     private static final int TARGET_FRAGMENT_REQUEST_CODE = 1;
     private static final String EXTRA_GREETING_MESSAGE = "EXTRA_PREFERENCES";
     private static final int REQUEST_CODE_FINE_LOCATION = 200;

    private CodeScanner mCodeScanner;
    // The scanner view displays the camera preview on the screen.
    private CodeScannerView scannerView;
    // The reset button resets the scanner when clicked.
    private Button resetButton;
    // The flash button toggles the camera flash when clicked.
    private Button flashButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_scan, container, false);

        // Initialize the scanner view and code scanner objects.
        scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);

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
                            Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                            // can get result of the scan with result.getText()
                            scannerView.setForeground(new ColorDrawable(Color.TRANSPARENT));

                            SharedPreferences preferences = activity.getSharedPreferences("preferences", MODE_PRIVATE); // to get user name and other user information

                            Location currentLocation = getCurLocation();

                            boolean withinRadius = false;
                            //TODO: check if user has scanned the same code
                            boolean sameCode = false; // this will be the placeholder for if the user has scanned the same code

                            if(sameCode){
                                //TODO: check to see if the qrcode has been scanned within a certain radius
                                withinRadius = true;
                            }

                            if(withinRadius && sameCode ){
                                Toast.makeText(activity, "You have scanned this code within this area already. This QRCode will not be added", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                new ScanResultPopupFragment().show(getChildFragmentManager(), "popup");
                            }
                        }
                    });
                }
            });
            // Start the preview when the scanner view is clicked.
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

            if(preferences.contains("Allow use of photo")){
                // TODO: Add photo
            }

            if(preferences.contains("Allow use of geolocation")){
                // TODO: add location
            }

            //TODO: add other data

        }
    }

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
