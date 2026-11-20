package com.dao;

import com.model.Goal;
import com.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GoalDao {

    // VIEW GOALS
    public void viewGoals(int userId) {

        String sql = """
            SELECT
                goal_id,
                goal_name,
                target_amount,
                saved_amount,
                DATE_FORMAT(deadline, '%d-%m-%Y') AS formatted_deadline,
                description
            FROM goals
            WHERE user_id = ?
            ORDER BY goal_id ASC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n================ GOALS ================\n");

            System.out.printf(
                "%-5s %-20s %-15s %-15s %-15s %-12s%n",
                "ID", "GOAL", "TARGET", "SAVED",
                "DEADLINE", "PROGRESS"
            );

            System.out.println("--------------------------------------------------------------------------------");

            boolean found = false;

            while (rs.next()) {

                found = true;

                double target = rs.getDouble("target_amount");
                double saved = rs.getDouble("saved_amount");

                double progress = 0;

                if (target > 0) {
                    progress = (saved / target) * 100;
                }

                String deadline = rs.getString("formatted_deadline");

                if (deadline == null) {
                    deadline = "Not set";
                }

                System.out.printf(
                    "%-5d %-20s %-15.2f %-15.2f %-15s %-11.2f%%%n",
                    rs.getInt("goal_id"),
                    rs.getString("goal_name"),
                    target,
                    saved,
                    deadline,
                    progress
                );

                String description = rs.getString("description");

                if (description != null && !description.isBlank()) {
                    System.out.println("      Description: " + description);
                }
            }

            if (!found) {
                System.out.println("No goals found.");
            }

        } catch (SQLException e) {
            System.out.println("Error viewing goals: " + e.getMessage());
        }
    }

    // ADD GOAL
    public void addGoal(Goal goal) {

        String sql = """
            INSERT INTO goals
            (user_id, goal_name, target_amount, saved_amount,
             deadline, description)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, goal.getUserId());
            ps.setString(2, goal.getGoalName());
            ps.setDouble(3, goal.getTargetAmount());
            ps.setDouble(4, goal.getSavedAmount());
            ps.setDate(5, goal.getDeadline());
            ps.setString(6, goal.getDescription());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Goal added successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Error adding goal: " + e.getMessage());
        }
    }

    // UPDATE GOAL NAME
    public void updateGoalName(int id, String name, int userId) {

        updateField(
            "UPDATE goals SET goal_name = ? WHERE goal_id = ? AND user_id = ?",
            name, id, userId
        );
    }

    // UPDATE TARGET AMOUNT
    public void updateTargetAmount(int id, double amount, int userId) {

        updateField(
            "UPDATE goals SET target_amount = ? WHERE goal_id = ? AND user_id = ?",
            amount, id, userId
        );
    }

    // UPDATE SAVED AMOUNT
    public void updateSavedAmount(int id, double amount, int userId) {

        updateField(
            "UPDATE goals SET saved_amount = ? WHERE goal_id = ? AND user_id = ?",
            amount, id, userId
        );
    }

    // UPDATE DEADLINE
    public void updateDeadline(int id, Date deadline, int userId) {

        updateField(
            "UPDATE goals SET deadline = ? WHERE goal_id = ? AND user_id = ?",
            deadline, id, userId
        );
    }

    // REMOVE DEADLINE
    public void removeDeadline(int id, int userId) {

        executeSimpleUpdate(
            "UPDATE goals SET deadline = NULL WHERE goal_id = ? AND user_id = ?",
            id, userId,
            "Deadline removed successfully!",
            "Goal not found."
        );
    }

    // UPDATE DESCRIPTION
    public void updateDescription(int id, String description, int userId) {

        updateField(
            "UPDATE goals SET description = ? WHERE goal_id = ? AND user_id = ?",
            description, id, userId
        );
    }

    // DELETE GOAL
    public void deleteGoal(int id, int userId) {

        executeSimpleUpdate(
            "DELETE FROM goals WHERE goal_id = ? AND user_id = ?",
            id, userId,
            "Goal deleted successfully!",
            "Goal not found."
        );
    }

    // COMMON UPDATE METHOD
    private void updateField(
        String sql, Object value, int id, int userId
    ) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setObject(1, value);
            ps.setInt(2, id);
            ps.setInt(3, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Goal updated successfully!");
            } else {
                System.out.println("Goal not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating goal: " + e.getMessage());
        }
    }

    // COMMON DELETE / NULL UPDATE METHOD
    private void executeSimpleUpdate(
        String sql, int id, int userId,
        String successMessage, String failureMessage
    ) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println(successMessage);
            } else {
                System.out.println(failureMessage);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}