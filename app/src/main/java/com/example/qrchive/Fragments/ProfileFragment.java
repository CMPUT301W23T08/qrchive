package com.example.qrchive.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Activities.MainActivity;
import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.OnQRCountQueryListener;
import com.example.qrchive.Classes.Player;
import com.example.qrchive.Classes.ScannedCode;
import com.example.qrchive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Profile Fragment
 *
 * @author Zayd & Grayden
 */
public class ProfileFragment extends Fragment {


    public Player user;
    private FirebaseWrapper fbw;
    public FirebaseFirestore db;
    public ProfileFragment(Player user, FirebaseWrapper fbw) {
        this.fbw = fbw;
        this.user = user;
    }

    /**
     * @return A new instance of fragment ProfileFragment.
     */
//    public static ProfileFragment newInstance() {
//        ProfileFragment fragment = new ProfileFragment();
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * onCreateView does a multitude of tasks, it first sets up all of the textViews and buttons to display properly
     * then it sets up the buttons to have listeners that perform the appropriate actions.
     *
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
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);

        Button editFollowFBtn = profileView.findViewById(R.id.profile_edit_button);
        Button deleteBtn = profileView.findViewById(R.id.delete_account_button);

        TextView userNameTextView = (TextView)profileView.findViewById(R.id.profile_username);
        TextView emailTextView = (TextView)profileView.findViewById(R.id.profile_email_address);
        TextView userIdTextView = (TextView)profileView.findViewById(R.id.profile_user_id);
        TextView userRankTextView = (TextView)profileView.findViewById(R.id.profile_user_rank);
        TextView qrCodeTextView = (TextView)profileView.findViewById(R.id.profile_qr_codes_collected);

        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        String userId = preferences.getString("userDID", "no user id found");
        String deviceID = preferences.getString("deviceID", "no user id found");

        userNameTextView.setText(user.getUserName());
        emailTextView.setText(user.getEmail());
        userIdTextView.setText(user.getDeviceID());
        System.out.println(user.getDeviceID());
        fbw.getUserRank(user.getDeviceID(), new FirebaseWrapper.OnRankRetrievedListener() {
            @Override
            public void OnRankRetrieved(int rank) {
                userRankTextView.setText("Rank: #" + Integer.toString(rank));
            }
        }, false);

        if(deviceID.equals(user.getDeviceID())){
            deleteBtn.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "wkaaksdf", Toast.LENGTH_SHORT);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fbw.deleteUser();
                    // sleep for 2 second
                    Toast.makeText(getContext(),
                            "Deleted user account and associated data, restarting...",  Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
//                    startActivity(new Intent(getContext(), MainActivity.class));
                }
            });


        }else{
            db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    boolean followed = false;
                    ArrayList<String> friends = (ArrayList<String>) task.getResult().getData().get("friends");
                    for(String friend : friends){
                        System.out.println(friend);
                        if(Objects.equals(friend, user.getDeviceID())){
                            followed = true;
                            System.out.println("followed");
                        }
                    }

                    if(followed){
                        editFollowFBtn.setText("Unfollow");
                        deleteBtn.setVisibility(View.INVISIBLE);

                        editFollowFBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                System.out.println("clicking");
                                db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        ArrayList<String> friends = (ArrayList<String>) task.getResult().getData().get("friends");
                                        friends.remove(user.getDeviceID());
                                        HashMap<String, Object> newUser = new HashMap<>();
                                        newUser.put("friends", friends);
                                        db.collection("Users").document(userId).set(newUser, SetOptions.merge());
                                    }
                                });
                            }
                        });
                    }else{
                        editFollowFBtn.setText("Follow");
                        deleteBtn.setVisibility(View.INVISIBLE);

                        editFollowFBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                System.out.println("clicking");
                                db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        ArrayList<String> friends = (ArrayList<String>) task.getResult().getData().get("friends");
                                        if(!friends.contains(user.getDeviceID()))
                                            friends.add(user.getDeviceID());
                                        HashMap<String, Object> newUser = new HashMap<>();
                                        newUser.put("friends", friends);
                                        db.collection("Users").document(userId).set(newUser, SetOptions.merge());
                                    }
                                });
                            }
                        });
                    }
                }
            });

        }


        user.getQRCount(new OnQRCountQueryListener() {
            @Override
            public void onQRCount(int count) {
                qrCodeTextView.setText("QR Codes Collected: " + Integer.toString(count));
            }

            @Override
            public void onError(String errorMessage) {
                qrCodeTextView.setText("QR codes collected: error");
            }
        });

        //TODO: get favorite qrcode

        return profileView;
    }
}