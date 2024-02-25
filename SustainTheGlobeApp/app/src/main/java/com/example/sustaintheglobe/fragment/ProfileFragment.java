package com.example.sustaintheglobe.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sustaintheglobe.activity.SignUpActivity;
import com.example.sustaintheglobe.adapter.PostsListAdapter;
import com.example.sustaintheglobe.dao.PostData;
import com.example.sustaintheglobe.dao.TaskInfoData;
import com.example.sustaintheglobe.dao.TaskOuterData;
import com.example.sustaintheglobe.dao.User;
import com.example.sustaintheglobe.databinding.FragmentProfileBinding;
import com.example.sustaintheglobe.service.CheckNetwork;
import com.example.sustaintheglobe.utils.FirestoreHelper;
import com.example.sustaintheglobe.utils.TaskHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private PostsListAdapter postsListAdapter;
    private static List<PostData> postsDetailsList = new ArrayList<>();
    public static ArrayList<String> postsListPosition;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (!CheckNetwork.isInternetAvailable(getContext())){
            Toast.makeText(getContext(), "No Internet Connection.\nRetry After Connecting!", Toast.LENGTH_LONG).show();
        }

        mAuth=FirebaseAuth.getInstance();

        postsListPosition = new ArrayList<>();

        binding.pullToRefresh.setOnRefreshListener(() -> {

            getDataFromFirestore();

        });

        getDataFromFirestore();


        binding.btnSettings.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SharedPreferences prefs = getActivity().getSharedPreferences("SustainTheGlobePrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();

            startActivity(intent);
            getActivity().finish();



//            TaskHelper firestoreHelper = new TaskHelper();
//
//
//            TaskInfoData[] tasks = {
//                    // Level 1 Tasks (Daily/Easy)
//                    new TaskInfoData("Energy Conservation", "Turn off all lights and appliances when leaving a room today.", 1, "11", "Save Electricity Today"),
//                    new TaskInfoData("Waste Reduction", "Use a reusable water bottle instead of disposable plastic bottles today.", 1, "12", "Use Reusable Water Bottle"),
//                    new TaskInfoData("Environmental Preservation", "Pick up and properly dispose of litter you see outside today.", 1, "13", "Clean Up Litter"),
//                    new TaskInfoData("Sustainable Transportation", "Use public transportation or carpool for your daily commute today.", 1, "14", "Use Public Transport Today"),
//                    new TaskInfoData("Water Conservation", "Fix any leaking faucets or pipes in your home today.", 1, "15", "Fix Leaks Today"),
//
//                    // Level 2 Tasks (Weekly/Medium)
//                    new TaskInfoData("Food Sustainability", "Plan and prepare a full day's meals using only locally sourced ingredients this week.", 2, "16", "Local Food Challenge"),
//                    new TaskInfoData("Community Engagement", "Organize a neighborhood cleanup event this weekend.", 2, "17", "Neighborhood Cleanup"),
//                    new TaskInfoData("Sustainable Shopping", "Make a list before going grocery shopping this week to avoid buying unnecessary items.", 2, "18", "Plan Grocery List"),
//                    new TaskInfoData("Education and Awareness", "Host a small workshop or discussion about sustainable living for your friends this week.", 2, "19", "Sustainability Workshop"),
//                    new TaskInfoData("Social Responsibility", "Volunteer at a local shelter or food bank this weekend.", 2, "20", "Volunteer at Shelter"),
//
//                    // Level 3 Tasks (Monthly/Hard)
//                    new TaskInfoData("Energy Conservation", "Install energy-efficient LED light bulbs in your home this month.", 3, "21", "Upgrade to LED Bulbs"),
//                    new TaskInfoData("Water Conservation", "Set up a rainwater harvesting system at home this month.", 3, "22", "Rainwater Harvesting"),
//                    new TaskInfoData("Environmental Preservation", "Participate in a tree planting event in your community this month.", 3, "23", "Tree Planting Event"),
//                    new TaskInfoData("Sustainable Transportation", "Commit to a car-free day once a week for this month.", 3, "24", "Car-Free Day"),
//                    new TaskInfoData("Food Sustainability", "Start composting food waste at home this month.", 3, "25", "Home Composting")
//            };
//
//
//            for (TaskInfoData task : tasks){
//                firestoreHelper.addTask(task, new FirestoreHelper.OnDataListener() {
//                    @Override
//                    public void onDataRetrieved(Object data) {
//
//                    }
//
//                    @Override
//                    public void onDataNotFound() {
//
//                    }
//
//                    @Override
//                    public void onDataError(Exception e) {
//
//                    }
//                });
//            }


        });



        FirestoreHelper fh  = new FirestoreHelper();
        fh.getUserDetails(mAuth.getCurrentUser().getUid(), new FirestoreHelper.OnDataListener() {
            @Override
            public void onDataRetrieved(Object data) {
                User user = User.class.cast(data);

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






        return view;
    }

    private void getDataFromFirestore() {
        if (!CheckNetwork.isInternetAvailable(getContext())) {
            return;
        }

        binding.animView.playAnimation();
        binding.animView.setVisibility(View.VISIBLE);
        binding.pullToRefresh.setRefreshing(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Query query = db.collection("Posts")
                .whereEqualTo("userID", mAuth.getCurrentUser().getUid())
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

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                binding.eventsRecyclerView.setLayoutManager(layoutManager);
                postsListAdapter = new PostsListAdapter(getContext(), postsDetailsList);
                binding.eventsRecyclerView.setAdapter(postsListAdapter);
                binding.animView.setVisibility(View.GONE);
                binding.animView.pauseAnimation();
                binding.pullToRefresh.setRefreshing(false);
            } else {
                binding.noEventsCard.setVisibility(View.VISIBLE);
                binding.animView.setVisibility(View.GONE);
                binding.animView.pauseAnimation();
                binding.pullToRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Unknown error (re030)", Toast.LENGTH_SHORT).show();
            }
        });
    }
}