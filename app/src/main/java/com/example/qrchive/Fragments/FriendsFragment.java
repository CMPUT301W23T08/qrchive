package com.example.qrchive.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.FriendsRecyclerViewAdapter;
import com.example.qrchive.Classes.Player;
import com.example.qrchive.Classes.ScannedCode;
import com.example.qrchive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * displays list of fiends or list of all users
 *
 * @author Zayd & Grayden
 */
public class FriendsFragment extends Fragment {

    private FirebaseWrapper fbw;

    public FirebaseFirestore db;

    private ArrayList<Player> users;
    private String userId;
    private String deviceID;
    private ArrayList<String> friendsList;
    private SharedPreferences onStartupPref;
    Button showFriendsButton;
    Button showRanklistButton;


    /**
     * The Constructor for the FriendsFragment.
     * @param fbw is a FirebaseWrapper that allows persistence to minimize the amount of queries needed to be done.
     */
    public FriendsFragment(FirebaseWrapper fbw) {this.fbw = fbw;}

    /**
     * @return A new instance of fragment FriendsFragment.
     */
//    public static FriendsFragment newInstance() {
//        FriendsFragment fragment = new FriendsFragment(fbw);
//
//        return fragment;
//    }

    /**
     * onCreate will initialize an instance of the fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.db = FirebaseFirestore.getInstance();
        this.users = new ArrayList<>();

        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        this.userId = preferences.getString("userDID", "no user id found");
        this.deviceID = preferences.getString("deviceID", "no device id found");


    }

    /**
     * onCreateView will first set up the display of each button and the recyclerview and then it will
     * set up listeners.
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
        View friendsView = inflater.inflate(R.layout.fragment_friends, container, false);

        onStartupPref = getActivity().getSharedPreferences("friendsStartupPref", Context.MODE_PRIVATE);

        Boolean showFriendsOnStart = onStartupPref.getBoolean("showFriends", true);

        showFriendsButton = friendsView.findViewById(R.id.show_friends_button);
        showRanklistButton = friendsView.findViewById(R.id.show_all_button);

        RecyclerView recyclerView = friendsView.findViewById(R.id.friends_recycler_list);

        if(showFriendsOnStart){
            displayFriends(recyclerView);
        }else{
            displayAllUsers(recyclerView);
        }


        showFriendsButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                displayFriends(recyclerView);
            }
        });

        showRanklistButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                displayAllUsers(recyclerView);
            }
        });
        return friendsView;
    }

    /**
     * displays the list of friends to the recycler view
     * @param recyclerView
     */
    private void displayFriends(RecyclerView recyclerView){

        onStartupPref.edit().putBoolean("showFriends", true).apply();
        showFriendsButton.setTextColor(Color.rgb(0, 0, 0));
        showRanklistButton.setTextColor(Color.rgb(255,255,255));

        getFriendsList(new OnFriendsRetrievedListener() {
            @Override
            public void onFriendsRetrieved(ArrayList<String> friends) {


                getFriends(new OnUsersRetrievedListener() {
                    @Override
                    public void onUsersRetrieved(ArrayList<Player> users) {
                        // The list of users is now available, you can update your UI or perform any necessary actions here



                        FriendsRecyclerViewAdapter friendsAdapter = new FriendsRecyclerViewAdapter(users);
                        setClickListener(friendsAdapter, users);
                        recyclerView.setAdapter(friendsAdapter);

                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }, friends);
            }
        });

    }

    /**
     * displays the lsit of all users to the recycler view
     * @param recyclerView
     */
    private void displayAllUsers(RecyclerView recyclerView){
        onStartupPref.edit().putBoolean("showFriends", false).apply();
        showFriendsButton.setTextColor(Color.rgb(255,255,255));
        showRanklistButton.setTextColor(Color.rgb(0, 0, 0));
        getAllUsers(new OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(ArrayList<Player> users) {
                FriendsRecyclerViewAdapter friendsAdapter = new FriendsRecyclerViewAdapter(users);
                setClickListener(friendsAdapter, users);
                recyclerView.setAdapter(friendsAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
    }

    /**
     * gets a list of all users get all users
     * @param listener
     */
    private void getAllUsers(final OnUsersRetrievedListener listener) {
        db.collection("Users").whereNotEqualTo("deviceID", this.deviceID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    ArrayList<Player> users = new ArrayList<>();
                    for (DocumentSnapshot document : docs) {
                        Map<String, Object> docData = document.getData();
                        fbw.getUserRank((String) docData.get("deviceID"), new FirebaseWrapper.OnRankRetrievedListener() {
                            @Override
                            public void OnRankRetrieved(int rank) {
                                Player player = new Player(
                                        (String) docData.get("userName"),
                                        (String) docData.get("emailID"),
                                        (String) docData.get("deviceID"),
                                        rank
                                );
                                users.add(player);
                            }
                        }, true);
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
     * gets all data of friends of the current user
     * @param listener
     * @param friendsList
     */
    private void getFriends(final OnUsersRetrievedListener listener, ArrayList<String> friendsList) {

        if(friendsList.size() > 0){
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
        }else{
            ArrayList<Player> users = new ArrayList<>();
            listener.onUsersRetrieved(users);
        }

    }

    /**
     * gets the list of friends from the current user
     * @param listener
     */
    private void getFriendsList(final OnFriendsRetrievedListener listener) {
        db.collection("Users").document(this.userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

    /**
     * sets the click listener for the recycler view.
     * makes it so when clicked on the card you are taken to the users profile page
     * @param friendsAdapter
     * @param players
     */
    public void setClickListener(FriendsRecyclerViewAdapter friendsAdapter, ArrayList<Player> players){
        friendsAdapter.setOnItemClickListener(new FriendsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(players.get(position), fbw))
                        .commit();

            }
        });
    }


    /**
     * listeners: listen for when we are done retrieving data from firebase
     */
    public interface OnFriendsRetrievedListener {
        /**
         * The callback function that the listener should perform.
         * @param friends is a list of the friends that were retrieved.
         */
        void onFriendsRetrieved(ArrayList<String> friends);
    }

    /**
     * Another listener for when all users have been retrieved.
     */
    public interface OnUsersRetrievedListener {
        /**
         * The callback function that the listener should perform.
         * @param users is a list of the users that were retrieved.
         */
        void onUsersRetrieved(ArrayList<Player> users);
    }


}