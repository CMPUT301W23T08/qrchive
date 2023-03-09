package com.example.qrchive.Classes;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.qrchive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseWrapper {
    public FirebaseFirestore db;
    private String myDeviceID;

    private String myUserDID;
    private HashMap<String, ArrayList<ScannedCode>> scannedCodesDict = new HashMap<>();
    private ArrayList<String> users; //part 4

    public FirebaseWrapper(String myUserDID) {
        this.db = FirebaseFirestore.getInstance();
        this.myUserDID = myUserDID;
        this.refreshScannedCodesForUser(myUserDID);
    }

    public void refreshScannedCodesForUser(String userDID) {
        ArrayList<ScannedCode> scannedCodes = new ArrayList<>();
        db.collection("ScannedCodes").whereEqualTo("userDID", userDID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int a = 1;
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        for (DocumentSnapshot document : docs) {
                            Map<String, Object> docData = document.getData();
                            ScannedCode scannedCode = new ScannedCode
                                    (docData.get("hash").toString(),
                                            Integer.parseInt(docData.get("hashVal").toString()),
                                            docData.get("date").toString(),
                                            docData.get("location").toString(),
                                            docData.get("locationImage").toString(),
                                            docData.get("userDID").toString());
                            scannedCodes.add(scannedCode);
                        }

                        // Default sort would be newest to oldest
                        scannedCodes.sort(Collections.reverseOrder(Comparator.comparing(ScannedCode::getDate)));
                        scannedCodesDict.put(userDID, scannedCodes);
                    }
                });
    }


    public String getMyUserDID() {
        return myUserDID;
    }

    public HashMap<String, ArrayList<ScannedCode>> getScannedCodesDict() {
        return scannedCodesDict;
    }

    public ArrayList<String> getUsers() {
        return users;
    }
}
