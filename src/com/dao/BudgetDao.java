package com.dao;

import com.model.Budget;
import com.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BudgetDao {

    // VIEW BUDGETS
    public void viewBudgets(int userId) {

        String sql = """
            SELECT
                b.budget_id,
                c.category_name,
                b.limit_amount,
                DATE_FORMAT(b.start_date, '%d-%m-%Y') AS formatted_start_date,
                DATE_FORMAT(b.end_date, '%d-%m-%Y') AS formatted_end_date,
                b.alert_threshold
            FROM budgets b
            LEFT JOIN categories c
                ON b.category_id = c.category_id
            WHERE b.user_id = ?
            ORDER BY b.start_date ASC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n================ BUDGETS ================\n");

            System.out.printf(
                "%-5s %-20s %-15s %-17s %-17s %-12s%n",
                "ID", "CATEGORY", "LIMIT", "START DATE",
                "END DATE", "ALERT (%)"
            );

            System.out.println("--------------------------------------------------------------------------------");

            boolean found = false;

            while (rs.next()) {

                found = true;

                String endDate = rs.getString("formatted_end_date");

                if (endDate == null) {
                    endDate = "Not set";
                }

                System.out.printf(
                    "%-5d %-20s %-15.2f %-17s %-17s %-12d%n",
                    rs.getInt("budget_id"),
                    rs.getString("category_name"),
                    rs.getDouble("limit_amount"),
                    rs.getString("formatted_start_date"),
                    endDate,
                    rs.getInt("alert_threshold")
                );
            }

            if (!found) {
                System.out.println("No budgets found.");
            }

        } catch (SQLException e) {
            System.out.println("Error viewing budgets: " + e.getMessage());
        }
    }

    // ADD BUDGET
    public void addBudget(Budget budget) {

        String sql = """
            INSERT INTO budgets
            (user_id, category_id, limit_amount, start_date,
             end_date, alert_threshold)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, budget.getUserId());
            ps.setInt(2, budget.getCategoryId());
            ps.setDouble(3, budget.getLimitAmount());
            ps.setDate(4, budget.getStartDate());
            ps.setDate(5, budget.getEndDate());
            ps.setInt(6, budget.getAlertThreshold());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Budget added successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Error adding budget: " + e.getMessage());
        }
    }

    // UPDATE LIMIT AMOUNT
    public void updateBudgetAmount(int id, double amount, int userId) {

        updateBudgetField(
            "UPDATE budgets SET limit_amount = ? WHERE budget_id = ? AND user_id = ?",
            amount, id, userId
        );
    }

    // UPDATE START DATE
    public void updateStartDate(int id, Date date, int userId) {

        updateBudgetField(
            "UPDATE budgets SET start_date = ? WHERE budget_id = ? AND user_id = ?",
            date, id, userId
        );
    }

    // UPDATE END DATE
    public void updateEndDate(int id, Date date, int userId) {

        updateBudgetField(
            "UPDATE budgets SET end_date = ? WHERE budget_id = ? AND user_id = ?",
            date, id, userId
        );
    }

    // REMOVE END DATE
    public void removeEndDate(int id, int userId) {

        executeSimpleUpdate(
            "UPDATE budgets SET end_date = NULL WHERE budget_id = ? AND user_id = ?",
            id, userId,
            "End date removed successfully!",
            "Budget not found."
        );
    }

    // UPDATE ALERT THRESHOLD
    public void updateAlertThreshold(int id, int threshold, int userId) {

        updateBudgetField(
            "UPDATE budgets SET alert_threshold = ? WHERE budget_id = ? AND user_id = ?",
            threshold, id, userId
        );
    }

    // DELETE BUDGET
    public void deleteBudget(int id, int userId) {

        executeSimpleUpdate(
            "DELETE FROM budgets WHERE budget_id = ? AND user_id = ?",
            id, userId,
            "Budget deleted successfully!",
            "Budget not found."
        );
    }

    // COMMON UPDATE METHOD
    private void updateBudgetField(
        String sql, Object value, int id, int userId
    ) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setObject(1, value);
            ps.setInt(2, id);
            ps.setInt(3, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Budget updated successfully!");
            } else {
                System.out.println("Budget not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating budget: " + e.getMessage());
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
