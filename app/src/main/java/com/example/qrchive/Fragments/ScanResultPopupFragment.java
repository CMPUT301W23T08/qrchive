package com.example.qrchive.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ScanResultPopupFragment extends DialogFragment {

    private final String scannedCodeDID;
    private List<String> selectedPreferences;

    private FirebaseWrapper fbw;
    private Fragment scanFrag;

    /**
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return popup Dialog Fragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        selectedPreferences = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Preferences");

        builder.setMultiChoiceItems(R.array.popupPrefrences, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                String[] items = getActivity().getResources().getStringArray(R.array.popupPrefrences);

                if(isChecked){
                    selectedPreferences.add(items[which]);
                }else if(selectedPreferences.contains(items[which])){
                    selectedPreferences.remove(items[which]);
                }

            }
        });

        builder.setPositiveButton("submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedPreferences.contains("Allow use of photo")) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new CaptureFragment(fbw, scannedCodeDID))
                            .addToBackStack(null)
                            .commit();
                }
                sendResult();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "you have chosen not to add this qrcode", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }

    public ScanResultPopupFragment(FirebaseWrapper fbw, Fragment scanFrag, String scannedCodeDID) {
        this.fbw = fbw;
        this.scanFrag = scanFrag;
        this.scannedCodeDID = scannedCodeDID;
    }


    private void sendResult(){

        if(scanFrag == null){
            return;
        }

        Intent intent = ScanFragment.newIntent((ArrayList<String>) selectedPreferences);
        scanFrag.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();


    }
}
