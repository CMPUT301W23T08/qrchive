package com.example.qrchive.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qrchive.Classes.Player;
import com.example.qrchive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditDialogFragment extends DialogFragment {

    private Player user;

    public interface OnDismissListener {
        void onDismiss();
    }

    private OnDismissListener mListener;

    public void setOnDismissListener(OnDismissListener listener) {
        mListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onDismiss();
        }
    }

    public EditDialogFragment(Player user){
        this.user = user;
    }

    /**
     * This class is used to create a dialog for editing user details.
     *
     * The user details are fetched from a Firestore database and displayed in the dialog.
     *
     * The user can edit their username and email in the dialog, and the changes are saved to the database.
     *
     * Once the changes are saved, the user's details are updated in the shared preferences.
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FirebaseFirestore db =  FirebaseFirestore.getInstance();

        SharedPreferences preferences =   getActivity().getSharedPreferences("preferences", MODE_PRIVATE);



        Pair<ArrayList<String>, ArrayList<String>> usernameAndEmailList = new Pair<>(new ArrayList<>(), new ArrayList<>());


        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> resultantDocuments = task.getResult().getDocuments();
                for (DocumentSnapshot doc : resultantDocuments) {
                    usernameAndEmailList.first.add(doc.get("userName").toString());
                    usernameAndEmailList.second.add(doc.get("emailID").toString());
                }
            }
        });

        View view = getLayoutInflater().inflate(R.layout.dialog_login, null);
        EditText userNameField = view.findViewById(R.id.username_field);
        EditText emailField = view.findViewById(R.id.email_field);
        userNameField.setText(user.getUserName());
        emailField.setText(user.getEmail());
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle("Edit!")
                .setPositiveButton("Done", null) // Set to null first
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Exit the dialog fragment
                        dismiss();
                    }
                })
                .create();

        // Override onClickListener of positive button
        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view1 -> {
                // checking if fields are empty
                if (userNameField.getText().toString().trim().length() == 0) {
                    userNameField.setError("Username field cannot be empty");
                    return;
                }
                if (userNameField.getText().toString().length() >= 20) {
                    userNameField.setError("Username too big");
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
                Map<String, Object> updates = new HashMap<>();
                updates.put("userName", userNameField.getText().toString());
                updates.put("emailID", emailField.getText().toString());


                DocumentReference docref = db.collection("Users").document(preferences.getString("userDID", ""));

                docref.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // The fields have been successfully updated
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors that occurred while updating the data
                            }
                        });

                user.setPlayerName(userNameField.getText().toString());
                user.setEmail(emailField.getText().toString());
                // Populating preferences
                SharedPreferences.Editor prefEdit = preferences.edit();
                prefEdit.putString("userName", userNameField.getText().toString());
                prefEdit.putString("emailID", emailField.getText().toString());
                prefEdit.apply(); // apply instead of commit


                alertDialog.dismiss();
            });
        });
        return alertDialog;
    }

}

