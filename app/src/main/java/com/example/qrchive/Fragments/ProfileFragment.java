package com.example.qrchive.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrchive.Classes.Player;
import com.example.qrchive.R;

/** Profile Fragment
 */
public class ProfileFragment extends Fragment {


    public Player user;
    public ProfileFragment(Player user) {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);

        Button editFollowFBtn = profileView.findViewById(R.id.profile_edit_button);
        Button deleteBtn = profileView.findViewById(R.id.delete_account_button);

        TextView userNameTextView = (TextView)profileView.findViewById(R.id.profile_username);
        TextView emailTextView = (TextView)profileView.findViewById(R.id.profile_email_address);
        TextView userIdTextView = (TextView)profileView.findViewById(R.id.profile_user_id);

        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        String userId = preferences.getString("userDID", "no user id found");

        userNameTextView.setText(user.getUserName());
        emailTextView.setText(user.getEmail());
        userIdTextView.setText(user.getDeviceID());

        if(userId == user.getDeviceID()){
            deleteBtn.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "wkaaksdf", Toast.LENGTH_SHORT);

        }else{
            editFollowFBtn.setText("Follow");
            deleteBtn.setVisibility(View.INVISIBLE);
        }


        //TODO: get count of QR codes collected

        //TODO: get favorite qrcode

        return profileView;
    }
}