package com.example.sustaintheglobe;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sustaintheglobe.activity.PersonalInfoActivity;
import com.example.sustaintheglobe.activity.SignInActivity;
import com.example.sustaintheglobe.activity.SignUpActivity;
import com.example.sustaintheglobe.dao.TaskInfoData;
import com.example.sustaintheglobe.dao.User;
import com.example.sustaintheglobe.databinding.ActivityMainBinding;
import com.example.sustaintheglobe.databinding.ActivitySignInBinding;
import com.example.sustaintheglobe.fragment.FeedFragment;
import com.example.sustaintheglobe.fragment.LeaderboardFragment;
import com.example.sustaintheglobe.fragment.MyTasksFragment;
import com.example.sustaintheglobe.fragment.ProfileFragment;
import com.example.sustaintheglobe.service.CheckNetwork;
import com.example.sustaintheglobe.utils.FirestoreHelper;
import com.example.sustaintheglobe.utils.TaskHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ActivityMainBinding binding;
    static AnimatedBottomBar bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        if (!CheckNetwork.isInternetAvailable(this)) {
            binding.noInternet.setVisibility(View.VISIBLE);
            return;
        }
        binding.noInternet.setVisibility(View.GONE);


        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, SignUpActivity.class));
        } else {
            if (!CheckNetwork.isInternetAvailable(this)) {
                return;
            }


            SharedPreferences prefs = getSharedPreferences("SustainTheGlobePrefs", Context.MODE_PRIVATE);

            boolean profileComplete = prefs.getBoolean("profileComplete", false);
            if (profileComplete) {

                long lastRunTimestamp = prefs.getLong("last_task_assigned", 0);


                // Get current timestamp
                Calendar now = Calendar.getInstance();


                // Compare the day part of the timestamps
                Calendar lastRunCal = Calendar.getInstance();
                lastRunCal.setTimeInMillis(lastRunTimestamp);


                if (((now.get(Calendar.DAY_OF_YEAR) != lastRunCal.get(Calendar.DAY_OF_YEAR)) || now.get(Calendar.YEAR) != lastRunCal.get(Calendar.YEAR)) || lastRunTimestamp == 0) {
                    // Run your function here
                    TaskHelper taskHelper = new TaskHelper();
                    taskHelper.assignTaskMaster(mAuth.getCurrentUser().getUid(), new TaskHelper.OnTaskDataListener() {
                        @Override
                        public void onDataRetrieved(Object object) {
                            String result = (String) object;
                            Log.d("TaskAssign", "FinalData " + result);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putLong("last_task_assigned", now.getTimeInMillis());

                            taskHelper.getMyTasksList(mAuth.getCurrentUser().getUid(), new TaskHelper.OnTaskDataListener() {
                                @Override
                                public void onDataRetrieved(Object object) {
                                    String combinedJsonString = (String) object;
                                    try {
                                        // Parse the combined JSON string
                                        JSONObject combinedJson = new JSONObject(combinedJsonString);

                                        // Extract currentTasks from the combined JSON
                                        JSONArray currentTasksArray = combinedJson.getJSONArray("currentTasks");
                                        List<String> currentTasks = new ArrayList<>();
                                        for (int i = 0; i < currentTasksArray.length(); i++) {
                                            currentTasks.add(currentTasksArray.getString(i));
                                        }

                                        // Extract taskInfoDataList from the combined JSON
                                        JSONArray taskInfoDataArray = combinedJson.getJSONArray("taskInfoDataList");
                                        List<TaskInfoData> taskInfoDataList = new ArrayList<>();
                                        Gson gson = new Gson();
                                        for (int i = 0; i < taskInfoDataArray.length(); i++) {
                                            JSONObject taskJson = taskInfoDataArray.getJSONObject(i);
                                            TaskInfoData taskInfoData = gson.fromJson(taskJson.toString(), TaskInfoData.class);
                                            taskInfoDataList.add(taskInfoData);
                                        }

                                        // Save the retrieved lists to SharedPreferences
                                        SharedPreferences prefs = getSharedPreferences("SustainTheGlobePrefs", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("current_tasks", gson.toJson(currentTasks));
                                        editor.putString("my_tasks", gson.toJson(taskInfoDataList));
                                        long nowTimestamp = now.getTimeInMillis();
                                        editor.putLong("last_task_assigned", nowTimestamp);

                                        editor.apply();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onDataNotFound(String message) {

                                }

                                @Override
                                public void onDataError(Exception e) {

                                }
                            });


                        }

                        @Override
                        public void onDataNotFound(String message) {

                        }

                        @Override
                        public void onDataError(Exception e) {

                        }
                    });


                }


                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyTasksFragment())
                        .commit();
            } else {
                // Open PersonalInfoActivity
                Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish(); // Optional: finish SignInActivity
            }

        }


        bottomNav = binding.bottomNavigation;
        bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary));
        bottomNav.setBackgroundResource(R.drawable.rounded_bottom_nav);


        bottomNav.selectTabAt(1, true);


        bottomNav.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {


            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment selectedFragment = null;

                switch (i1) {
                    case 0:
                        selectedFragment = new LeaderboardFragment();
                        break;

                    case 1:
                        selectedFragment = new MyTasksFragment();
                        break;

                    case 2:
                        selectedFragment = new FeedFragment();
                        break;
                    case 3:
                        selectedFragment = new ProfileFragment();
                        break;
                    default:
                        break;
                }

                ft.replace(R.id.fragment_container,
                        selectedFragment).commit();

            }


            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }


        });
    }
}