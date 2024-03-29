package com.example.qrchive.Fragments;
import static android.content.Intent.getIntent;
import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.qrchive.Activities.MainActivity;
import com.example.qrchive.R;
import com.google.firestore.bundle.BundleElement;

/** Settings Fragment
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * displays the settings view and allows the user to change the location preferences.
     * Inflates the layout for this fragment and sets an {@code OnClickListener} on the "changeLocPrefBtn" button that launches an intent to the location settings screen when clicked.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        Button changeLocPrefBtn = settingsView.findViewById(R.id.cnageLocPrefBtn);

        changeLocPrefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

        });

        return settingsView;
    }
}