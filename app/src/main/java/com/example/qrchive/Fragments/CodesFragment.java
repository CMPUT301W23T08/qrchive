package com.example.qrchive.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.MyScannedCodeCardRecyclerViewAdapter;
import com.example.qrchive.R;

import com.example.qrchive.Classes.ScannedCode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * create an instance of this fragment.
 */
public class CodesFragment extends Fragment {

    private List<ScannedCode> scannedCodes;
    private FirebaseWrapper fbw;
    private MyScannedCodeCardRecyclerViewAdapter scannedCodesAdapter;
    public CodesFragment(FirebaseWrapper fbw) {
        this.fbw = fbw;
    }

//    /**
//     * @return A new instance of fragment CodesFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static CodesFragment newInstance(String param1, String param2) {
//        return new CodesFragment();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_codes, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_list);


        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        scannedCodes = fbw.getScannedCodesDict().get(fbw.getMyUserDID());
        scannedCodesAdapter = new MyScannedCodeCardRecyclerViewAdapter(scannedCodes);
        scannedCodesAdapter.setOnItemClickListener(new MyScannedCodeCardRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                ScannedCode scannedCode = scannedCodes.get(position);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new OnClickCodeFragment(scannedCode, fbw))
                        .addToBackStack(null)
                        .commit();
            }
        });


        scannedCodesAdapter.notifyDataSetChanged(); /* todo, this CAN be the reason why Codes
                                                    /* fragment is empty (race condition between
                                                    / *this line and FirebaseWrapper still
                                                    / *initializing the codes for this user) */
        ((TextView) view.findViewById(R.id.bottom_qrs_card_text)).setText(String.valueOf(scannedCodes.size()));
        ((TextView) view.findViewById(R.id.bottom_pts_card_text)).setText(String.valueOf(
                scannedCodes.stream().mapToInt(ScannedCode::getPoints).sum()
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(scannedCodesAdapter);

        Spinner sortedBySpinner = view.findViewById(R.id.sorted_by_spinner);
        sortedBySpinner.setAdapter(new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Date", "Location", "Points", "Name"}));
        sortedBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                switch (selectedItem) {
                    case "Points":
                        scannedCodes.sort(Collections.reverseOrder(Comparator.comparingInt(ScannedCode::getPoints)));
                        break;
                    case "Location":
                        scannedCodes.sort(Comparator.comparing(ScannedCode::getLocation));
                        break;
                    case "Date":
                        scannedCodes.sort(Collections.reverseOrder(Comparator.comparing(ScannedCode::getDate)));
                        break;
                    case "Name":
                        scannedCodes.sort(Comparator.comparing(ScannedCode::getName));
                        break;
                }
                scannedCodesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }
}