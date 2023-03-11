package com.example.qrchive.Classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
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
        holder.codeCardName.setText(scannedCode.getName());
        holder.codeCardLocation.setText(scannedCode.getLocationString());
        holder.codeCardDate.setText(scannedCode.getDate());
        holder.codeCardPts.setText(String.valueOf(scannedCode.getPoints()));
        holder.codeCardAscii.setText(scannedCode.getAscii());
    }

    @Override
    public int getItemCount() {
        return scannedCodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView codeCardAscii;
        public final TextView codeCardName;
        public final TextView codeCardLocation;
        public final TextView codeCardDate;
        public final TextView codeCardPts;

        public ViewHolder(@NonNull FragmentCodesContentBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getBindingAdapterPosition();
                        mListener.OnItemClick(binding.getRoot(), position);
                    }
                }
            });
            codeCardAscii = binding.codeCardAscii;
            codeCardName = binding.codeCardName;
            codeCardLocation = binding.codeCardLocation;
            codeCardDate = binding.codeCardDate;
            codeCardPts = binding.codeCardPts;
        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }
}