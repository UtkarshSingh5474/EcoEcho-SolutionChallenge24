package com.example.sustaintheglobe.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sustaintheglobe.R;
import com.example.sustaintheglobe.activity.UserProfileActivity;
import com.example.sustaintheglobe.adapter.LeaderboardListAdapter;
import com.example.sustaintheglobe.dao.User;
import com.example.sustaintheglobe.databinding.FragmentLeaderboardBinding;
import com.example.sustaintheglobe.service.CheckNetwork;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;

    private FirebaseAuth mAuth;
    private LeaderboardListAdapter leaderboardListAdapter;
    private static List<User> leaderboardDetailsList = new ArrayList<>();
    public static ArrayList<String> leaderboardListPosition;
    private FirebaseFirestore db;

    private int chipSelected = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);

        if (!CheckNetwork.isInternetAvailable(getContext())) {
            return binding.getRoot();
        }
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        leaderboardListPosition = new ArrayList<>();

        binding.pullToRefresh.setOnRefreshListener(() -> {
            if (chipSelected<3) {
                getLeaderBoardDataByLocation();
            }else if (chipSelected==3) {
                getWorldLeaderboard();
            }else {
                getFollowersLeaderboard();
            }
        });

        getLeaderBoardDataByLocation();

        binding.nearbyChip.setOnClickListener(view -> {
            chipSelected=1;
            getLeaderBoardDataByLocation();
        });

        binding.countryChip.setOnClickListener(view -> {
            chipSelected=2;
            getLeaderBoardDataByLocation();
        });
        binding.worldChip.setOnClickListener(view -> {
            chipSelected=3;
            getWorldLeaderboard();
        });
        binding.followersChip.setOnClickListener(view -> {
            chipSelected=4;
            getFollowersLeaderboard();
        });




        return binding.getRoot();
    }




    private void getLeaderBoardDataByLocation(){
        if (!CheckNetwork.isInternetAvailable(getContext())) {
            return;
        }

        binding.noFollowers.setVisibility(View.GONE);
        binding.listLL.setVisibility(View.VISIBLE);
        binding.topThreeLL.setVisibility(View.VISIBLE);


        binding.animView.playAnimation();
        binding.animView.setVisibility(View.VISIBLE);
        binding.pullToRefresh.setRefreshing(true);

        //get geoHash from user
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    User user = task.getResult().toObject(User.class);



                    if (user != null) {
                        Query query = null;

                        switch (chipSelected){
                            case 1:
                                query = db.collection("Users")
                                        .whereEqualTo("cityName", user.getCityName())
                                        .orderBy("points", Query.Direction.DESCENDING)
                                        .limit(10);
                                break;
                            case 2:
                                query = db.collection("Users")
                                        .whereEqualTo("countryName", user.getCountryName())
                                        .orderBy("points", Query.Direction.DESCENDING)
                                        .limit(10);
                                break;
                        }



                        query.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                leaderboardDetailsList.clear();


                                for (QueryDocumentSnapshot document : task2.getResult()) {
                                    if (document.exists()) {
                                        User pfo = document.toObject(User.class);
                                        leaderboardDetailsList.add(pfo);
                                        leaderboardListPosition.add(document.getId());
                                    }
                                }

                                if (leaderboardDetailsList.size() != 0) {
                                    binding.eventsRecyclerView.setVisibility(View.VISIBLE);
                                    binding.noEventsCard.setVisibility(View.GONE);
                                } else {
                                    binding.eventsRecyclerView.setVisibility(View.GONE);
                                    binding.noEventsCard.setVisibility(View.VISIBLE);
                                }

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                binding.eventsRecyclerView.setLayoutManager(layoutManager);
                                List<User> top3 = leaderboardDetailsList.stream().limit(3).collect(Collectors.toList());
                                List<User> remaining = leaderboardDetailsList.stream().skip(3).collect(Collectors.toList());
                                leaderboardListAdapter = new LeaderboardListAdapter(getContext(), remaining);
                                binding.eventsRecyclerView.setAdapter(leaderboardListAdapter);
                                setTopThree(top3);
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
            } else {
                binding.animView.setVisibility(View.GONE);
                binding.animView.pauseAnimation();
                binding.pullToRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Unknown error (re029)", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getWorldLeaderboard() {
        if (!CheckNetwork.isInternetAvailable(getContext())) {
            return;
        }

        binding.noFollowers.setVisibility(View.GONE);
        binding.listLL.setVisibility(View.VISIBLE);
        binding.topThreeLL.setVisibility(View.VISIBLE);



        binding.animView.playAnimation();
        binding.animView.setVisibility(View.VISIBLE);
        binding.pullToRefresh.setRefreshing(true);

        if (mAuth.getCurrentUser().getUid() != null) {

            Query query = db.collection("Users")
                    .orderBy("points", Query.Direction.DESCENDING)
                    .limit(10);

            query.get().addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    leaderboardDetailsList.clear();


                    for (QueryDocumentSnapshot document : task2.getResult()) {
                        if (document.exists()) {
                            User pfo = document.toObject(User.class);
                            leaderboardDetailsList.add(pfo);
                            leaderboardListPosition.add(document.getId());
                        }
                    }

                    if (leaderboardDetailsList.size() != 0) {
                        binding.eventsRecyclerView.setVisibility(View.VISIBLE);
                        binding.noEventsCard.setVisibility(View.GONE);
                    } else {
                        binding.eventsRecyclerView.setVisibility(View.GONE);
                        binding.noEventsCard.setVisibility(View.VISIBLE);
                    }

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                    binding.eventsRecyclerView.setLayoutManager(layoutManager);
                    List<User> top3 = leaderboardDetailsList.stream().limit(3).collect(Collectors.toList());
                    List<User> remaining = leaderboardDetailsList.stream().skip(3).collect(Collectors.toList());
                    leaderboardListAdapter = new LeaderboardListAdapter(getContext(), remaining);
                    binding.eventsRecyclerView.setAdapter(leaderboardListAdapter);
                    setTopThree(top3);
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
    private void getFollowersLeaderboard() {

        if (!CheckNetwork.isInternetAvailable(getContext())) {
            return;
        }

        leaderboardDetailsList.clear();

        binding.animView.playAnimation();
        binding.animView.setVisibility(View.VISIBLE);
        binding.pullToRefresh.setRefreshing(true);

        //get geoHash from user
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    User user = task.getResult().toObject(User.class);



                    if (user != null) {
                        Query query = null;

                        List<String> followingList = new ArrayList<>();

                        if (!user.getFollowing().isEmpty()) {
                            followingList = user.getFollowing();
                            followingList.add(mAuth.getCurrentUser().getUid());
                            binding.noFollowers.setVisibility(View.GONE);
                            binding.listLL.setVisibility(View.VISIBLE);
                            binding.topThreeLL.setVisibility(View.VISIBLE);
                            query= db.collection("Users")
                                    .whereIn("userID", followingList)
                                    .orderBy("points", Query.Direction.DESCENDING)
                                    .limit(10);

                        }else{
                            binding.topThreeLL.setVisibility(View.GONE);
                            binding.noFollowers.setVisibility(View.VISIBLE);
                            binding.listLL.setVisibility(View.GONE);
                            binding.animView.pauseAnimation();
                            binding.pullToRefresh.setRefreshing(false);
                            return;
                        }


                        query.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                leaderboardDetailsList.clear();


                                for (QueryDocumentSnapshot document : task2.getResult()) {
                                    if (document.exists()) {
                                        User pfo = document.toObject(User.class);
                                        leaderboardDetailsList.add(pfo);
                                        leaderboardListPosition.add(document.getId());
                                    }
                                }

                                if (leaderboardDetailsList.size() != 0) {
                                    binding.eventsRecyclerView.setVisibility(View.VISIBLE);
                                    binding.noEventsCard.setVisibility(View.GONE);
                                } else {
                                    binding.eventsRecyclerView.setVisibility(View.GONE);
                                    binding.noEventsCard.setVisibility(View.VISIBLE);
                                }

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                binding.eventsRecyclerView.setLayoutManager(layoutManager);
                                List<User> top3 = leaderboardDetailsList.stream().limit(3).collect(Collectors.toList());
                                List<User> remaining = leaderboardDetailsList.stream().skip(3).collect(Collectors.toList());
                                leaderboardListAdapter = new LeaderboardListAdapter(getContext(), remaining);
                                binding.eventsRecyclerView.setAdapter(leaderboardListAdapter);
                                setTopThree(top3);
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
            } else {
                binding.animView.setVisibility(View.GONE);
                binding.animView.pauseAnimation();
                binding.pullToRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Unknown error (re029)", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setTopThree(List<User> leaderboardDetailsList){
        String currentUserID = mAuth.getCurrentUser().getUid();


        if(leaderboardDetailsList.size()>0){
            if (currentUserID.equals(leaderboardDetailsList.get(0).getUserID())) {
                binding.firstPlaceTV.setText(leaderboardDetailsList.get(0).getFullName()+" (You)");
                binding.firstPlaceLL.setClickable(false);
                binding.firstPlaceTV.setTextColor(getResources().getColor(R.color.primary));
                binding.firstPlaceLL.setOnClickListener(view -> {



                });
            }
            else {
                binding.firstPlaceTV.setText(leaderboardDetailsList.get(0).getFullName());
                binding.firstPlaceLL.setOnClickListener(view -> {


                    Intent intent = new Intent(getContext(), UserProfileActivity.class);

                    intent.putExtra("userUID",leaderboardDetailsList.get(0).getUserID());
                    getContext().startActivity(intent);

                });
            }
            binding.firstUserPoints.setText(String.valueOf(leaderboardDetailsList.get(0).getPoints()));

        }else{
            binding.firstPlaceTV.setText("---");
            binding.firstUserPoints.setText(""+0);
            binding.firstPlaceLL.setClickable(false);
        }


        if(leaderboardDetailsList.size()>1){
        if (currentUserID.equals(leaderboardDetailsList.get(1).getUserID())) {
            binding.secondPlaceTV.setText(leaderboardDetailsList.get(1).getFullName()+" (You)");
            binding.secondPlaceTV.setTextColor(getResources().getColor(R.color.primary));
            binding.secondPlaceLL.setClickable(false);
            binding.secondPlaceLL.setOnClickListener(view -> {

            });
        }
        else {
            binding.secondPlaceTV.setText(leaderboardDetailsList.get(1).getFullName());
            binding.secondPlaceLL.setOnClickListener(view -> {


                Intent intent = new Intent(getContext(), UserProfileActivity.class);

                intent.putExtra("userUID",leaderboardDetailsList.get(1).getUserID());
                getContext().startActivity(intent);

            });
        }
        binding.secondUserPoints.setText(String.valueOf(leaderboardDetailsList.get(1).getPoints()));
        }else{
            binding.secondPlaceTV.setText("---");
            binding.secondUserPoints.setText(""+0);
            binding.secondPlaceLL.setClickable(false);
        }

        if(leaderboardDetailsList.size()>2){
        if (currentUserID.equals(leaderboardDetailsList.get(2).getUserID())) {
            binding.thirdPlaceTV.setTextColor(getResources().getColor(R.color.primary));
            binding.thirdPlaceTV.setText(leaderboardDetailsList.get(2).getFullName()+" (You)");
            binding.thirdPlaceLL.setClickable(false);
            binding.thirdPlaceLL.setOnClickListener(view -> {

            });
        }
        else {
            binding.thirdPlaceTV.setText(leaderboardDetailsList.get(2).getFullName());
            binding.thirdPlaceLL.setOnClickListener(view -> {

                Intent intent = new Intent(getContext(), UserProfileActivity.class);

                intent.putExtra("userUID",leaderboardDetailsList.get(2).getUserID());
                getContext().startActivity(intent);

            });
        }
        binding.thirdUserPoints.setText(String.valueOf(leaderboardDetailsList.get(2).getPoints()));
        }else{
            binding.thirdPlaceTV.setText("---");
            binding.thirdUserPoints.setText(""+0);
            binding.thirdPlaceLL.setClickable(false);
        }

    }
}