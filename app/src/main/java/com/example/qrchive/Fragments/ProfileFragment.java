package com.example.qrchive.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrchive.R;

/** Profile Fragment
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView userNameTextView = (TextView)profileView.findViewById(R.id.profile_username);
        TextView emailTextView = (TextView)profileView.findViewById(R.id.profile_email_address);
        TextView userIdTextView = (TextView)profileView.findViewById(R.id.profile_user_id);

        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        String userName = preferences.getString("userName", "no user name found");
        String email = preferences.getString("emailID", "no email found");
        String userId = preferences.getString("userDID", "no user id found");

        userNameTextView.setText(userName);
        emailTextView.setText(email);
        userIdTextView.setText(userId);

        //TODO: get count of QR codes collected

        //TODO: get favorite qrcode

        return profileView;
    }
}