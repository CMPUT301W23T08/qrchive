package com.example.qrchive.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.example.qrchive.Classes.MyScannedCodeCardRecyclerViewAdapter;
import com.example.qrchive.R;

import com.example.qrchive.Classes.ScannedCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * create an instance of this fragment.
 */
public class CodesFragment extends Fragment {

    private List<ScannedCode> scannedCodes;
    private MyScannedCodeCardRecyclerViewAdapter scannedCodesAdapter;
    public CodesFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment CodesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CodesFragment newInstance(String param1, String param2) {
        return new CodesFragment();
    }

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
        scannedCodes = new ArrayList<>();
        scannedCodesAdapter = new MyScannedCodeCardRecyclerViewAdapter(scannedCodes);

        db.collection("ScannedCodes").whereEqualTo("userDID", preferences.getString("userDID", "")).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int a = 1;
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        for (DocumentSnapshot document : docs) {
                            Map<String, Object> docData = document.getData();
                            ScannedCode scannedCode = new ScannedCode
                                    (document.getId(),
                                            docData.get("codeDID").toString(),
                                            docData.get("date").toString(),
                                            docData.get("location").toString(),
                                            docData.get("userDID").toString());
                            scannedCodes.add(scannedCode);
                        }

                        // Default sort would be newest to oldest
                        scannedCodes.sort(Collections.reverseOrder(Comparator.comparing(ScannedCode::getDate)));
                        scannedCodesAdapter.notifyDataSetChanged();
                        ((TextView) view.findViewById(R.id.bottom_qrs_card_text)).setText(String.valueOf(scannedCodes.size()));
                        ((TextView) view.findViewById(R.id.bottom_pts_card_text)).setText(String.valueOf(
                                scannedCodes.stream().mapToInt(ScannedCode::getPoints).sum()
                        ));
                    }
                });

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