package com.example.qrchive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScannedCodeAdapter extends RecyclerView.Adapter<ScannedCodeAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<ScannedCode> scannedCodes;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //        private final ImageView pokemonImage;
        private final TextView name;
        private final TextView location;
        private final TextView pts;

        public ViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.code_card_name);
            location = view.findViewById(R.id.code_card_location);
            pts = view.findViewById(R.id.code_card_pts);
        }
    }


    public ScannedCodeAdapter(Context ctx, ArrayList<ScannedCode> scannedCodes) {
        this.ctx = ctx;
        this.scannedCodes = scannedCodes;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_codes_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScannedCodeAdapter.ViewHolder holder, int position) {
        ScannedCode scannedCode = scannedCodes.get(position);
        holder.name.setText("Sample Name");; //todo
        holder.location.setText(scannedCode.getLocation());
        holder.pts.setText("100"); //todo
    }

    @Override
    public int getItemCount() {
        return 0;
    }


}

