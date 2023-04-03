package com.example.qrchive.Classes;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrchive.R;

import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

/**
 * An adapter class for RecyclerView that displays a list of friends with their details
 *
 * @author Zayd
 */

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.PlayerViewHolder> {

    private ArrayList<Player> playerList;

    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * Constructs a new FriendsRecyclerViewAdapter object with the given list of players.
     * @param playerList playerList the list of players to display in the RecyclerView
     */
    public FriendsRecyclerViewAdapter(ArrayList<Player> playerList) {
        Collections.sort(playerList, Comparator.comparing(p -> -p.getNumericalRank()));
        this.playerList = playerList;
    }

    /**
     * This method creates and returns a new instance of the PlayerViewHolder
     * class by inflating the fragment_friends_content layout
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_friends_content, parent, false);
        return new PlayerViewHolder(view);
    }


    /**
     * Binds the data to the views in the PlayerViewHolder. This method is called by the RecyclerView
     * to display the data at the specified position. The PlayerViewHolder object is created if it does not
     * exist.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Player player = playerList.get(position);
        holder.playerName.setText(player.getUserName());

        // Call the getQRCount method with an instance of the OnQRCountListener interface
        player.getQRCount(new OnQRCountQueryListener() {
            @Override
            public void onQRCount(int count) {
                holder.qrCount.setText("QR codes collected: " + Integer.toString(count));
            }

            @Override
            public void onError(String errorMessage) {
                holder.qrCount.setText("QR codes collected: error");
            }
        });
        player.getScore(new Player.OnPlayerScoreRetrieved(){
            @Override
            public void onScoresRetrieved(int score) {
                holder.playersPoints.setText(Integer.toString(score));
            }
        });

        holder.playerRank.setText(player.getRank());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener != null){
                    clickListener.OnItemClick(v, position);
                }
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in the data set
     */
    @Override
    public int getItemCount() {
        return playerList.size();
    }

    /**
     * A view holder for the player information displayed in the FriendsRecyclerViewAdapter.
     */
    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName, qrCount, playersPoints, playerRank;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);


            playerName = itemView.findViewById(R.id.player_name);
            qrCount = itemView.findViewById(R.id.qr_count);
            playersPoints = itemView.findViewById(R.id.players_points);
            playerRank = itemView.findViewById(R.id.player_rank);
        }
    }

    /**
     * Interface definition for a callback to be invoked when an item in the RecyclerView has been clicked.
     */
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }
}

