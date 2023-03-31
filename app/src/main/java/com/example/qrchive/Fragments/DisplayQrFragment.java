package com.example.qrchive.Fragments;



import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.ScannedCode;
import com.example.qrchive.R;

import java.util.Hashtable;
import java.util.List;

public class DisplayQrFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_IMAGES = 2;
    private TextView textViewQrName;

    private TextView textViewScore;

    ScannedCode scannedCode;
    private String name;
    private int score;
    private Button nextButton;


    private String scannedCodeDID;
    FirebaseWrapper fbw;
    List<String> selectedPreference;
    public DisplayQrFragment(String scannedCodeDID,ScannedCode scannedCode, FirebaseWrapper fbw, List<String> selectedPreference) {
        this.scannedCodeDID = scannedCodeDID;
        this.selectedPreference = selectedPreference;
        this.scannedCode = scannedCode;
        this.fbw = fbw;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_display_qr, container, false);

        // displaying the qr code name
        textViewQrName = root.findViewById(R.id.fragment_display_qr_name);
        name = scannedCode.getName().toString();
        textViewQrName.setText("Qr code scanned: " + name);
        // displaying number of score

        textViewScore = root.findViewById(R.id.fragment_display_qr_score);
        score = scannedCode.getPoints();
        textViewQrName.setText("Qr code score:" + score);

        // if save image not accepted -> scanFragment
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            scannedCode.setLocationNull();
        } else {
            // Permission already granted, keep the location
        }
        //fragment_display_next
        Button buttonNext = root.findViewById(R.id.fragment_display_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( selectedPreference.size() == 2) {
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new CaptureFragment(scannedCodeDID, scannedCode,fbw))
                            .commit();
                }
                else if (selectedPreference.size() == 1) {
                    int temp = 0;
                    for (int i = 0; i < 2; i++) {
                        if (selectedPreference.get(0).equals("Allow use of geolocation")) {
                        temp = 1;
                        } //
                        if (selectedPreference.get(0).equals("Allow use of photo")) { // upload image to database was accepted
                            // need to save picture but not geolocation
                            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new CaptureFragment(scannedCodeDID, scannedCode,fbw))
                                    .commit();
                        }
                    } // not accepted
                    if ( temp != 1 ) {
                        scannedCode.setLocationNull();
                    }
                    // receive scannedCodeDID through FragmentManager
                    // Added into firestore database if there is no need to save geo and picture
                    fbw.db.collection("ScannedCode").document(scannedCodeDID).set(scannedCode);
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanFragment(fbw))
                            .commit();

                }
                else {
                    fbw.db.collection("ScannedCode").document(scannedCodeDID).set(scannedCode);
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanFragment(fbw))
                            .commit();
                }
            }
        });


        return root;

    }
}
