package com.example.sustaintheglobe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sustaintheglobe.R;
import com.example.sustaintheglobe.activity.UserProfileActivity;
import com.example.sustaintheglobe.dao.PostData;
import com.example.sustaintheglobe.dao.TaskInfoData;
import com.example.sustaintheglobe.utils.FirestoreHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.ViewHolder>{

    private Context context;
    private List<PostData> postDataList;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();



    public PostsListAdapter(Context context, List<PostData> postDataList) {
        this.context = context;
        this.postDataList = postDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        PostData postDetails = postDataList.get(position);
        String userId=postDetails.getUserID();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirestoreHelper fh = new FirestoreHelper();
        fh.getTaskInfoData(userId, postDetails.getTaskID(), new FirestoreHelper.OnDataListener() {
            @Override
            public void onDataRetrieved(Object data) {
                TaskInfoData taskInfoData = TaskInfoData.class.cast(data);

                holder.taskTitle.setText(taskInfoData.getTitle());
            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataError(Exception e) {

            }
        });


        db.collection("Users").document(userId).get().addOnSuccessListener(documentSnapshot1 -> {
            holder.username.setText("@ "+documentSnapshot1.getString("username"));
        });

        holder.postCaption.setText(postDetails.getCaption());
        Timestamp postTimestamp = postDetails.getPostTime();
        Date postDate = postTimestamp.toDate();
        String postDateString = new SimpleDateFormat("EEE, dd/MM/yyyy", Locale.getDefault()).format(postDate);
        holder.postTime.setText(postDateString);

        Glide.with(context).load(postDetails.getImageLink()).into(holder.postImage);
        holder.likeCount.setText(String.valueOf(postDetails.getLikes().size()));
        String[] likes = postDetails.getLikes().toArray(new String[0]);
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assuming mAuth is FirebaseAuth instance

        final boolean[] currentUserLiked = {Arrays.stream(likes).anyMatch(userID -> userID.equals(currentUserID))};
        if(currentUserLiked[0]){
            holder.likeImageView.setImageResource(R.drawable.ic_like_filled);
        }else {
            holder.likeImageView.setImageResource(R.drawable.ic_like_unfilled);
        }

        final int[] likeCount = {postDetails.getLikes().size()};
        holder.userCard.setOnClickListener(view -> {
            if(!currentUserID.equals(postDetails.getUserID())){

                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("userUID",postDetails.getUserID());
                context.startActivity(intent);
            }
        });
        holder.likeImageView.setOnClickListener(view -> {


            if (currentUserLiked[0]) {

                fh.removeLike(postDetails.getPostID(), currentUserID, new FirestoreHelper.OnDataListener() {
                    @Override
                    public void onDataRetrieved(Object data) {
                        Toast.makeText(context,"UnLiked!",Toast.LENGTH_SHORT).show();
                        holder.likeImageView.setImageResource(R.drawable.ic_like_unfilled);
                        currentUserLiked[0] =false;
                        likeCount[0]--;
                        holder.likeCount.setText(likeCount[0] +"");

                    }

                    @Override
                    public void onDataNotFound() {

                    }

                    @Override
                    public void onDataError(Exception e) {

                    }
                });
            } else {
                holder.likeImageView.setImageResource(R.drawable.ic_like_unfilled);
                fh.setLike(postDetails.getPostID(), currentUserID, new FirestoreHelper.OnDataListener() {
                    @Override
                    public void onDataRetrieved(Object data) {
                        Toast.makeText(context,"Liked!",Toast.LENGTH_SHORT).show();
                        holder.likeImageView.setImageResource(R.drawable.ic_like_filled);
                        currentUserLiked[0]=true;
                        likeCount[0]++;
                        holder.likeCount.setText(likeCount[0] +"");

                    }

                    @Override
                    public void onDataNotFound() {

                    }

                    @Override
                    public void onDataError(Exception e) {

                    }
                });

            }
        });

    }



    @Override
    public int getItemCount() {
        return postDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView postImage;
        ImageView likeImage;

        MaterialCardView taskInfoCV;
        TextView likeCount;
        ImageView likeImageView;
        TextView taskTitle;
        TextView postCaption;
        TextView username;
        TextView postTime;
        LinearLayout userCard;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            userCard = itemView.findViewById(R.id.userCard);
            likeCount=itemView.findViewById(R.id.likeCount);
            likeImageView =itemView.findViewById(R.id.likeImage);
            postImage = itemView.findViewById(R.id.postImage);
            likeImage = itemView.findViewById(R.id.likeImage);
            taskInfoCV=itemView.findViewById(R.id.taskInfoCV);
            taskTitle=itemView.findViewById(R.id.taskTitle);
            postCaption=itemView.findViewById(R.id.postCaption);
            username =itemView.findViewById(R.id.username);
            postTime = itemView.findViewById(R.id.postTime);

        }

        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(context, ViewEventActivity.class);
//            intent.putExtra("eventID", EventsFragment.eventsListPosition.get(getAdapterPosition()));
//            context.startActivity(intent);
        }
    }
}
