package com.example.qrchive.Fragments;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.ScannedCode;
import com.example.qrchive.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class OnClickCodeFragment extends Fragment {

    ScannedCode scannedCode;
    FirebaseWrapper fbw;
    public OnClickCodeFragment(ScannedCode scannedCode, FirebaseWrapper fbw) {
        this.scannedCode = scannedCode;
        this.fbw = fbw;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OnClickCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static OnClickCodeFragment newInstance(String param1, String param2) {
//        return new OnClickCodeFragment();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_click_code, container, false);

        // Setting the fragment elements
        ConstraintLayout rootLayout = view.findViewById(R.id.fragment_on_item_click_code_constraint_layout_view);
        ((TextView) rootLayout.findViewById(R.id.code_name)).setText(scannedCode.getName());
        ((TextView) rootLayout.findViewById(R.id.code_ascii)).setText(scannedCode.getAscii());


        ((TextView) rootLayout.findViewById(R.id.code_location)).setText(scannedCode.getLocationString());
        ((TextView) rootLayout.findViewById(R.id.code_date)).setText(scannedCode.getDate());
        ((TextView) rootLayout.findViewById(R.id.code_hash_val)).setText(String.valueOf(scannedCode.getHashVal()));


        ((TextView) rootLayout.findViewById(R.id.code_points)).setText(String.valueOf(scannedCode.getPoints()));
        ((TextView) rootLayout.findViewById(R.id.code_rank)).setText("TODO");

        ((ImageView) rootLayout.findViewById(R.id.delete_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbw.deleteCode(scannedCode);
                fbw.refreshScannedCodesForUser(scannedCode.getUserDID());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new CodesFragment(fbw))
                        .commit();

            }
        });

        return view;
    }
}