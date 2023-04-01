package com.example.qrchive.Classes;

import static java.security.AccessController.getContext;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.qrchive.Fragments.FriendsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Player {

    private String email;
    private String playerName;
    private String deviceID;
    private String userDID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();




    public Player(String playerName, String email, String deviceID, String userDID){
        this.playerName = playerName;
        this.email = email;
        this.deviceID = deviceID;
        this.userDID = userDID;

    }


    public String getEmail(){return this.email;}
    public String getUserName(){return this.playerName;}
    public String getDeviceID(){return this.deviceID;}


    public int getPoints(){
        //TODO: get player points
        return 0;
    }

    public void getQRCount(final OnQRCountQueryListener listener) {
        db.collection("ScannedCodes")
                .whereEqualTo("userDID", userDID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            listener.onQRCount(count);
                        } else {
                            listener.onError(task.getException().getMessage());
                        }
                    }
                });
    }
}
