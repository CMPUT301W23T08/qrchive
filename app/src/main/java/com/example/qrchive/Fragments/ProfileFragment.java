package com.example.qrchive.Fragments;

import android.content.Context;
import android.content.Intent;
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

import com.example.qrchive.Activities.MainActivity;
import com.example.qrchive.Activities.MapsActivity;
import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.OnQRCountQueryListener;
import com.example.qrchive.Classes.Player;
import com.example.qrchive.R;
import com.google.firebase.firestore.FirebaseFirestore;

/** Profile Fragment
 */
public class ProfileFragment extends Fragment {


    private Player user;
    private FirebaseWrapper fbw;
    public ProfileFragment(Player user, FirebaseWrapper fbw) {
        this.user = user;
        this.fbw = fbw;
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
        TextView qrCodeTextView = (TextView)profileView.findViewById(R.id.profile_qr_codes_collected);

        SharedPreferences preferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        String deviceID = preferences.getString("deviceID", "no user id found");

        userNameTextView.setText(user.getUserName());
        emailTextView.setText(user.getEmail());
        userIdTextView.setText(user.getDeviceID());

        if(deviceID.equals(user.getDeviceID())){
            deleteBtn.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "wkaaksdf", Toast.LENGTH_SHORT);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fbw.deleteUser();
                    startActivity(new Intent(getContext(), MainActivity.class));
                }
            });


        }else{
            editFollowFBtn.setText("Follow");
            deleteBtn.setVisibility(View.INVISIBLE);
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