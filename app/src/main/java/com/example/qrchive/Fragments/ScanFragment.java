package com.example.qrchive.Fragments;


import android.app.Activity;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;


/**
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment {
    private static final int TARGET_FRAGMENT_REQUEST_CODE = 1;
    private static final String EXTRA_GREETING_MESSAGE ="EXTRA_PREFERENCES";
    private CodeScanner mCodeScanner;

    private CodeScannerView scannerView;
    private Button resetButton;
    private Button flashButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_scan, container, false);

        scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        if (ContextCompat.checkSelfPermission(
                activity, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {

            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                            // can get result of the scan with result.getText()
                            scannerView.setForeground(new ColorDrawable(Color.TRANSPARENT));

                            new ScanResultPopupFragment().show(getChildFragmentManager(), "popup");



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

            if(preferences.contains("Allow use of photo")){
                // TODO: Add photo
            }

            if(preferences.contains("Allow use of geolocation")){
                // TODO: add location
            }

            //TODO: add other data

        }
    }
}
