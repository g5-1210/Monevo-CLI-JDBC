package com.dao;

import com.model.Transaction;
import com.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionDao {

    // VIEW TRANSACTIONS
    public void viewTransactions(int userId) {

        String sql = """
            SELECT
                t.transaction_id,
                t.title,
                t.amount,
                t.type,
                DATE_FORMAT(t.transaction_date, '%d-%m-%Y') AS formatted_date,
                t.payment_method,
                c.category_name
            FROM transactions t
            LEFT JOIN categories c
                ON t.category_id = c.category_id
            WHERE t.user_id = ?
            ORDER BY t.transaction_date ASC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n================ TRANSACTIONS ================\n");

            System.out.printf(
                "%-5s %-22s %-12s %-10s %-15s %-18s %-18s%n",
                "ID", "TITLE", "AMOUNT", "TYPE",
                "DATE", "PAYMENT", "CATEGORY"
            );

            System.out.println("------------------------------------------------------------------------------------------");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.printf(
                    "%-5d %-22s %-12.2f %-10s %-15s %-18s %-18s%n",
                    rs.getInt("transaction_id"),
                    rs.getString("title"),
                    rs.getDouble("amount"),
                    rs.getString("type"),
                    rs.getString("formatted_date"),
                    rs.getString("payment_method"),
                    rs.getString("category_name")
                );
            }

            if (!found) {
                System.out.println("No transactions found.");
            }

        } catch (SQLException e) {
            System.out.println("Error viewing transactions: " + e.getMessage());
        }
    }

    // ADD TRANSACTION
    public void addTransaction(Transaction transaction) {

        String sql = """
            INSERT INTO transactions
            (user_id, category_id, title, amount, type,
             transaction_date, payment_method, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, transaction.getUserId());
            ps.setInt(2, transaction.getCategoryId());
            ps.setString(3, transaction.getTitle());
            ps.setDouble(4, transaction.getAmount());
            ps.setString(5, transaction.getType());
            ps.setDate(6, transaction.getTransactionDate());
            ps.setString(7, transaction.getPaymentMethod());
            ps.setString(8, transaction.getNotes());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Transaction added successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Error adding transaction: " + e.getMessage());
        }
    }

    // UPDATE TITLE
    public void updateTitle(int id, String title, int userId) {

        updateField(
            "UPDATE transactions SET title = ? WHERE transaction_id = ? AND user_id = ?",
            title, id, userId
        );
    }

    // UPDATE AMOUNT
    public void updateAmount(int id, double amount, int userId) {

        updateField(
            "UPDATE transactions SET amount = ? WHERE transaction_id = ? AND user_id = ?",
            amount, id, userId
        );
    }

    // UPDATE TITLE AND AMOUNT
    public void updateTransaction(
        int id, String title, double amount, int userId
    ) {

        String sql = """
            UPDATE transactions
            SET title = ?, amount = ?
            WHERE transaction_id = ? AND user_id = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setDouble(2, amount);
            ps.setInt(3, id);
            ps.setInt(4, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Transaction updated successfully!");
            } else {
                System.out.println("Transaction not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating transaction: " + e.getMessage());
        }
    }

    // DELETE TRANSACTION
    public void deleteTransaction(int id, int userId) {

        String sql = """
            DELETE FROM transactions
            WHERE transaction_id = ? AND user_id = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Transaction deleted successfully!");
            } else {
                System.out.println("Transaction not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting transaction: " + e.getMessage());
        }
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
                System.out.println("Transaction updated successfully!");
            } else {
                System.out.println("Transaction not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating transaction: " + e.getMessage());
        }
    }
}
