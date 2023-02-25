package com.example.qrchive.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qrchive.R;

/**
 * create an instance of this fragment.
 */
public class CodesFragment extends Fragment {

    public CodesFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment CodesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CodesFragment newInstance(String param1, String param2) {
        CodesFragment fragment = new CodesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_codes, container, false);
    }
}