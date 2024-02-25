package com.example.sustaintheglobe.activity;

import static java.lang.System.in;
import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sustaintheglobe.R;
import com.example.sustaintheglobe.adapter.PostsListAdapter;
import com.example.sustaintheglobe.dao.PostData;
import com.example.sustaintheglobe.dao.User;
import com.example.sustaintheglobe.databinding.ActivityUserProfileBinding;
import com.example.sustaintheglobe.service.CheckNetwork;
import com.example.sustaintheglobe.utils.FirestoreHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private FirebaseAuth mAuth;
    private PostsListAdapter postsListAdapter;
    private static List<PostData> postsDetailsList = new ArrayList<>();
    public static ArrayList<String> postsListPosition;
    private String userUID=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        if (!CheckNetwork.isInternetAvailable(this)){
            Toast.makeText(this, "No Internet Connection.\nRetry After Connecting!", Toast.LENGTH_LONG).show();
        }


        userUID = getIntent().getStringExtra("userUID");
        mAuth= FirebaseAuth.getInstance();

        postsListPosition = new ArrayList<>();

        binding.pullToRefresh.setOnRefreshListener(() -> {

            getDataFromFirestore();

        });

        getDataFromFirestore();

        FirebaseFirestore db = FirebaseFirestore.getInstance();






        FirestoreHelper fh  = new FirestoreHelper();
        fh.getUserDetails(userUID, new FirestoreHelper.OnDataListener() {
            @Override
            public void onDataRetrieved(Object data) {
                User user = User.class.cast(data);

                if(user.getFollowers().contains(mAuth.getCurrentUser().getUid())){
                    binding.followTV.setText("Following");
                    binding.followIV.setImageResource(R.drawable.ic_alreadyfollow_user);
                }else {
                    binding.followTV.setText("Follow");
                    binding.followIV.setImageResource(R.drawable.ic_follow_user);
                }

                binding.btnFollow.setOnClickListener(view -> {
                    if(binding.followTV.getText().equals("Following")){
                        fh.removeFollower(mAuth.getUid(), userUID, new FirestoreHelper.OnDataListener() {
                            @Override
                            public void onDataRetrieved(Object data) {
                                Toast.makeText(getApplicationContext(), "UnFollowed!", Toast.LENGTH_LONG).show();
                                binding.followTV.setText("Follow");
                                binding.followIV.setImageResource(R.drawable.ic_follow_user);
                            }

                            @Override
                            public void onDataNotFound() {

                            }

                            @Override
                            public void onDataError(Exception e) {

                            }
                        });
                    }else {
                        fh.addFollower(mAuth.getUid(), userUID, new FirestoreHelper.OnDataListener() {
                            @Override
                            public void onDataRetrieved(Object data) {
                                Toast.makeText(getApplicationContext(), "Following!", Toast.LENGTH_LONG).show();
                                binding.followTV.setText("Following");
                                binding.followIV.setImageResource(R.drawable.ic_alreadyfollow_user);
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

                binding.fullName.setText(user.getFullName());
                binding.username.setText('@'+user.getUsername());
                binding.bio.setText(user.getBio());


                List<String> completedTasks = (List<String>) user.getCompletedTasks();
                List<String> followers = (List<String>) user.getFollowers();

                binding.points.setText(String.valueOf(user.getPoints()));
                binding.completedTasks.setText(String.valueOf(completedTasks != null ? completedTasks.size() : 0));
                binding.followers.setText(String.valueOf(followers != null ? followers.size() : 0));

                Gson gson = new Gson();
                String json = gson.toJson(user);

                Log.d("UserData", "onUserDetailsRetrieved: "+json);
            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataError(Exception e) {

            }
        });

        binding.toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });
    }
    private void getDataFromFirestore() {
        if (!CheckNetwork.isInternetAvailable(this)) {
            return;
        }

        binding.animView.playAnimation();
        binding.animView.setVisibility(View.VISIBLE);
        binding.pullToRefresh.setRefreshing(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Query query = db.collection("Posts")
                .whereEqualTo("userID", userUID)
                .orderBy("postTime", Query.Direction.DESCENDING)
                .limit(50);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                postsDetailsList.clear();


                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.exists()) {
                        PostData pfo = document.toObject(PostData.class);
                        postsDetailsList.add(pfo);
                        postsListPosition.add(document.getId());
                    }
                }

                if (postsDetailsList.size() != 0) {
                    binding.eventsRecyclerView.setVisibility(View.VISIBLE);
                    binding.noEventsCard.setVisibility(View.GONE);
                } else {
                    binding.eventsRecyclerView.setVisibility(View.GONE);
                    binding.noEventsCard.setVisibility(View.VISIBLE);
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                binding.eventsRecyclerView.setLayoutManager(layoutManager);
                postsListAdapter = new PostsListAdapter(this, postsDetailsList);
                binding.eventsRecyclerView.setAdapter(postsListAdapter);
                binding.animView.setVisibility(View.GONE);
                binding.animView.pauseAnimation();
                binding.pullToRefresh.setRefreshing(false);
            } else {
                binding.noEventsCard.setVisibility(View.VISIBLE);
                binding.animView.setVisibility(View.GONE);
                binding.animView.pauseAnimation();
                binding.pullToRefresh.setRefreshing(false);
                Toast.makeText(this, "Unknown error (re030)", Toast.LENGTH_SHORT).show();
            }
        });
    }
}