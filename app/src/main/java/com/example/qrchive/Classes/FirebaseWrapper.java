package com.example.qrchive.Classes;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String myUserName;
    private HashMap<String, ArrayList<ScannedCode>> scannedCodesDict = new HashMap<>();
    private ArrayList<String> users = new ArrayList<>(); //part 4
    private HashMap<String, ScannedCode> topCodeForUser = new HashMap<>();
    private HashMap<String, Integer> userRank = new HashMap<>();
    private HashMap<String, String> deviceToDoc = new HashMap<>();
    private final FirebaseStorage storage = FirebaseStorage.getInstance("gs://qrchive-images");


    /**
     * FirebaseWrapper constructor, instantiates a single instance of the FirebaseWrapper class.
     *
     * @param myUserDID is the device ID of the user we want to operate on.
     */
    public FirebaseWrapper(String myUserDID, String myUserName) {
        this.db = FirebaseFirestore.getInstance();
        this.myUserDID = myUserDID;
        this.myUserName = myUserName;
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

    public void getAllUsers(final OnUsersRetrievedListener listener){
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                for (DocumentSnapshot document : docs) {
                    Map<String, Object> docData = document.getData();
                    users.add(document.getId());
                    deviceToDoc.put((String) docData.get("deviceID"), document.getId());
                }

                listener.onUsersRetrieved(users, deviceToDoc);
            }
        });
    }

    public void getTopScoreForUsers(final OnScoresRetrievedListener listener){
        db.collection("ScannedCodes").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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

                            if(!topCodeForUser.containsKey((String) docData.get("userDID")) || scannedCode.getPoints() > topCodeForUser.get((String) docData.get("userDID")).getPoints()){
                                topCodeForUser.put((String) docData.get("userDID"), scannedCode);
                            }
                        }

                        listener.onScoresRetrieved(topCodeForUser);
                    }
                });
    }

    public void createRanklist(final OnRanklistRetrievedListener listener){
        getAllUsers(new OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(ArrayList<String> users, HashMap<String, String> deviceToDoc) {
                getTopScoreForUsers(new OnScoresRetrievedListener() {
                    @Override
                    public void onScoresRetrieved(HashMap<String, ScannedCode> topCodeForUser) {
                        ArrayList<Pair<Integer, String>> rankList = new ArrayList<>();

                        for(String key : topCodeForUser.keySet()){
                            Pair<Integer, String> keyPair = new Pair<Integer, String>(topCodeForUser.get(key).getPoints(), key);
                            rankList.add(keyPair);
                        }

                        // Sort in descending order of score
                        Collections.sort(rankList, Comparator.comparing(p -> -p.first));

                        int i = 0;
                        for(; i < rankList.size(); ++i){
                            userRank.put(rankList.get(i).second, i+1);
                        }
                        for(String user : users){
                            if(!userRank.containsKey(user)){
                                userRank.put(user, i++);
                            }
                        }

                        listener.OnRanklistRetrieved(userRank, topCodeForUser);
                    }
                });
            }
        });
    }

    public void getUserRank(String userDeviceID, final OnRankRetrievedListener listener, boolean rawVal){
        String docID = deviceToDoc.get(userDeviceID);

        if(docID == null){
            getAllUsers(new OnUsersRetrievedListener() {
                @Override
                public void onUsersRetrieved(ArrayList<String> users, HashMap<String, String> deviceToDoc) {
                    System.out.println(userDeviceID);
                    String docID = deviceToDoc.get(userDeviceID);
                    System.out.println(deviceToDoc);
                    System.out.println(docID);
                    if(userRank.get(docID) == null){
                        createRanklist(new OnRanklistRetrievedListener() {
                            @Override
                            public void OnRanklistRetrieved(HashMap<String, Integer> userRank, HashMap<String, ScannedCode> topCodeForUser) {
                                System.out.println(userRank);
                                System.out.println(userRank.get(docID));
                                if(!rawVal){
                                    listener.OnRankRetrieved(userRank.get(docID));
                                }else{
                                    ScannedCode top = topCodeForUser.get(docID);
                                    if(top == null){
                                        listener.OnRankRetrieved(0);
                                    }else{
                                        listener.OnRankRetrieved(topCodeForUser.get(docID).getPoints());
                                    }
                                }
                            }
                        });
                    }else{
                        if(!rawVal){
                            listener.OnRankRetrieved(userRank.get(docID));
                        }else{
                            ScannedCode top = topCodeForUser.get(docID);
                            if(top == null){
                                listener.OnRankRetrieved(0);
                            }else{
                                listener.OnRankRetrieved(topCodeForUser.get(docID).getPoints());
                            }
                        }
                    }
                }
            });
        }else{
            if(userRank.get(docID) == null){
                createRanklist(new OnRanklistRetrievedListener() {
                    @Override
                    public void OnRanklistRetrieved(HashMap<String, Integer> userRank, HashMap<String, ScannedCode> topCodeForUser) {
                        System.out.println(userRank);
                        System.out.println(docID);
                        if(!rawVal){
                            listener.OnRankRetrieved(userRank.get(docID));
                        }else{
                            ScannedCode top = topCodeForUser.get(docID);
                            if(top == null){
                                listener.OnRankRetrieved(0);
                            }else{
                                listener.OnRankRetrieved(topCodeForUser.get(docID).getPoints());
                            }
                        }
                    }
                });
            }else{
                if(!rawVal){
                    listener.OnRankRetrieved(userRank.get(docID));
                }else{
                    ScannedCode top = topCodeForUser.get(docID);
                    if(top == null){
                        listener.OnRankRetrieved(0);
                    }else{
                        listener.OnRankRetrieved(topCodeForUser.get(docID).getPoints());
                    }
                }
            }

        }
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
     * @param scannedCodeDID is the codeDID we wish to delete from the DB.
     */
    public void deleteCode(String scannedCodeDID) {
        // Delete the code
        db.collection("ScannedCodes").document(scannedCodeDID)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        // Delete the image
        storage.getReference(scannedCodeDID + ".jpg")
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    public void deleteUser(){
        System.out.println("user id:  " + myUserDID);
        db.collection("Users").document(myUserDID)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Delete all user's associated data
                        // Deleting comments
                        db.collection("Comments")
                                .whereEqualTo("userName", myUserName)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                                        for (DocumentSnapshot doc : docs) {
                                            doc.getReference().delete();
                                        }
                                    }
                                });

                        // Deleting pending friend requests
                        db.collection("FriendRequest")
                                .whereEqualTo("userName", myUserName)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                                        for (DocumentSnapshot doc : docs) {
                                            doc.getReference().delete();
                                        }
                                    }
                                });

                        // Deleting scanned codes
                        db.collection("ScannedCodes")
                                .whereEqualTo("userDID", myUserDID)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                                        for (DocumentSnapshot doc : docs) {
                                            deleteCode(doc.getId());
                                        }
                                    }
                                });
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

    public void uploadImage(byte[] bytes, String scannedCodeDID) {
        StorageReference storageRef = storage.getReference(scannedCodeDID + ".jpg");
        UploadTask uploadTask = storageRef.putBytes(bytes);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

            }
        });
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public interface OnUsersRetrievedListener {
        void onUsersRetrieved(ArrayList<String> users, HashMap<String, String> deviceToDoc);
    }
    public interface OnScoresRetrievedListener {
        void onScoresRetrieved(HashMap<String, ScannedCode> topCodeForUser);
    }

    public interface OnRanklistRetrievedListener {
        void OnRanklistRetrieved(HashMap<String, Integer> userRank, HashMap<String, ScannedCode> topCodeForUser);
    }

    public interface OnRankRetrievedListener {
        void OnRankRetrieved(int rank);
    }
}
