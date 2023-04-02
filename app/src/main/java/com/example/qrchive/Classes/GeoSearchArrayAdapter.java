package com.example.qrchive.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrchive.Activities.MainActivity;
import com.example.qrchive.R;

import java.util.ArrayList;

/** Class: GeoSearchArrayAdapter
 * Used to show a custom list item in the geo search dropdown list. Item consists
 * of the QR code name, date and points.
 * */
public class GeoSearchArrayAdapter extends ArrayAdapter<ScannedCode> {

    private Context mContext;
    private int mResource;
    private MainActivity mainActivity;

    public GeoSearchArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ScannedCode> objects, MainActivity mainActivity) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.mainActivity = mainActivity;
    }

    /** Set the text fields in the view item to the contents of the QR code
     * that has been clicked in the Geo-Search drop dowwn.
     * */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        // Get the current item from the array
        ScannedCode currentObject = getItem(position);

        // Find the TextViews in the layout
        TextView itemDate = convertView.findViewById(R.id.item_geo_search_date);
        TextView itemName = convertView.findViewById(R.id.item_geo_search_name);
        TextView itemPoints = convertView.findViewById(R.id.item_geo_search_points);
        ImageView itemImage = convertView.findViewById(R.id.item_geo_search_image);

        // Set the text for each TextView
        itemDate.setText(String.valueOf(currentObject.getDate()));
        itemName.setText(String.valueOf(currentObject.getName()));
        itemPoints.setText("Points: " + String.valueOf(currentObject.getPoints()));
        itemImage.setImageResource(mainActivity.getDrawableResourceIdFromString(
                currentObject.getMonsterResourceName()
        ));

        return convertView;
    }
}