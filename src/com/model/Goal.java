package com.model;

import java.sql.Date;

public class Goal {

    private int goalId;
    private int userId;
    private String goalName;
    private double targetAmount;
    private double savedAmount;
    private Date deadline;
    private String description;

    public Goal(int goalId, int userId, String goalName,
                double targetAmount, double savedAmount,
                Date deadline, String description) {

        this.goalId = goalId;
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.deadline = deadline;
        this.description = description;
    }

    public int getGoalId() {
        return goalId;
    }

    public int getUserId() {
        return userId;
    }

    public String getGoalName() {
        return goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
