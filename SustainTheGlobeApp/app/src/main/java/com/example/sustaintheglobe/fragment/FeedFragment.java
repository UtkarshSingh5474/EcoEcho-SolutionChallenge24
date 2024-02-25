package com.example.sustaintheglobe.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sustaintheglobe.adapter.PostsListAdapter;
import com.example.sustaintheglobe.dao.PostData;
import com.example.sustaintheglobe.databinding.FragmentFeedBinding;
import com.example.sustaintheglobe.service.CheckNetwork;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;

    private FirebaseAuth mAuth;
    private PostsListAdapter postsListAdapter;
    private static List<PostData> postsDetailsList = new ArrayList<>();
    public static ArrayList<String> postsListPosition;
    FirebaseFirestore db;

    private int chipSelected = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFeedBinding.inflate(inflater, container, false);

        if (!CheckNetwork.isInternetAvailable(getContext())) {
            return binding.getRoot();
        }
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        postsListPosition = new ArrayList<>();
        binding.pullToRefresh.setOnRefreshListener(() -> {
            if (chipSelected==1) {
                getNearbyPostsData();
            } else if (chipSelected==2) {
                getCountryPostsData();
            }else if (chipSelected==3) {
                getWorldPostsData();
            }
        });

        binding.nearbyChip.setOnClickListener(view -> {
            binding.subtitleTv.setText("Events that are currently Live!");
            chipSelected=1;
            getNearbyPostsData();
        });

        binding.countryChip.setOnClickListener(view -> {
            binding.subtitleTv.setText("Events that are already ended!");
            chipSelected=2;
            getCountryPostsData();
        });
        binding.worldChip.setOnClickListener(view -> {
            binding.subtitleTv.setText("Events that are already ended!");
            chipSelected=3;
            getWorldPostsData();
        });

        getNearbyPostsData();



        return binding.getRoot();
    }

    private void getNearbyPostsData() {
        if (!CheckNetwork.isInternetAvailable(getContext())) {
            return;
        }


        binding.animView.playAnimation();
        binding.animView.setVisibility(View.VISIBLE);
        binding.pullToRefresh.setRefreshing(true);


        db.collection("Users")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        if (task1.getResult().exists()) {
                            GeoPoint currentUserGeopoint = new GeoPoint(0,0);
                            currentUserGeopoint = task1.getResult().getGeoPoint("location");

                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            String location = "";
                            String city = "";

                            try {
                                List<Address> addresses = geocoder.getFromLocation(
                                        currentUserGeopoint.getLatitude(),
                                        currentUserGeopoint.getLongitude(),
                                        1);

                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                     city = address.getLocality();
                                    // Now you have the city and country
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            db.collection("Posts").whereEqualTo("cityName", city).orderBy("postTime", Query.Direction.DESCENDING).limit(50)
                                    .get()
                                    .addOnCompleteListener(task -> {
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
                });
    }
    private void getCountryPostsData() {
        if (!CheckNetwork.isInternetAvailable(getContext())) {
            return;
        }


        binding.animView.playAnimation();
        binding.animView.setVisibility(View.VISIBLE);
        binding.pullToRefresh.setRefreshing(true);


        db.collection("Users")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        if (task1.getResult().exists()) {
                            GeoPoint currentUserGeopoint = new GeoPoint(0,0);
                            currentUserGeopoint = task1.getResult().getGeoPoint("location");

                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            String countryName = "";

                            try {
                                List<Address> addresses = geocoder.getFromLocation(
                                        currentUserGeopoint.getLatitude(),
                                        currentUserGeopoint.getLongitude(),
                                        1);

                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    String country = address.getCountryName();
                                    countryName = country;
                                    // Now you have the city and country
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            db.collection("Posts").whereEqualTo("countryName", countryName).orderBy("postTime", Query.Direction.DESCENDING).limit(50)
                                    .get()
                                    .addOnCompleteListener(task -> {
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
                });
    }

    private void getWorldPostsData() {
        if (!CheckNetwork.isInternetAvailable(getContext())) {
            return;
        }

        binding.animView.playAnimation();
        binding.animView.setVisibility(View.VISIBLE);
        binding.pullToRefresh.setRefreshing(true);



        Query query = db.collection("Posts")
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