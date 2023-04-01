package com.example.qrchive.Classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrchive.databinding.FragmentCodesContentBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ScannedCode}.
 *
 * @author Shelly
 * @version 1.0
 */
public class MyScannedCodeCardRecyclerViewAdapter extends RecyclerView.Adapter<MyScannedCodeCardRecyclerViewAdapter.ViewHolder> {
    private final List<ScannedCode> scannedCodes;
    private OnItemClickListener mListener;

    /**
     * setOnItemClickListener sets up a click listener for the view adapter.
     *
     * @param listener is the OnItemClickListener we want to attach.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * MyScannedCodeCardRecyclerViewAdapter is the constructor for the class.
     *
     * @param items is the list of scanned codes we want to store in the view adapter.
     */
    public MyScannedCodeCardRecyclerViewAdapter(List<ScannedCode> items) {
        scannedCodes = items;
    }

    /**
     * onCreateViewHolder is called when RecyclerView needs a new RecyclerView.ViewHolder
     * of type ScannedCode to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentCodesContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    /**
     * onBindViewHolder is called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ScannedCode scannedCode = scannedCodes.get(position);
        holder.codeCardName.setText(scannedCode.getName());
        holder.codeCardLocation.setText(scannedCode.getLocationString());
        holder.codeCardPts.setText(String.valueOf(scannedCode.getPoints()));
        holder.codeCardAscii.setText(scannedCode.getAscii());
        {
            String complicatedDateString = scannedCode.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date date = null;
            boolean success = true;
            try {
                date = dateFormat.parse(complicatedDateString);
            } catch (ParseException e) {
                success = false;
                holder.codeCardDate.setText(complicatedDateString);
            }

            if (success) {
                SimpleDateFormat betterFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
                holder.codeCardDate.setText(betterFormat.format(date));
            }
        }
    }

    /**
     * Getter function for number of scanned codes.
     *
     * @return Returns the number of scanned codes.
     */
    @Override
    public int getItemCount() {
        return scannedCodes.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     *
     * @author Shelly
     * @version 1.0
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView codeCardAscii;
        public final TextView codeCardName;
        public final TextView codeCardLocation;
        public final TextView codeCardDate;
        public final TextView codeCardPts;

        /**
         * The constructor for the ViewHolder class.
         *
         * @param binding is the fragment binding that stores the information we want to view.
         */
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

        /**
         * onClick allows the program to catch when a view is clicked on.
         * @param v the view that is being clicked on.
         */
        @Override
        public void onClick(View v) {
        }
    }

    /**
     * OnItemClickListener is a listener for the custom view adapter.
     *
     * @author Shelly
     * @version 1.0
     */
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }
}