package com.example.sustaintheglobe.dao;

import com.google.firebase.Timestamp;

public class TaskOuterData {
    private Timestamp created;

    private boolean isPersonalised;
    private String masterTaskID;
    private String postID;
    private String taskID;


    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public boolean isPersonalised() {
        return isPersonalised;
    }

    public void setPersonalised(boolean personalised) {
        isPersonalised = personalised;
    }

    public String getMasterTaskID() {
        return masterTaskID;
    }

    public void setMasterTaskID(String masterTaskID) {
        this.masterTaskID = masterTaskID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }
}
