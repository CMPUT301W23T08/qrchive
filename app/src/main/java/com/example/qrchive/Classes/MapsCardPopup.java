package com.example.qrchive.Classes;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qrchive.Activities.MainActivity;
import com.example.qrchive.Fragments.MapsFragment;
import com.example.qrchive.Fragments.OnClickCodeFragment;
import com.example.qrchive.R;

@SuppressLint("ValidFragment")
public class MapsCardPopup extends DialogFragment {

    private MainActivity mainActivity;
    private TextView cardHeader;
    private TextView cardDistance;
    private ImageView cardImage;
    private Button cardViewButton;
    private ScannedCode code;

    public MapsCardPopup(ScannedCode code, MainActivity mainActivity) {
        this.code = code;
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_card_popup, container, false);
        cardHeader = view.findViewById(R.id.map_card_popup_header);
        cardDistance = view.findViewById(R.id.map_card_popup_distance);
        cardImage = view.findViewById(R.id.map_card_popup_image);
        cardViewButton = view.findViewById(R.id.map_card_popup_view_button);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardHeader.setText(code.getName());
        cardImage.setImageResource(mainActivity.getDrawableResourceIdFromString(code.getMonsterResourceName()));

        double distance = code.getDistance();
        String appendUnit = " Km";
        if (distance < 1.0) {
            distance = distance * 10000; //convert to meters
            appendUnit = " m";
        }
        String formattedDistance = "Distance: " + String.format("%.2f", distance) + appendUnit;
        cardDistance.setText(String.valueOf(formattedDistance));

        cardViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MapsCardPopup", "Button clicked");
                mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OnClickCodeFragment(code, mainActivity.getFirebaseWrapper()))
                        .commit();
                dismiss();
                return;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

}
