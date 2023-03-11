package com.example.qrchive.Fragments;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.qrchive.R;
import com.google.zxing.Result;



/**
 * The ScanFragment class is responsible for scanning QR codes using the device camera.
 * It displays a preview of the camera feed on the screen, and decodes any QR codes that
 * are detected. The class also provides buttons for resetting the scanner and toggling
 * the camera flash.
 */

public class ScanFragment extends Fragment {

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
}
