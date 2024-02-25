package com.example.sustaintheglobe.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sustaintheglobe.R;
import com.example.sustaintheglobe.activity.UserProfileActivity;
import com.example.sustaintheglobe.dao.User;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardListAdapter extends RecyclerView.Adapter<LeaderboardListAdapter.ViewHolder>{

    private Context context;
    private List<User> leaderboardDataList;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();



    public LeaderboardListAdapter(Context context, List<User> leaderboardDataList) {
        this.context = context;
        this.leaderboardDataList = leaderboardDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.leaderboard_user_recylerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        User userDetails = leaderboardDataList.get(position);
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserID.equals(userDetails.getUserID())) {
            holder.userFullName.setText(userDetails.getFullName()+" (You)");
            holder.userCard.setStrokeColor(context.getResources().getColor(R.color.primary));
            holder.userCard.setStrokeWidth(3);

        }else {
            holder.userFullName.setText(userDetails.getFullName());
        }


        holder.userRank.setText("#"+String.valueOf(position+4));

        holder.username.setText("@"+userDetails.getUsername());
        holder.userPoints.setText(String.valueOf(userDetails.getPoints()));


        holder.userCard.setOnClickListener(view -> {
            if(!currentUserID.equals(userDetails.getUserID())){

                Intent intent = new Intent(context, UserProfileActivity.class);

                intent.putExtra("userUID",userDetails.getUserID());
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return leaderboardDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        MaterialCardView userCard;
       TextView userRank,userFullName, username,userPoints;
       CircleImageView userImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            userCard=itemView.findViewById(R.id.userCard);
            userRank = itemView.findViewById(R.id.userRank);
            userFullName = itemView.findViewById(R.id.userFullName);
            username = itemView.findViewById(R.id.username);
            userPoints = itemView.findViewById(R.id.userPoints);
            userImage = itemView.findViewById(R.id.userImage);

        }

        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(context, ViewEventActivity.class);
//            intent.putExtra("eventID", EventsFragment.eventsListPosition.get(getAdapterPosition()));
//            context.startActivity(intent);
        }
    }
}
