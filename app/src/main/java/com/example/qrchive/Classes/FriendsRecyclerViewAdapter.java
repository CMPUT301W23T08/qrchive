package com.example.qrchive.Classes;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrchive.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.PlayerViewHolder> {

    private ArrayList<Player> playerList;

    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public FriendsRecyclerViewAdapter(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_friends_content, parent, false);
        return new PlayerViewHolder(view);
    }

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
        holder.playersPoints.setText(Integer.toString(player.getPoints()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener != null){
                    clickListener.OnItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName, qrCount, playersPoints;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);


            playerName = itemView.findViewById(R.id.player_name);
            qrCount = itemView.findViewById(R.id.qr_count);
            playersPoints = itemView.findViewById(R.id.players_points);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }
}

