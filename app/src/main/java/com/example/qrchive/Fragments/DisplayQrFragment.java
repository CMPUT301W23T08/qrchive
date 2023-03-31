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

public class DisplayQrFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_IMAGES = 2;
    private TextView textViewQrName;

    private TextView textViewScore;

    ScannedCode scannedCode;
    private String name;
    private int score;
    private Button acceptLocation;
    private Button acceptImage;
    FirebaseWrapper fbw;

    public DisplayQrFragment(ScannedCode scannedCode, FirebaseWrapper fbw) {
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
//        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanFragment(fbw))
//                .commit();
//        // if save image accepted -> CaptureFragment
//        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new CaptureFragment(fbw))
//                .commit();

        return root;
    }


}
