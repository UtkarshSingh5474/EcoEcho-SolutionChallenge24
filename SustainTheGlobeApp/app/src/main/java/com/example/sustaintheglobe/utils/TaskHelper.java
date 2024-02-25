package com.example.sustaintheglobe.utils;

import android.util.Log;

import com.example.sustaintheglobe.dao.TaskInfoData;
import com.example.sustaintheglobe.dao.TaskOuterData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class TaskHelper {

    private final FirebaseFirestore db;

    public TaskHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void getMyTasksList(String uid, OnTaskDataListener listener) {
        FirestoreHelper firestoreHelper = new FirestoreHelper();
        firestoreHelper.getCurrentTasks(uid, new FirestoreHelper.OnDataListener() {
            @Override
            public void onDataRetrieved(Object data) {
                class TaskDataPair {
                    final String taskID;
                    final TaskInfoData taskInfoData;

                    TaskDataPair(String taskID, TaskInfoData taskInfoData) {
                        this.taskID = taskID;
                        this.taskInfoData = taskInfoData;
                    }
                }
                List<String> currentTasks = (List<String>) data;
                List<TaskDataPair> taskInfoDataList = new ArrayList<>();
                Log.d("MyTasks", "onDataRetrieved: " + currentTasks.size());

                for (String taskID : currentTasks) {
                    Log.d("MyTasks", "onDataRetrieved: " + taskID);
                    firestoreHelper.getTaskInfoData(uid, taskID, new FirestoreHelper.OnDataListener() {
                        @Override
                        public void onDataRetrieved(Object data) {
                            TaskInfoData taskInfoData = (TaskInfoData) data;

                            // Here, we create a simple class to hold both the taskID and the TaskInfoData
                            // This ensures they stay together and in order


                            TaskDataPair pair = new TaskDataPair(taskID, taskInfoData);
                            taskInfoDataList.add(pair);

                            if (taskInfoDataList.size() == currentTasks.size()) {
                                String combinedJsonString = null;
                                try {
                                    JSONObject combinedJson = new JSONObject();
                                    combinedJson.put("currentTasks", new JSONArray(currentTasks));

                                    JSONArray taskInfoDataArray = new JSONArray();
                                    Gson gson = new Gson();
                                    for (String taskID : currentTasks) {
                                        for (TaskDataPair ipair : taskInfoDataList) {
                                            if (taskID.equals(ipair.taskID)) {
                                                JSONObject taskJson = new JSONObject(gson.toJson(ipair.taskInfoData));
                                                taskInfoDataArray.put(taskJson);
                                                break; // Break out of the inner loop once a match is found
                                            }
                                        }
                                    }

                                    combinedJson.put("taskInfoDataList", taskInfoDataArray);

                                    combinedJsonString = combinedJson.toString();
                                    // Now combinedJsonString contains both currentTasks and taskInfoDataList as fields in a single JSON object
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                listener.onDataRetrieved(combinedJsonString);
                            }
                            Log.d("MyTasks", "InfoDataList" + taskInfoDataList.size());
                        }

                        @Override
                        public void onDataNotFound() {
                            Log.d("MyTasks", "onDataNotFound: ");
                        }

                        @Override
                        public void onDataError(Exception e) {
                            Log.d("MyTasks", "onDataError: " + e.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataError(Exception e) {

            }
        });
    }


    public void assignTaskMaster(String userID, final OnTaskDataListener listener) {
        Gson gson = new Gson();

        checkTask(userID, new OnTaskDataListener() {
            @Override
            public void onDataRetrieved(Object object) {
                JsonArray jsonArray = gson.fromJson(object.toString(), JsonArray.class);

                List<String> currentTasks = new ArrayList<>();
                List<Boolean> expiryStatus = new ArrayList<>();

                for (JsonElement element : jsonArray) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    currentTasks.add(jsonObject.get("taskID").getAsString());
                    expiryStatus.add(jsonObject.get("expiryStatus").getAsBoolean());
                }

                Log.d("TaskAssign", "CurrentTasks: " + currentTasks);
                Log.d("TaskAssign", "ExpiryBoolean: " + expiryStatus);

                int trueCount = 0;
                for (boolean b : expiryStatus) {
                    if (b) {
                        trueCount++;
                    }
                }

                if (trueCount > 0) {
                    final int[] count = {0};
                    int finalTrueCount = trueCount;

                    for (int i = 0; i < expiryStatus.size(); i++) {
                        if (expiryStatus.get(i)) {
                            int level = i < 2 ? 1 : i < 3 ? 2 : 3;
                            int finalI = i;

                            assignTaskByLevel(userID, level, new OnTaskDataListener() {
                                @Override
                                public void onDataRetrieved(Object object) {
                                    count[0]++;
                                    currentTasks.set(finalI, (String) object);

                                    if (count[0] == finalTrueCount) {
                                        Log.d("TaskAssign", "newTasksArray: " + currentTasks);

                                        db.collection("Users").document(userID)
                                                .update("currentTasks", currentTasks)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        listener.onDataRetrieved(count[0] + " Tasks Assigned");
                                                    } else {
                                                        listener.onDataError(task.getException());
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onDataNotFound(String message) {
                                    listener.onDataNotFound(message);
                                }

                                @Override
                                public void onDataError(Exception e) {
                                    listener.onDataError(e);
                                }
                            });
                        }
                    }
                } else {
                    listener.onDataRetrieved("0 Tasks Assigned");
                }
            }

            @Override
            public void onDataNotFound(String message) {
                listener.onDataNotFound(message);
            }

            @Override
            public void onDataError(Exception e) {
                listener.onDataError(e);
            }
        });
    }


    public void assignTaskByLevel(String userID, int level, final OnTaskDataListener listener) {

        db.collection("Tasks")
                .whereEqualTo("level", level)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (!documents.isEmpty()) {
                            // Randomly select a task
                            DocumentSnapshot selectedTask = documents.get(new Random().nextInt(documents.size()));

                            TaskInfoData taskInfoData = selectedTask.toObject(TaskInfoData.class);

                            TaskOuterData taskOuterData = createUserTask(taskInfoData);

                            // Add the selected task to the user's tasks
                            DocumentReference userTasksRef = db.collection("Users/" + userID + "/allUserTasks")
                                    .document();

                            taskOuterData.setTaskID(userTasksRef.getId());

                            userTasksRef.set(taskOuterData)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            listener.onDataRetrieved(taskOuterData.getTaskID());
                                        } else {
                                            listener.onDataError(task1.getException());
                                        }
                                    });
                        } else {
                            listener.onDataNotFound("No tasks found for level " + level);
                        }
                    } else {
                        listener.onDataError(task.getException());
                    }
                });
    }


    public void replaceTaskID(String userID, int taskIndexToRemove, String newTaskID, OnTaskDataListener listener) {
        DocumentReference userRef = db.collection("Users").document(userID);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the currentTasks array or initialize it with 4 empty strings if null
                        ArrayList<String> currentTasks = (ArrayList<String>) document.get("currentTasks");
                        if (currentTasks == null) {
                            currentTasks = new ArrayList<>();
                            for (int i = 0; i < 4; i++) {
                                currentTasks.add("");
                            }
                        }

                        // Update the value at the specified index
                        int index = taskIndexToRemove;
                        if (index >= 0 && index < currentTasks.size()) {
                            currentTasks.set(index, newTaskID);

                            // Create a map with the updated currentTasks array
                            Map<String, Object> data = new HashMap<>();
                            data.put("currentTasks", currentTasks);

                            // Set the updated data back to the user document
                            userRef.set(data, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Update successful
                                                listener.onDataRetrieved(true);
                                            } else {
                                                // Handle error
                                                Exception e = task.getException();
                                                if (e != null) {
                                                    listener.onDataError(e);
                                                }
                                            }
                                        }
                                    });
                        } else {
                            // Handle invalid index
                            listener.onDataNotFound("Invalid index");
                        }
                    } else {
                        // Handle non-existing user document
                        listener.onDataNotFound("User document does not exist");
                    }
                } else {
                    // Handle failures
                    listener.onDataNotFound(task.getException().getMessage());
                }
            }
        });
    }


    public TaskOuterData createUserTask(TaskInfoData taskInfoData) {
        TaskOuterData taskOuterData = new TaskOuterData();
        taskOuterData.setCreated(Timestamp.now());
        taskOuterData.setPersonalised(false);
        taskOuterData.setMasterTaskID(taskInfoData.getTaskID());
        taskOuterData.setPostID("");
        taskOuterData.setTaskID("");
        return taskOuterData;
    }

    public void checkTask(String userID, final OnTaskDataListener listener) {
        FirestoreHelper fh = new FirestoreHelper();
        fh.getCurrentTasks(userID, new FirestoreHelper.OnDataListener() {
            @Override
            public void onDataRetrieved(Object data) {
                List<String> currentTasks = (List<String>) data;
                List<CompletableFuture<Boolean>> expiryStatusFutures = new ArrayList<>();

                if (currentTasks.isEmpty()) {
                    Gson gson = new Gson();
                    JsonArray tasksArray = new JsonArray();
                    for (int i = 0; i < 4; i++) {
                        JsonObject taskObject = new JsonObject();
                        taskObject.addProperty("taskID", "0");
                        taskObject.addProperty("expiryStatus", true);
                        tasksArray.add(taskObject);
                    }
                    String json = gson.toJson(tasksArray);
                    listener.onDataRetrieved(json);
                    return;
                }

                // Fetch task info data for all tasks concurrently
                for (String taskID : currentTasks) {
                    expiryStatusFutures.add(fetchTaskExpiryStatus(userID, taskID));
                }

                // Combine all futures into a single future
                CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                        expiryStatusFutures.toArray(new CompletableFuture[0]));

                // When all futures are completed, extract the results and call listener
                allFutures.thenAccept(ignored -> {
                    boolean[] expiryStatus = new boolean[expiryStatusFutures.size()];
                    for (int i = 0; i < expiryStatusFutures.size(); i++) {
                        expiryStatus[i] = expiryStatusFutures.get(i).join(); // Get the result of each CompletableFuture<Boolean>
                    }
                    // Create JSON of currentTasks with their expiry status
                    Gson gson = new Gson();
                    JsonArray tasksArray = new JsonArray();
                    for (int i = 0; i < currentTasks.size(); i++) {
                        JsonObject taskObject = new JsonObject();
                        taskObject.addProperty("taskID", currentTasks.get(i));
                        taskObject.addProperty("expiryStatus", expiryStatus[i]);
                        tasksArray.add(taskObject);
                    }
                    String json = gson.toJson(tasksArray);
                    listener.onDataRetrieved(json);

                });

            }

            @Override
            public void onDataNotFound() {
                listener.onDataNotFound("No current tasks found");
            }

            @Override
            public void onDataError(Exception e) {
                listener.onDataError(e);
            }
        });
    }

    private CompletableFuture<Boolean> fetchTaskExpiryStatus(String userID, String taskID) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FirestoreHelper fh = new FirestoreHelper();
                CompletableFuture<TaskOuterData> outerDataTaskFuture = new CompletableFuture<>();
                fh.getTaskOuterData(userID, taskID, new FirestoreHelper.OnDataListener() {
                    @Override
                    public void onDataRetrieved(Object taskData) {
                        TaskOuterData outerData = (TaskOuterData) taskData;
                        outerDataTaskFuture.complete(outerData);
                    }

                    @Override
                    public void onDataNotFound() {
                        // Handle if outer data not found
                        outerDataTaskFuture.completeExceptionally(new Exception("Outer data not found"));
                    }

                    @Override
                    public void onDataError(Exception e) {
                        // Handle error
                        outerDataTaskFuture.completeExceptionally(e);
                    }
                });

                TaskOuterData outerData = outerDataTaskFuture.get(); // Wait for outer data
                CompletableFuture<TaskInfoData> infoDataTaskFuture = new CompletableFuture<>();
                fh.getTaskInfoData(userID, taskID, new FirestoreHelper.OnDataListener() {
                    @Override
                    public void onDataRetrieved(Object data) {
                        TaskInfoData taskInfoData = (TaskInfoData) data;
                        infoDataTaskFuture.complete(taskInfoData);
                    }

                    @Override
                    public void onDataNotFound() {
                        // Handle if info data not found
                        infoDataTaskFuture.completeExceptionally(new Exception("Info data not found"));
                    }

                    @Override
                    public void onDataError(Exception e) {
                        // Handle error
                        infoDataTaskFuture.completeExceptionally(e);
                    }
                });

                TaskInfoData taskInfoData = infoDataTaskFuture.get(); // Wait for info data
                Timestamp taskTimestamp = outerData.getCreated();
                int taskLevel = taskInfoData.getLevel();
                return hasExpired(taskTimestamp, taskLevel);
            } catch (Exception e) {
                // Log error or handle as needed
                return false;
            }
        });
    }


    public static boolean hasExpired(Timestamp firestoreTimestamp, int taskLevel) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar taskCalendar = Calendar.getInstance();
        taskCalendar.setTime(firestoreTimestamp.toDate());

        switch (taskLevel) {
            case 1:
                return isDailyExpired(currentCalendar, taskCalendar);
            case 2:
                return isWeeklyExpired(currentCalendar, taskCalendar);
            case 3:
                return isMonthlyExpired(currentCalendar, taskCalendar);
            default:
                return false; // Invalid task level
        }
    }

    private static boolean isDailyExpired(Calendar currentCalendar, Calendar taskCalendar) {
        return currentCalendar.get(Calendar.DAY_OF_YEAR) != taskCalendar.get(Calendar.DAY_OF_YEAR)
                || currentCalendar.get(Calendar.YEAR) != taskCalendar.get(Calendar.YEAR);
    }

    private static boolean isWeeklyExpired(Calendar currentCalendar, Calendar taskCalendar) {
        int currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int taskWeek = taskCalendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int taskYear = taskCalendar.get(Calendar.YEAR);

        // Adjust current week calculation if current day is Monday or later
        if (currentCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            currentWeek++;
        }

        // Adjust task week calculation if task day is Monday or later
        if (taskCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            taskWeek++;
        }

        // Handle cases where the week calculation spans across different years
        if (currentWeek == 1 && taskWeek > 52) {
            currentYear--;
        }
        if (taskWeek == 1 && currentWeek > 52) {
            taskYear--;
        }

        // Compare adjusted week numbers and years
        return currentWeek != taskWeek || currentYear != taskYear;
    }

    public void addTask(TaskInfoData data, FirestoreHelper.OnDataListener listener) {
        //add data to Tasks collection with document id data.getTaskID
        db.collection("Tasks")
                .document(data.getTaskID())
                .set(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onDataRetrieved(data);
                    } else {
                        listener.onDataError(task.getException());
                    }
                });
    }


    private static boolean isMonthlyExpired(Calendar currentCalendar, Calendar taskCalendar) {
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int taskMonth = taskCalendar.get(Calendar.MONTH);
        return currentMonth != taskMonth || currentCalendar.get(Calendar.YEAR) != taskCalendar.get(Calendar.YEAR);
    }

    public interface OnTaskDataListener {
        void onDataRetrieved(Object object);

        void onDataNotFound(String message);

        void onDataError(Exception e);
    }

}
