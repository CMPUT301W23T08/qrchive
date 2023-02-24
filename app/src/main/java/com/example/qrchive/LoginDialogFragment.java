package com.example.qrchive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginDialogFragment extends DialogFragment {
    FirebaseFirestore db;
    String android_device_id;

    SharedPreferences preferences;
    LoginDialogFragment(FirebaseFirestore db, SharedPreferences preferences, String android_device_id) {
        super();
        this.db = db;
        this.preferences = preferences;
        this.android_device_id = android_device_id;
    }

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
                    db.collection("Users").add(user);

                    // Populating preferences
                    SharedPreferences.Editor prefEdit = preferences.edit();
                    prefEdit.putString("userName", userNameField.getText().toString());
                    prefEdit.putString("emailID", emailField.getText().toString());
                    prefEdit.putString("deviceID", android_device_id);
                })
                .create();
    }
}

//public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_city, null);
//        EditText editCityName = view.findViewById(R.id.edit_text_city_text);
//        EditText editProvinceName = view.findViewById(R.id.edit_text_province_text);
//        return new AlertDialog.Builder(requireContext())
//                .setView(view)
//                .setTitle("Edit City")
//                .setNegativeButton("Cancel", null)
//                .setPositiveButton("Edit", (dialog, which) -> {
//                    String cityName = editCityName.getText().toString();
//                    String provinceName = editProvinceName.getText().toString();
//                    City city = cityArrayAdapter.getItem(itemPostion);
//                    city.setName(cityName);
//                    city.setProvince(provinceName);
//                    cityArrayAdapter.notifyDataSetChanged();
//                })
//                .create();
//    }
