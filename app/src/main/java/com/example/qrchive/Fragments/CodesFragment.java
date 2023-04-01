package com.example.qrchive.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.Classes.MyScannedCodeCardRecyclerViewAdapter;
import com.example.qrchive.R;

import com.example.qrchive.Classes.ScannedCode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * CodesFragment displays this list of users scanned codes.
 * It is a simple subclass of {@link Fragment}.
 *
 * @author Shelly
 * @version 1.0
 */
public class CodesFragment extends Fragment {

    private List<ScannedCode> scannedCodes;
    private FirebaseWrapper fbw;
    private MyScannedCodeCardRecyclerViewAdapter scannedCodesAdapter;

    /**
     * The constructor for the fragment.
     *
     * @param fbw is an instance of the FirebaseWrapper that allows for queries to the Firebase DB.
     */
    public CodesFragment(FirebaseWrapper fbw) {
        this.fbw = fbw;
    }

//    /**
//     * @return A new instance of fragment CodesFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static CodesFragment newInstance(String param1, String param2) {
//        return new CodesFragment();
//    }

    /**
     * onCreate is called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
}

    /**
     * onCreateView is called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Returns the view that was instantiated.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_codes, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_list);


        scannedCodes = fbw.getScannedCodesDict().get(fbw.getMyUserDID());
        scannedCodesAdapter = new MyScannedCodeCardRecyclerViewAdapter(scannedCodes);
        scannedCodesAdapter.setOnItemClickListener(new MyScannedCodeCardRecyclerViewAdapter.OnItemClickListener() {
            /**
             * OnItemClick is a click listener for the view adapter.
             *
             * @param view is the view that is being clicked on.
             * @param position is the position in the view adapter that has been clicked.
             */
            @Override
            public void OnItemClick(View view, int position) {
                ScannedCode scannedCode = scannedCodes.get(position);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new OnClickCodeFragment(scannedCode, fbw))
                        .addToBackStack(null)
                        .commit();
            }
        });


        scannedCodesAdapter.notifyDataSetChanged(); /* todo, this CAN be the reason why Codes
                                                    /* fragment is empty (race condition between
                                                    / *this line and FirebaseWrapper still
                                                    / *initializing the codes for this user) */
        ((TextView) view.findViewById(R.id.bottom_qrs_card_text)).setText(String.valueOf(scannedCodes.size()));
        ((TextView) view.findViewById(R.id.bottom_pts_card_text)).setText(String.valueOf(
                scannedCodes.stream().mapToInt(ScannedCode::getPoints).sum()
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(scannedCodesAdapter);

        Spinner sortedBySpinner = view.findViewById(R.id.sorted_by_spinner);
        sortedBySpinner.setAdapter(new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Date", "Location", "Points", "Name"}));
        sortedBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * onItemSelected is a callback function that is invoked when an item has been selected.
             *
             * @param parent is the AdapterView.
             * @param view is the view.
             * @param position is the position in the view adapter.
             * @param id is the id of the item that has been selected.
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                switch (selectedItem) {
                    case "Points":
                        scannedCodes.sort(Collections.reverseOrder(Comparator.comparingInt(ScannedCode::getPoints)));
                        break;
                    case "Location":
                        scannedCodes.sort(Comparator.comparing(ScannedCode::getLocation));
                        break;
                    case "Date":
                        scannedCodes.sort(Collections.reverseOrder(Comparator.comparing(ScannedCode::getDateObject)));
                        break;
                    case "Name":
                        scannedCodes.sort(Comparator.comparing(ScannedCode::getName));
                        break;
                }
                scannedCodesAdapter.notifyDataSetChanged();
            }

            /**
             * onNothingSelected is a callback function that occurs when no items are selected.
             *
             * @param parent is the AdapterView.
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }
}