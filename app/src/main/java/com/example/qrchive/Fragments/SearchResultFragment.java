package com.example.qrchive.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * displays list of searched users
 *
 * @author Grayden
 */
public class SearchResultFragment extends Fragment {
    public FirebaseWrapper fbw;

    private String query;
    private String userId;

    public SearchResultFragment(String query, FirebaseWrapper fbw) {
        this.query = query;
        this.fbw = fbw;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fbw.db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View searchView = inflater.inflate(R.layout.fragment_search, container, false);

        RecyclerView recyclerView = searchView.findViewById(R.id.search_recycler_list);

        displayAllUsers(recyclerView);

        return searchView;
    }

    /**
     * displays the list of all users to the recycler view
     * @param recyclerView
     */
    private void displayAllUsers(RecyclerView recyclerView){
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
        fbw.db.collection("Users").orderBy("userName")
                .startAt(query).endAt(query+"\uf8ff").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        void onFriendsRetrieved(ArrayList<String> friends);
    }

    public interface OnUsersRetrievedListener {
        void onUsersRetrieved(ArrayList<Player> users);
    }


}