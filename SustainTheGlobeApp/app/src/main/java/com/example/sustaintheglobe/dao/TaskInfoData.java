package com.example.sustaintheglobe.dao;

public class TaskInfoData {
    private String category;
    private String desc;
    private int level;
    private String taskID;
    private String title;

    public TaskInfoData(){

    }
    public TaskInfoData(String category, String desc, int level, String taskID, String title) {
        this.category = category;
        this.desc = desc;
        this.level = level;
        this.taskID = taskID;
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
