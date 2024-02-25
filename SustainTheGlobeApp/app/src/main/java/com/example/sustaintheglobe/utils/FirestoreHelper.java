package com.example.sustaintheglobe.utils;

import androidx.annotation.NonNull;

import com.example.sustaintheglobe.dao.TaskInfoData;
import com.example.sustaintheglobe.dao.TaskOuterData;
import com.example.sustaintheglobe.dao.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FirestoreHelper {

    private FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void getUserDetails(String userID, final OnDataListener listener) {
        CollectionReference usersRef = db.collection("Users");

        usersRef.document(userID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            listener.onDataRetrieved(user);
                        } else {
                            listener.onDataNotFound();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onDataError(e);
                    }
                });
    }

    public void getTaskOuterData(String userID,String outerTaskID,final OnDataListener listener){
        db.collection("Users/" + userID + "/allUserTasks")
                .document(outerTaskID)
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        TaskOuterData taskOuterData = documentSnapshot1.toObject(TaskOuterData.class);
                        listener.onDataRetrieved(taskOuterData);
                    } else {
                        listener.onDataNotFound();
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onDataError(e);

                });
    }

    public void getTaskInfoData(String userID,String outerTaskID,final OnDataListener listener){
        db.collection("Users/" + userID + "/allUserTasks")
                .document(outerTaskID)
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {

                        if(!documentSnapshot1.getBoolean("personalised")){
                            getCommonTask(documentSnapshot1.getString("masterTaskID"),listener);
                        }else{
                            getPersonalisedTask(userID,documentSnapshot1.getString("masterTaskID"),listener);
                        }
                    } else {
                        listener.onDataNotFound();
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onDataError(e);

                });
    }

    public void getCommonTask(String commonTaskID,final OnDataListener listener){
        db.collection("Tasks")
                .document(commonTaskID)
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {



                        TaskInfoData taskInfoData = documentSnapshot1.toObject(TaskInfoData.class);
                        listener.onDataRetrieved(taskInfoData);
                    } else {
                        listener.onDataNotFound();
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onDataError(e);

                });
    }

    public void getPersonalisedTask(String userID, String personalisedTaskID, final OnDataListener listener){
        db.collection("Users/"+userID+"/personalisedTasks")
                .document(personalisedTaskID)
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {



                        TaskInfoData taskInfoData = documentSnapshot1.toObject(TaskInfoData.class);
                        listener.onDataRetrieved(taskInfoData);
                    } else {
                        listener.onDataNotFound();
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onDataError(e);

                });

    }

    public void setLike(String postID, String userID, final OnDataListener listener) {
        // Get the document reference
        DocumentReference postRef = db.collection("Posts").document(postID);

        // Update the document
        postRef.update("likes", FieldValue.arrayUnion(userID))
                .addOnSuccessListener(aVoid -> {
                    // Notify the listener that the operation was successful
                    listener.onDataRetrieved("Liked Successfully!");
                })
                .addOnFailureListener(e -> {
                    // Notify the listener about the failure
                    listener.onDataError(e);
                });
    }
    public void removeLike(String postID, String userID, final OnDataListener listener) {
        // Get the document reference
        DocumentReference postRef = db.collection("Posts").document(postID);

        // Update the document
        postRef.update("likes", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(aVoid -> {
                    // Notify the listener that the operation was successful
                    listener.onDataRetrieved("Unliked Successfully!");
                })
                .addOnFailureListener(e -> {
                    // Notify the listener about the failure
                    listener.onDataError(e);
                });
    }

    public void addFollower(String addToFollowingUID, String addToFollowerUID, final OnDataListener listener){

        db.collection("Users").document(addToFollowingUID).update("following",FieldValue.arrayUnion(addToFollowerUID))
                .addOnSuccessListener(aVoid -> {
                    db.collection("Users").document(addToFollowerUID).update("followers",FieldValue.arrayUnion(addToFollowingUID))
                            .addOnSuccessListener(aVoid2 ->{
                                listener.onDataRetrieved(true);
                            }).addOnFailureListener(e ->{
                                listener.onDataRetrieved(false);

                            });
                }).addOnFailureListener(e ->{
                    listener.onDataRetrieved(false);
                });
    }
    public void removeFollower(String addToFollowingUID, String addToFollowerUID, final OnDataListener listener){

        db.collection("Users").document(addToFollowingUID).update("following",FieldValue.arrayRemove(addToFollowerUID))
                .addOnSuccessListener(aVoid -> {
                    db.collection("Users").document(addToFollowerUID).update("followers",FieldValue.arrayRemove(addToFollowingUID))
                            .addOnSuccessListener(aVoid2 ->{
                                listener.onDataRetrieved(true);
                            }).addOnFailureListener(e ->{
                                listener.onDataRetrieved(false);

                            });
                }).addOnFailureListener(e ->{
                    listener.onDataRetrieved(false);
                });
    }


    public void getCurrentTasks(String userID,final OnDataListener listener){
        db.collection("Users")
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {

                        List<String> currentTasks= (List<String>) documentSnapshot1.get("currentTasks");
                        listener.onDataRetrieved(currentTasks);
                    } else {
                        listener.onDataNotFound();
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onDataError(e);

                });
    }

    public void getCompletedTasks(String uid, final OnDataListener listener){
        db.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {

                        List<String> completedTasks= (List<String>) documentSnapshot1.get("completedTasks");
                        listener.onDataRetrieved(completedTasks);
                    } else {
                        listener.onDataNotFound();
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onDataError(e);

                });

    }


    public interface OnDataListener {
        void onDataRetrieved(Object data);
        void onDataNotFound();
        void onDataError(Exception e);
    }
}
