package com.example.qrchive.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qrchive.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * LoginDialogFragment is the fragment that displays when a user is signing on for the first time.
 * It is a subclass of {@link DialogFragment}.
 *
 * @author Shelly
 * @version 1.0
 */
public class LoginDialogFragment extends DialogFragment {
    FirebaseFirestore db;
    String android_device_id;

    SharedPreferences preferences;

    /**
     * The constructor for LoginDialogFragment
     *
     * @param db is an instance of the Firebase DB
     * @param preferences is an instance of SharedPreferences to allow for user settings.
     * @param android_device_id is the users device ID.
     */
    public LoginDialogFragment(FirebaseFirestore db, SharedPreferences preferences, String android_device_id) {
        super();
        this.db = db;
        this.preferences = preferences;
        this.android_device_id = android_device_id;
    }

    /**
     * onCreateDialog is used to show an AlertDialog that allows for a user to register.
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Returns an instance of Dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_login, null);
        EditText userNameField = view.findViewById(R.id.username_field);
        EditText emailField = view.findViewById(R.id.email_field);
        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle("Welcome!")
                .setMessage("This device is not registered with QRchive. Please register your device to use the app.")
                .setPositiveButton("Done", (dialog, which) -> {
                    // Making firedb User collection entry
                    Map<String, Object> user = new HashMap<>();
                    user.put("deviceID", android_device_id);
                    user.put("friends", new ArrayList<String>());
                    user.put("userName", userNameField.getText().toString());
                    user.put("emailID",emailField.getText().toString());

                    DocumentReference docref = db.collection("Users").document();
                    docref.set(user);

                    // Populating preferences
                    SharedPreferences.Editor prefEdit = preferences.edit();
                    prefEdit.putString("userName", userNameField.getText().toString());
                    prefEdit.putString("emailID", emailField.getText().toString());
                    prefEdit.putString("deviceID", android_device_id);
                    prefEdit.putString("userDID", docref.getId());
                    prefEdit.apply();
                    OnLoginSuccessListener listener = (OnLoginSuccessListener) getActivity();
                    listener.onLoginSuccess(preferences.getString("userDID", ""),
                            preferences.getString("userName", ""));
                })
                .create();


    }

    public interface OnLoginSuccessListener {
        void onLoginSuccess(String userDID, String userName);
    }


}