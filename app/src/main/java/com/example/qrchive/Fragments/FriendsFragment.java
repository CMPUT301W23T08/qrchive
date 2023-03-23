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
import android.widget.Toast;

import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.FriendsRecyclerViewAdapter;
import com.example.qrchive.Classes.Player;
import com.example.qrchive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    private FirebaseWrapper fbw;

    public FirebaseFirestore db;

    private ArrayList<Player> users;
    private String userId;
    private ArrayList<String> friendsList;


    public FriendsFragment(FirebaseWrapper fbw) {
        this.fbw = fbw;


    }

    /**
     * @return A new instance of fragment FriendsFragment.
     */
//    public static FriendsFragment newInstance() {
//        FriendsFragment fragment = new FriendsFragment(fbw);
//
//        return fragment;
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.db = FirebaseFirestore.getInstance();
        this.users = new ArrayList<>();

        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        this.userId = preferences.getString("userDID", "no user id found");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View friendsView = inflater.inflate(R.layout.fragment_friends, container, false);


        //get list of friends

        getFriendsList(new OnFriendsRetrievedListener() {
            @Override
            public void onFriendsRetrieved(ArrayList<String> friends) {


                getFriends(new OnUsersRetrievedListener() {
                    @Override
                    public void onUsersRetrieved(ArrayList<Player> users) {
                        // The list of users is now available, you can update your UI or perform any necessary actions here
                        System.out.println("User count: " + users.size());


                        RecyclerView recyclerView = friendsView.findViewById(R.id.friends_recycler_list);

                        FriendsRecyclerViewAdapter friendsAdapter = new FriendsRecyclerViewAdapter(users);
                        recyclerView.setAdapter(friendsAdapter);

                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }, friends);





            }
        }, userId);

        // Call the getUsers() method and provide a callback to handle the retrieved list of users








//        // Find the RecyclerView in your layout
//        recyclerView = findViewById(R.id.recyclerView);
//
//        // Create a new PlayerAdapter and set it as the RecyclerView's adapter
//        playerAdapter = new PlayerAdapter(playerList);
//        recyclerView.setAdapter(playerAdapter);
//
//        // Set the RecyclerView's layout manager
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        return friendsView;
    }


    /**
     * Funciton will get all users
     *
     * @param listener
     */

    private void getAllUsers(final OnUsersRetrievedListener listener) {
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    ArrayList<Player> users = new ArrayList<>();
                    for (DocumentSnapshot document : docs) {
                        Map<String, Object> docData = document.getData();
                        Player player = new Player(
                                (String) docData.get("userName"),
                                (String) docData.get("emailID"),
                                (String) docData.get("deviceID")
                        );
                        users.add(player);
                    }
                    // Invoke the callback method with the list of users as a parameter
                    listener.onUsersRetrieved(users);
                } else {
                    Toast.makeText(getContext(), "no users", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    /**
     * function will get all friends
     *
     * @param listener
     */
    private void getFriends(final OnUsersRetrievedListener listener, ArrayList<String> friendsList) {


        db.collection("Users").whereIn("deviceID", (friendsList)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    ArrayList<Player> users = new ArrayList<>();
                    for (DocumentSnapshot document : docs) {
                        Map<String, Object> docData = document.getData();
                        Player player = new Player(
                                (String) docData.get("userName"),
                                (String) docData.get("emailID"),
                                (String) docData.get("deviceID")
                        );
                        users.add(player);

                    }


                    // Invoke the callback method with the list of users as a parameter
                    listener.onUsersRetrieved(users);
                } else {
                    Toast.makeText(getContext(), "no users", Toast.LENGTH_SHORT);
                }
            }
        });
    }


    private void getFriendsList(final OnFriendsRetrievedListener listener, String userId) {

        db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    ArrayList<String> friends = (ArrayList<String>) doc.get("friends");

                    listener.onFriendsRetrieved(friends);
                } else {
                    Toast.makeText(getContext(), "no users", Toast.LENGTH_SHORT);
                }
            }
        });

    }


    public interface OnFriendsRetrievedListener {
        void onFriendsRetrieved(ArrayList<String> friends);
    }

    public interface OnUsersRetrievedListener {
        void onUsersRetrieved(ArrayList<Player> users);
    }


}