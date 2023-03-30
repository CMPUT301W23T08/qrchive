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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

// In the ScannedCodes collection on firebase, the userDID, location, hasLocation, hash can ...
// ... uniquely identify a QR code

/**
 * A wrapper class for Firebase that allows developers to easily manipulate the Firebase DB.
 *
 * @author Shelly
 * @version 1.0
 */
public class FirebaseWrapper {
    public FirebaseFirestore db;
    private String myDeviceID;

    private String myUserDID;
    private HashMap<String, ArrayList<ScannedCode>> scannedCodesDict = new HashMap<>();
    private ArrayList<String> users; //part 4

    /**
     * FirebaseWrapper constructor, instantiates a single instance of the FirebaseWrapper class.
     *
     * @param myUserDID is the device ID of the user we want to operate on.
     */
    public FirebaseWrapper(String myUserDID) {
        this.db = FirebaseFirestore.getInstance();
        this.myUserDID = myUserDID;
        this.refreshScannedCodesForUser(myUserDID);
    }

    /**
     * refreshScannedCodesForUser will re-initialize the scannedCodesDict with all of the users
     * scanned codes.
     *
     * @param userDID is the user document ID we are trying to fetch the codes for.
     */
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
                                            (GeoPoint) docData.get("location"),
                                            (boolean) docData.get("hasLocation"),
                                            docData.get("locationImage").toString(),
                                            docData.get("userDID").toString(),
                                            document.getId());
                            scannedCodes.add(scannedCode);
                        }

                        // Default sort would be newest to oldest
                        scannedCodes.sort(Collections.reverseOrder(Comparator.comparing(ScannedCode::getDate)));
                        scannedCodesDict.put(userDID, scannedCodes);
                    }
                });
    }


    /**
     * getMyUserDID is a getter function for myUserDID.
     *
     * @return Returns private attribute myUserDID.
     */
    public String getMyUserDID() {
        return myUserDID;
    }

    /**
     * getScannedCodesDict is a getter function for scannedCodesDict.
     *
     * @return Returns private attribute scannedCodesDict.
     */
    public HashMap<String, ArrayList<ScannedCode>> getScannedCodesDict() {
        return scannedCodesDict;
    }

    /**
     * deleteCode will delete a ScannedCode from the firebase DB and maintain the scannedCodesDict.
     *
     * @param scannedCode is the code we wish to delete from the DB.
     */
    public void deleteCode(ScannedCode scannedCode) {
//        Tasks.await()
        db.collection("ScannedCodes").document(scannedCode.getScannedCodeDID())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

    }

    public void deleteUser(){
        System.out.println("user id:  " + myUserDID);
        db.collection("Users").document(myUserDID)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    /**
     * getUsers is a getter function for users.
     * @return Returns private attribute users.
     */
    public ArrayList<String> getUsers() {
        return users;
    }
}
