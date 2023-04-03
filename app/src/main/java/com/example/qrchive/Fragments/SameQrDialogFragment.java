package com.example.qrchive.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.FriendsRecyclerViewAdapter;
import com.example.qrchive.Classes.Player;
import com.example.qrchive.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SameQrDialogFragment extends DialogFragment {
    private ArrayList<String> userIdList;
    private FirebaseWrapper fbw;

    public SameQrDialogFragment(ArrayList<String> userIdList, FirebaseWrapper fbw) {
        this.userIdList = userIdList;
        this.fbw = fbw;
    }
    String TAG = "SameQRDialogFragment";

    private RecyclerView mRecyclerView;
    private FriendsRecyclerViewAdapter adapter;
    private ArrayList<Player> playerArrayList = new ArrayList<>();
    // this method create view for your Dialog
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.fragment_dialog_show_player_same_qr, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.same_qr_recycler_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext()));
        adapter = new FriendsRecyclerViewAdapter(playerArrayList);
        mRecyclerView.setAdapter(adapter);
        for (String userDID : userIdList) {
            fbw.db.collection("Users").document(userDID).get()
                    .addOnSuccessListener(docData -> {
                        Player player = new Player(
                                (String) docData.get("userName"),
                                (String) docData.get("emailID"),
                                (String) docData.get("deviceID"),
                                (String) docData.get("userDID")
                        );
                        playerArrayList.add(player);
                        adapter.notifyItemInserted(playerArrayList.size() - 1);
                    });
        }
        adapter.setOnItemClickListener((view, position) -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(playerArrayList.get(position), fbw))
                    .commit();
        });
        return v;
    }
}
