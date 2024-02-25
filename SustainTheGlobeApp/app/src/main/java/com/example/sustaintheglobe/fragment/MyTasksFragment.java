package com.example.sustaintheglobe.fragment;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.sustaintheglobe.R;
import com.example.sustaintheglobe.activity.CreatePostActivity;
import com.example.sustaintheglobe.dao.TaskInfoData;
import com.example.sustaintheglobe.databinding.FragmentMyTasksBinding;
import com.example.sustaintheglobe.service.HelperPopUpWindow;
import com.example.sustaintheglobe.service.LoadingBar;
import com.example.sustaintheglobe.utils.CategoryDrawableHelper;
import com.example.sustaintheglobe.utils.FirestoreHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MyTasksFragment extends Fragment {

    private FragmentMyTasksBinding binding;
    private List<TaskInfoData> taskInfoDataList;
    private List<String> currentTasks;
    private List<String> completedTasks=new ArrayList<>();
    LoadingBar loadingBar;
    private FirestoreHelper firestoreHelper = new FirestoreHelper();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyTasksBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        loadingBar = new LoadingBar(getContext(), "Preparing Your Tasks");
        loadingBar.show();


        firestoreHelper.getCompletedTasks(FirebaseAuth.getInstance().getUid(),new FirestoreHelper.OnDataListener() {
            @Override
            public void onDataRetrieved(Object data) {
                completedTasks = (List<String>) data;
                fetchTasksData();
            }

            @Override
            public void onDataNotFound() {
                // Handle data not found
            }

            @Override
            public void onDataError(Exception e) {
                // Handle data error
            }
        });




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = getActivity().getSharedPreferences("SustainTheGlobePrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("my_tasks", null);
        if(json!=null){
            firestoreHelper.getCompletedTasks(FirebaseAuth.getInstance().getUid(),new FirestoreHelper.OnDataListener() {
                @Override
                public void onDataRetrieved(Object data) {
                    completedTasks = (List<String>) data;
                    setTasksData();
                }

                @Override
                public void onDataNotFound() {
                    // Handle data not found
                }

                @Override
                public void onDataError(Exception e) {
                    // Handle data error
                }
            });
        }

    }

    private void setTasksData(){


        //Task1


        binding.task1Title.setText(taskInfoDataList.get(0).getTitle());
        binding.task1Points.setText("+ "+getResources().getString(R.string.dailyTaskPoints)+" Points");
        binding.task1CatergoryLogo.setImageResource(CategoryDrawableHelper.getCategoryDrawable(taskInfoDataList.get(0).getCategory()));
        binding.task1CV.setOnClickListener(view -> {
            HelperPopUpWindow helperPopUpWindow = new HelperPopUpWindow();
            helperPopUpWindow.showPopupWindow(view,currentTasks.get(0),taskInfoDataList.get(0).getTitle(), taskInfoDataList.get(0).getDesc(), taskInfoDataList.get(0).getCategory());

        });
        binding.task1Btn.setOnClickListener(view -> createPost(currentTasks.get(0),getPoints(taskInfoDataList.get(0).getLevel())));
        if(completedTasks.contains(currentTasks.get(0))){
            binding.task1BtnIV.setVisibility(View.VISIBLE);
            binding.task1Btn.setEnabled(false);
            binding.task1CV.setStrokeColor(getResources().getColor(R.color.primary));
            binding.task1CV.setStrokeWidth(12);
        }

        //Task2
        binding.task2Title.setText(taskInfoDataList.get(1).getTitle());
        binding.task2Points.setText("+ "+getResources().getString(R.string.dailyTaskPoints)+" Points");
        binding.task2CatergoryLogo.setImageResource(CategoryDrawableHelper.getCategoryDrawable(taskInfoDataList.get(1).getCategory()));
        binding.task2CV.setOnClickListener(view -> {
            HelperPopUpWindow helperPopUpWindow = new HelperPopUpWindow();
            helperPopUpWindow.showPopupWindow(view,currentTasks.get(1),taskInfoDataList.get(1).getTitle(), taskInfoDataList.get(1).getDesc(), taskInfoDataList.get(1).getCategory());
        });
        binding.task2Btn.setOnClickListener(view -> createPost(currentTasks.get(1),getPoints(taskInfoDataList.get(1).getLevel())));
        if(completedTasks.contains(currentTasks.get(1))){
            binding.task2BtnIV.setVisibility(View.VISIBLE);
            binding.task2Btn.setEnabled(false);
            binding.task2CV.setStrokeColor(getResources().getColor(R.color.primary));
            binding.task2CV.setStrokeWidth(12);
        }

        //Task3
        binding.task3Title.setText(taskInfoDataList.get(2).getTitle());
        binding.task3Points.setText("+ "+getResources().getString(R.string.weeklyTaskPoints)+" Points");
        binding.task3CatergoryLogo.setImageResource(CategoryDrawableHelper.getCategoryDrawable(taskInfoDataList.get(2).getCategory()));
        binding.task3CV.setOnClickListener(view -> {
            HelperPopUpWindow helperPopUpWindow = new HelperPopUpWindow();
            helperPopUpWindow.showPopupWindow(view,currentTasks.get(2),taskInfoDataList.get(2).getTitle(), taskInfoDataList.get(2).getDesc(), taskInfoDataList.get(2).getCategory());
        });
        binding.task3Btn.setOnClickListener(view -> createPost(currentTasks.get(2),getPoints(taskInfoDataList.get(2).getLevel())));
        if(completedTasks.contains(currentTasks.get(2))){
            binding.task3BtnIV.setVisibility(View.VISIBLE);
            binding.task3Btn.setEnabled(false);
            binding.task3CV.setStrokeColor(getResources().getColor(R.color.primary));
            binding.task3CV.setStrokeWidth(12);
        }

        //Task4
        binding.task4Title.setText(taskInfoDataList.get(3).getTitle());
        binding.task4Points.setText("+ "+getResources().getString(R.string.monthlyTaskPoints)+" Points");
        binding.task4CatergoryLogo.setImageResource(CategoryDrawableHelper.getCategoryDrawable(taskInfoDataList.get(3).getCategory()));
        binding.task4CV.setOnClickListener(view -> {
            HelperPopUpWindow helperPopUpWindow = new HelperPopUpWindow();
            helperPopUpWindow.showPopupWindow(view,currentTasks.get(3),taskInfoDataList.get(3).getTitle(), taskInfoDataList.get(3).getDesc(), taskInfoDataList.get(3).getCategory());
        });
        binding.task4Btn.setOnClickListener(view -> createPost(currentTasks.get(3),getPoints(taskInfoDataList.get(3).getLevel())));
        if(completedTasks.contains(currentTasks.get(3))){
            binding.task4BtnIV.setVisibility(View.VISIBLE);
            binding.task4Btn.setEnabled(false);
            binding.task4CV.setStrokeColor(getResources().getColor(R.color.primary));
            binding.task4CV.setStrokeWidth(12);

        }

        loadingBar.dismiss();

    }

    private void fetchTasksData() {
        taskInfoDataList = new ArrayList<>(); // Initialize taskInfoDataList here


        SharedPreferences prefs = getActivity().getSharedPreferences("SustainTheGlobePrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("my_tasks", null);
        String curretnTasksJson = prefs.getString("current_tasks", null);


        if (json != null) {
            Gson gson = new Gson();
            TaskInfoData[] taskInfoData = gson.fromJson(json, TaskInfoData[].class);
            for (TaskInfoData task : taskInfoData) {
                taskInfoDataList.add(task);
            }
            currentTasks = new ArrayList<>();
            if (curretnTasksJson != null) {
                currentTasks = gson.fromJson(curretnTasksJson, currentTasks.getClass());
            }
            setTasksData();
        } else {
            // JSON is empty, show loading bar and retry fetching after some delay
            retryFetchingAfterDelay();
        }
    }

    private void retryFetchingAfterDelay() {
        // You can use a Handler to delay the retry, or you can use other mechanisms like RxJava or Kotlin Coroutines
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchTasksData(); // Retry fetching tasks data
            }
        }, 500);
    }


    public void createPost(String taskID,int pointsToIncrement){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/allUserTasks")
                .document(taskID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String postID = task.getResult().getString("postID");
                        if (postID.isEmpty()) {
                            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                            intent.putExtra("taskID", taskID);
                            intent.putExtra("pointsToIncrement", pointsToIncrement);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                    }

            }
                });


    }

    public int getPoints(int level){
        //return 3 if level 1, 10 if level 2, 30 if level 3
        return level<2?3:level<3?10:30;
    }
}