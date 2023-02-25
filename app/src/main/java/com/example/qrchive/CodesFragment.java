package com.example.qrchive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create an instance of this fragment.
 */
public class CodesFragment extends Fragment {
    FirebaseFirestore db = MainActivity.db;
//    private ArrayAdapter<Map<String, Object>> ScannedCodeAdapter;

    public CodesFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment CodesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CodesFragment newInstance(String param1, String param2) {
        CodesFragment fragment = new CodesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Firestore init
//        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_codes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<ScannedCode> scannedCodes = new ArrayList<>();
        scannedCodes.add(new ScannedCode("abc", "def", "ghi", "jkl", "mno"));
        RecyclerView scannedCodeCardRecyclerView = view.findViewById(R.id.codes_recyclerview);
        ScannedCodeAdapter scannedCodeAdapter = new ScannedCodeAdapter(view.getContext(), scannedCodes);
        scannedCodeCardRecyclerView.setAdapter(scannedCodeAdapter);
        scannedCodeCardRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);

//        db.collection("ScannedCodes").whereEqualTo("userDID", preferences.getString("userDID", "")).get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        int a = 1;
//                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
//                        for (DocumentSnapshot document : docs) {
//                            Map<String, Object> docData = document.getData();
//                            ScannedCode scannedCode = new ScannedCode
//                                    (document.getId(),
//                                            docData.get("codeDID").toString(),
//                                            docData.get("date").toString(),
//                                            docData.get("location").toString(),
//                                            docData.get("userDID").toString());
//                            scannedCodes.add(scannedCode);
//                        }
//                        scannedCodeAdapter.notifyDataSetChanged();
//                    }
//                });

    }
}