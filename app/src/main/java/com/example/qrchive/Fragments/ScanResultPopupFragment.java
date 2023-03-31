package com.example.qrchive.Fragments;

import android.annotation.SuppressLint;
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
import com.example.qrchive.Classes.ScannedCode;
import com.example.qrchive.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ScanResultPopupFragment extends DialogFragment {

    private List<String> selectedPreferences;

    private FirebaseWrapper fbw;
    ScannedCode scannedCode;
    public ScanResultPopupFragment(ScannedCode scannedCode, FirebaseWrapper fbw) {

        this.fbw = fbw;
        this.scannedCode = scannedCode;
    }
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
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,new DisplayQrFragment(scannedCode,fbw),null).commit();
                //sendResult();
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




    private void sendResult(){

        Fragment scanFrag = getParentFragment();

        if(scanFrag == null){
            return;
        }

        Intent intent = ScanFragment.newIntent((ArrayList<String>) selectedPreferences);
        scanFrag.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();


    }
}
