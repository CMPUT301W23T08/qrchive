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

import com.example.qrchive.Classes.MyScannedCodeCardRecyclerViewAdapter;
import com.example.qrchive.R;

import com.example.qrchive.Classes.ScannedCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                        scannedCodesAdapter.notifyDataSetChanged();
                    }
                });

}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_codes, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(scannedCodesAdapter);
        }
        return view;
    }
}