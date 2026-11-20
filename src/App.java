import com.dao.TransactionDao;
import com.dao.BudgetDao;
import com.dao.GoalDao;

import com.model.Transaction;
import com.model.Budget;
import com.model.Goal;

import com.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class App {

    private static final Scanner sc = new Scanner(System.in);

    private static final int USER_ID = 1;

    private static final TransactionDao transactionDao = new TransactionDao();
    private static final BudgetDao budgetDao = new BudgetDao();
    private static final GoalDao goalDao = new GoalDao();

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            System.out.println("\n========================================");
            System.out.println("          WELCOME TO MONEVO");
            System.out.println("       Personal Finance Manager");
            System.out.println("========================================");

            System.out.println("\nDatabase connected successfully!");

            mainMenu();

        } catch (Exception e) {

            System.out.println("\nApplication error: " + e.getMessage());
        }
    }

    // ================= MAIN MENU =================

    private static void mainMenu() {

        while (true) {

            System.out.println("\n============== MAIN MENU ==============");
            System.out.println("1. Transactions");
            System.out.println("2. Budgets");
            System.out.println("3. Goals");
            System.out.println("4. Exit");
            System.out.println("========================================");

            int choice = readChoice("Enter your choice: ", 4);

            switch (choice) {

                case 1:
                    transactionMenu();
                    break;

                case 2:
                    budgetMenu();
                    break;

                case 3:
                    goalMenu();
                    break;

                case 4:
                    System.out.println("\nThank you for using Monevo!");
                    System.exit(0);
                    break;
            }
        }
    }

    // ================= TRANSACTION MENU =================

    private static void transactionMenu() {

        while (true) {

            System.out.println("\n========== TRANSACTIONS ==========");
            System.out.println("1. View Transactions");
            System.out.println("2. Add Transaction");
            System.out.println("3. Update Transaction");
            System.out.println("4. Delete Transaction");
            System.out.println("5. Back");
            System.out.println("=================================");

            int choice = readChoice("Enter your choice: ", 5);

            switch (choice) {

                case 1:
                    transactionDao.viewTransactions(USER_ID);
                    break;

                case 2:
                    addTransaction();
                    break;

                case 3:
                    updateTransaction();
                    break;

                case 4:
                    deleteTransaction();
                    break;

                case 5:
                    return;
            }
        }
    }

    // ADD TRANSACTION

    private static void addTransaction() {

        System.out.println("\n========== ADD TRANSACTION ==========");

        int categoryId = readPositiveInt("Enter category ID: ");

        String title = readRequiredText("Enter transaction title: ");

        double amount = readPositiveDouble("Enter amount: ");

        System.out.println("\nSelect Transaction Type:");
        System.out.println("1. Income");
        System.out.println("2. Expense");

        int typeChoice = readChoice("Enter choice: ", 2);

        String type = (typeChoice == 1) ? "income" : "expense";

        Date date = readDate("Enter transaction date");

        System.out.println("\nSelect Payment Method:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. UPI");
        System.out.println("4. Bank Transfer");
        System.out.println("5. Other");

        int paymentChoice = readChoice("Enter choice: ", 5);

        String[] paymentMethods = {
            "Cash", "Card", "UPI", "Bank Transfer", "Other"
        };

        String paymentMethod = paymentMethods[paymentChoice - 1];

        String notes = readLine("Enter notes (optional): ");

        System.out.println("\n========== TRANSACTION DETAILS ==========");
        System.out.println("Category ID    : " + categoryId);
        System.out.println("Title          : " + title);
        System.out.println("Amount         : " + amount);
        System.out.println("Type           : " + type);
        System.out.println("Date           : " + date.toLocalDate().format(DATE_FORMATTER));
        System.out.println("Payment Method : " + paymentMethod);
        System.out.println("Notes          : " + notes);
        System.out.println("=========================================");

        if (confirm("Confirm transaction?")) {

            Transaction transaction = new Transaction(
                0,
                USER_ID,
                categoryId,
                title,
                amount,
                type,
                date,
                paymentMethod,
                notes
            );

            transactionDao.addTransaction(transaction);

        } else {

            System.out.println("Transaction cancelled.");
        }
    }

    // UPDATE TRANSACTION

    private static void updateTransaction() {

        System.out.println("\n========== UPDATE TRANSACTION ==========");

        int id = readPositiveInt("Enter transaction ID: ");

        System.out.println("\nWhat do you want to update?");
        System.out.println("1. Title");
        System.out.println("2. Amount");
        System.out.println("3. Both Title and Amount");
        System.out.println("4. Cancel");

        int choice = readChoice("Enter choice: ", 4);

        String title = null;
        double amount = 0;

        switch (choice) {

            case 1:
                title = readRequiredText("Enter new title: ");
                break;

            case 2:
                amount = readPositiveDouble("Enter new amount: ");
                break;

            case 3:
                title = readRequiredText("Enter new title: ");
                amount = readPositiveDouble("Enter new amount: ");
                break;

            case 4:
                System.out.println("Update cancelled.");
                return;
        }

        if (confirm("Confirm update?")) {

            switch (choice) {

                case 1:
                    transactionDao.updateTitle(id, title, USER_ID);
                    break;

                case 2:
                    transactionDao.updateAmount(id, amount, USER_ID);
                    break;

                case 3:
                    transactionDao.updateTransaction(
                        id, title, amount, USER_ID
                    );
                    break;
            }

        } else {

            System.out.println("Update cancelled.");
        }
    }

    // DELETE TRANSACTION

    private static void deleteTransaction() {

        System.out.println("\n========== DELETE TRANSACTION ==========");

        int id = readPositiveInt("Enter transaction ID: ");

        if (confirm("Are you sure you want to delete this transaction?")) {

            transactionDao.deleteTransaction(id, USER_ID);

        } else {

            System.out.println("Deletion cancelled.");
        }
    }

    // ================= BUDGET MENU =================

    private static void budgetMenu() {

        while (true) {

            System.out.println("\n============== BUDGETS ==============");
            System.out.println("1. View Budgets");
            System.out.println("2. Add Budget");
            System.out.println("3. Update Budget");
            System.out.println("4. Delete Budget");
            System.out.println("5. Back");
            System.out.println("=====================================");

            int choice = readChoice("Enter your choice: ", 5);

            switch (choice) {

                case 1:
                    budgetDao.viewBudgets(USER_ID);
                    break;

                case 2:
                    addBudget();
                    break;

                case 3:
                    updateBudget();
                    break;

                case 4:
                    deleteBudget();
                    break;

                case 5:
                    return;
            }
        }
    }

    // ADD BUDGET

    private static void addBudget() {

        System.out.println("\n========== ADD BUDGET ==========");

        int categoryId = readPositiveInt("Enter category ID: ");

        double limitAmount = readPositiveDouble("Enter budget limit: ");

        Date startDate = readDate("Enter start date");

        Date endDate = readOptionalDate("Enter end date");

        if (endDate != null && endDate.before(startDate)) {

            System.out.println("End date cannot be before start date.");
            return;
        }

        int threshold = readThreshold();

        System.out.println("\n========== BUDGET DETAILS ==========");
        System.out.println("Category ID : " + categoryId);
        System.out.println("Limit       : " + limitAmount);
        System.out.println("Start Date  : " + formatDate(startDate));
        System.out.println("End Date    : " + formatDate(endDate));
        System.out.println("Threshold   : " + threshold + "%");
        System.out.println("====================================");

        if (confirm("Confirm budget?")) {

            Budget budget = new Budget(
                0,
                USER_ID,
                categoryId,
                limitAmount,
                startDate,
                endDate,
                threshold
            );

            budgetDao.addBudget(budget);

        } else {

            System.out.println("Budget creation cancelled.");
        }
    }

    // UPDATE BUDGET

    private static void updateBudget() {

        System.out.println("\n========== UPDATE BUDGET ==========");

        int id = readPositiveInt("Enter budget ID: ");

        System.out.println("\nWhat do you want to update?");
        System.out.println("1. Limit Amount");
        System.out.println("2. Start Date");
        System.out.println("3. End Date");
        System.out.println("4. Alert Threshold");
        System.out.println("5. Cancel");

        int choice = readChoice("Enter choice: ", 5);

        double amount = 0;
        Date date = null;
        int threshold = 0;

        switch (choice) {

            case 1:
                amount = readPositiveDouble("Enter new limit amount: ");
                break;

            case 2:
                date = readDate("Enter new start date");
                break;

            case 3:
                date = readOptionalDate("Enter new end date");
                break;

            case 4:
                threshold = readThreshold();
                break;

            case 5:
                System.out.println("Update cancelled.");
                return;
        }

        if (confirm("Confirm budget update?")) {

            switch (choice) {

                case 1:
                    budgetDao.updateBudgetAmount(id, amount, USER_ID);
                    break;

                case 2:
                    budgetDao.updateStartDate(id, date, USER_ID);
                    break;

                case 3:

                    if (date == null) {
                        budgetDao.removeEndDate(id, USER_ID);
                    } else {
                        budgetDao.updateEndDate(id, date, USER_ID);
                    }

                    break;

                case 4:
                    budgetDao.updateAlertThreshold(id, threshold, USER_ID);
                    break;
            }

        } else {

            System.out.println("Update cancelled.");
        }
    }

    // DELETE BUDGET

    private static void deleteBudget() {

        System.out.println("\n========== DELETE BUDGET ==========");

        int id = readPositiveInt("Enter budget ID: ");

        if (confirm("Are you sure you want to delete this budget?")) {

            budgetDao.deleteBudget(id, USER_ID);

        } else {

            System.out.println("Deletion cancelled.");
        }
    }

    // ================= GOAL MENU =================

    private static void goalMenu() {

        while (true) {

            System.out.println("\n=============== GOALS ===============");
            System.out.println("1. View Goals");
            System.out.println("2. Add Goal");
            System.out.println("3. Update Goal");
            System.out.println("4. Delete Goal");
            System.out.println("5. Back");
            System.out.println("=====================================");

            int choice = readChoice("Enter your choice: ", 5);

            switch (choice) {

                case 1:
                    goalDao.viewGoals(USER_ID);
                    break;

                case 2:
                    addGoal();
                    break;

                case 3:
                    updateGoal();
                    break;

                case 4:
                    deleteGoal();
                    break;

                case 5:
                    return;
            }
        }
    }

    // ADD GOAL

    private static void addGoal() {

        System.out.println("\n========== ADD GOAL ==========");

        String goalName = readRequiredText("Enter goal name: ");

        double targetAmount = readPositiveDouble("Enter target amount: ");

        double savedAmount = readNonNegativeDouble("Enter saved amount: ");

        while (savedAmount > targetAmount) {

            System.out.println("Saved amount cannot exceed target amount.");

            savedAmount = readNonNegativeDouble(
                "Enter saved amount again: "
            );
        }

        Date deadline = readOptionalDate("Enter deadline");

        String description = readLine("Enter description (optional): ");

        System.out.println("\n========== GOAL DETAILS ==========");
        System.out.println("Goal Name    : " + goalName);
        System.out.println("Target       : " + targetAmount);
        System.out.println("Saved        : " + savedAmount);
        System.out.println("Deadline     : " + formatDate(deadline));
        System.out.println("Description  : " + description);
        System.out.println("==================================");

        if (confirm("Confirm goal?")) {

            Goal goal = new Goal(
                0,
                USER_ID,
                goalName,
                targetAmount,
                savedAmount,
                deadline,
                description
            );

            goalDao.addGoal(goal);

        } else {

            System.out.println("Goal creation cancelled.");
        }
    }

    // UPDATE GOAL

    private static void updateGoal() {

        System.out.println("\n========== UPDATE GOAL ==========");

        int id = readPositiveInt("Enter goal ID: ");

        System.out.println("\nWhat do you want to update?");
        System.out.println("1. Goal Name");
        System.out.println("2. Target Amount");
        System.out.println("3. Saved Amount");
        System.out.println("4. Deadline");
        System.out.println("5. Description");
        System.out.println("6. Cancel");

        int choice = readChoice("Enter choice: ", 6);

        String name = null;
        double amount = 0;
        Date deadline = null;
        String description = null;

        switch (choice) {

            case 1:
                name = readRequiredText("Enter new goal name: ");
                break;

            case 2:
                amount = readPositiveDouble("Enter new target amount: ");
                break;

            case 3:
                amount = readNonNegativeDouble("Enter new saved amount: ");
                break;

            case 4:
                deadline = readOptionalDate("Enter new deadline");
                break;

            case 5:
                description = readLine("Enter new description: ");
                break;

            case 6:
                System.out.println("Update cancelled.");
                return;
        }

        if (confirm("Confirm goal update?")) {

            switch (choice) {

                case 1:
                    goalDao.updateGoalName(id, name, USER_ID);
                    break;

                case 2:
                    goalDao.updateTargetAmount(id, amount, USER_ID);
                    break;

                case 3:
                    goalDao.updateSavedAmount(id, amount, USER_ID);
                    break;

                case 4:

                    if (deadline == null) {
                        goalDao.removeDeadline(id, USER_ID);
                    } else {
                        goalDao.updateDeadline(id, deadline, USER_ID);
                    }

                    break;

                case 5:
                    goalDao.updateDescription(id, description, USER_ID);
                    break;
            }

        } else {

            System.out.println("Update cancelled.");
        }
    }

    // DELETE GOAL

    private static void deleteGoal() {

        System.out.println("\n========== DELETE GOAL ==========");

        int id = readPositiveInt("Enter goal ID: ");

        if (confirm("Are you sure you want to delete this goal?")) {

            goalDao.deleteGoal(id, USER_ID);

        } else {

            System.out.println("Deletion cancelled.");
        }
    }

    // ================= INPUT METHODS =================

    // READ STRING
    private static String readLine(String message) {

        System.out.print(message);

        String value = sc.nextLine().trim();

        if (value.equalsIgnoreCase("ESC")) {

            System.out.println("\nProgram terminated.");
            System.exit(0);
        }

        return value;
    }

    // READ REQUIRED TEXT
    private static String readRequiredText(String message) {

        while (true) {

            String value = readLine(message);

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }

    // READ INTEGER
    private static int readInt(String message) {

        while (true) {

            String value = readLine(message);

            try {
                return Integer.parseInt(value);

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // READ POSITIVE INTEGER
    private static int readPositiveInt(String message) {

        while (true) {

            int value = readInt(message);

            if (value > 0) {
                return value;
            }

            System.out.println("Value must be greater than zero.");
        }
    }

    // READ MENU CHOICE
    private static int readChoice(String message, int max) {

        while (true) {

            int choice = readInt(message);

            if (choice >= 1 && choice <= max) {
                return choice;
            }

            System.out.println("Please enter a choice between 1 and " + max + ".");
        }
    }

    // READ POSITIVE DOUBLE
    private static double readPositiveDouble(String message) {

        while (true) {

            String value = readLine(message);

            try {

                double amount = Double.parseDouble(value);

                if (Double.isFinite(amount) && amount > 0) {
                    return amount;
                }

                System.out.println("Amount must be greater than zero.");

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid amount.");
            }
        }
    }

    // READ NON-NEGATIVE DOUBLE
    private static double readNonNegativeDouble(String message) {

        while (true) {

            String value = readLine(message);

            try {

                double amount = Double.parseDouble(value);

                if (Double.isFinite(amount) && amount >= 0) {
                    return amount;
                }

                System.out.println("Amount cannot be negative.");

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid amount.");
            }
        }
    }

    // READ REQUIRED DATE - DD-MM-YYYY
    private static Date readDate(String message) {

        while (true) {

            String value = readLine(message + " (DD-MM-YYYY): ");

            try {

                LocalDate date = LocalDate.parse(
                    value, DATE_FORMATTER
                );

                return Date.valueOf(date);

            } catch (DateTimeParseException e) {

                System.out.println(
                    "Invalid date! Use DD-MM-YYYY format. Example: 20-11-2026"
                );
            }
        }
    }

    // READ OPTIONAL DATE - DD-MM-YYYY
    private static Date readOptionalDate(String message) {

        while (true) {

            String value = readLine(
                message + " (DD-MM-YYYY or press Enter to skip): "
            );

            if (value.isEmpty()) {
                return null;
            }

            try {

                LocalDate date = LocalDate.parse(
                    value, DATE_FORMATTER
                );

                return Date.valueOf(date);

            } catch (DateTimeParseException e) {

                System.out.println(
                    "Invalid date! Use DD-MM-YYYY format. Example: 20-11-2026"
                );
            }
        }
    }

    // FORMAT DATE FOR CONFIRMATION DISPLAY
    private static String formatDate(Date date) {

        if (date == null) {
            return "Not set";
        }

        return date.toLocalDate().format(DATE_FORMATTER);
    }

    // READ ALERT THRESHOLD
    private static int readThreshold() {

        while (true) {

            int threshold = readInt("Enter alert threshold (1-100): ");

            if (threshold >= 1 && threshold <= 100) {
                return threshold;
            }

            System.out.println("Threshold must be between 1 and 100.");
        }
    }

    // CONFIRMATION
    private static boolean confirm(String message) {

        while (true) {

            String choice = readLine(message + " (Y/N): ");

            if (choice.equalsIgnoreCase("Y")) {
                return true;
            }

            if (choice.equalsIgnoreCase("N")) {
                return false;
            }

            System.out.println("Please enter Y or N.");
        }
    }
}
