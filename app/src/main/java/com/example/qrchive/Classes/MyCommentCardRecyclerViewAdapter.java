package com.example.qrchive.Classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrchive.databinding.FragmentCommentsBinding;

import java.util.List;

public class MyCommentCardRecyclerViewAdapter extends RecyclerView.Adapter<MyCommentCardRecyclerViewAdapter.ViewHolder> {
    private final List<Comment> comments;

    /**
     * MyCommentCardRecyclerViewAdapter is the constructor for the class.
     *
     * @param items is the list of comments we want to store in the view adapter.
     */
    public MyCommentCardRecyclerViewAdapter(List<Comment> items) {
        comments = items;
    }

    /**
     * onCreateViewHolder is called when RecyclerView needs a new RecyclerView.ViewHolder
     * of type Comment to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentCommentsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        Comment comment = comments.get(position);
        String header = comment.getUserName() + " @ " + comment.getDateString();
        holder.commentHeader.setText(header);
        holder.commentContent.setText(comment.getContent());
    }
    /**
     * Getter function for number of comments.
     *
     * @return Returns the number of comments.
     */
    @Override
    public int getItemCount() {
        return comments.size();
    }


    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     *
     * @author Shelly
     * @version 1.0
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView commentHeader;
        public final TextView commentContent;

        /**
         * The constructor for the ViewHolder class.
         *
         * @param binding is the fragment binding that stores the information we want to view.
         */
        public ViewHolder(@NonNull FragmentCommentsBinding binding) {
            super(binding.getRoot());
            commentHeader = binding.commentHeader;
            commentContent = binding.commentContent;
        }

        /**
         * onClick allows the program to catch when a view is clicked on.
         *
         * @param v the view that is being clicked on.
         */
        @Override
        public void onClick(View v) {

        }
    }
}
