package com.example.qrchive.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
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
    Pair<ArrayList<String>, ArrayList<String>> usernameAndEmailList;

    SharedPreferences preferences;

    /**
     * The constructor for LoginDialogFragment
     *
     * @param db                is an instance of the Firebase DB
     * @param preferences       is an instance of SharedPreferences to allow for user settings.
     * @param android_device_id is the users device ID.
     * @param usernameAndEmailList is the list of usernames and emails of users who are already registered
     */
    public LoginDialogFragment(FirebaseFirestore db, SharedPreferences preferences,
                               String android_device_id, Pair<ArrayList<String>, ArrayList<String>> usernameAndEmailList) {
        super();
        this.db = db;
        this.preferences = preferences;
        this.android_device_id = android_device_id;
        this.usernameAndEmailList = usernameAndEmailList;
    }

    /**
     * onCreateDialog is used to show an AlertDialog that allows for a user to register.
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Returns an instance of Dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_login, null);
        EditText userNameField = view.findViewById(R.id.username_field);
        EditText emailField = view.findViewById(R.id.email_field);
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle("Welcome!")
                .setMessage("This device is not registered with QRchive. Please register your device to use the app.")
                .setPositiveButton("Done", null) // Set to null first
                .create();

        // Override onClickListener of positive button
        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view1 -> {
                // checking if fields are empty
                if (userNameField.getText().toString().trim().length() == 0) {
                    userNameField.setError("Username field cannot be empty");
                    return;
                }
                if (emailField.getText().toString().trim().length() == 0) {
                    emailField.setError("email field cannot be empty");
                    return;
                }
                // checking if fields are already taken
                if (usernameAndEmailList.first.contains(userNameField.getText().toString())) {
                    userNameField.setError("Username is already taken");
                    return;
                }
                if (usernameAndEmailList.second.contains(emailField.getText().toString())) {
                    emailField.setError("email is already taken");
                    return;
                }
                Map<String, Object> user = new HashMap<>();
                user.put("deviceID", android_device_id);
                user.put("friends", new ArrayList<String>());
                user.put("userName", userNameField.getText().toString());
                user.put("emailID", emailField.getText().toString());

                DocumentReference docref = db.collection("Users").document();
                docref.set(user);

                // Populating preferences
                SharedPreferences.Editor prefEdit = preferences.edit();
                prefEdit.putString("userName", userNameField.getText().toString());
                prefEdit.putString("emailID", emailField.getText().toString());
                prefEdit.putString("deviceID", android_device_id);
                prefEdit.putString("userDID", docref.getId());
                prefEdit.apply(); // apply instead of commit

                alertDialog.dismiss();
            });
        });

        return alertDialog;
    }
}
