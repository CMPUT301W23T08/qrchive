package com.example.qrchive;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrchive.databinding.FragmentCodesContentBinding;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ScannedCode}.
 */
public class MyScannedCodeCardRecyclerViewAdapter extends RecyclerView.Adapter<MyScannedCodeCardRecyclerViewAdapter.ViewHolder> {

    private final List<ScannedCode> scannedCodes;

    public MyScannedCodeCardRecyclerViewAdapter(List<ScannedCode> items) {
        scannedCodes = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentCodesContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ScannedCode scannedCode = scannedCodes.get(position);
        holder.codeCardName.setText("Name"); //TODO
        holder.codeCardLocation.setText(scannedCode.getLocation());
        holder.codeCardDate.setText(scannedCode.getDate());
        holder.codeCardPts.setText("100"); // TODO
    }

    @Override
    public int getItemCount() {
        return scannedCodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView codeCardImage;
        public final TextView codeCardName;
        public final TextView codeCardLocation;
        public final TextView codeCardDate;
        public final TextView codeCardPts;

        public ViewHolder(@NonNull FragmentCodesContentBinding binding) {
            super(binding.getRoot());
            codeCardImage = binding.codeCardImage;
            codeCardName = binding.codeCardName;
            codeCardLocation = binding.codeCardLocation;
            codeCardDate = binding.codeCardDate;
            codeCardPts = binding.codeCardPts;
        }

    }
}