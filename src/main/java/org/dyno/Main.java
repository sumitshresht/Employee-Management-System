package org.dyno;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/project";
    private static final String DB_USERNAME = "dyno";
    private static final String DB_PASSWORD = "admin123";
    private static final Scanner scanner = new Scanner(System.in); // Single Scanner instance

    public static void main(String[] args) {
        int choice;
        while (true) {
            choice = menu();
            switch (choice) {
                case 1:
                    insert();
                    break;
                case 2:
                    update();
                    break;
                case 3:
                    delete();
                    break;
                case 4:
                    display();
                    break;
                case 5:
                    System.out.println("Are you sure you want to exit? (yes/no)");
                    String exitConfirmation = scanner.nextLine().trim().toLowerCase();
                    if (exitConfirmation.equals("yes")) {
                        System.out.println("Exiting the program. Goodbye!");
                        return;
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static int menu() {
        System.out.println("\nPlease enter your choice:");
        System.out.println("1. Insert a new employee");
        System.out.println("2. Update an existing employee");
        System.out.println("3. Remove an employee");
        System.out.println("4. Print details of employees");
        System.out.println("5. Exit");
        System.out.print("Choice: ");

        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a numeric choice.");
            scanner.next(); // Clear invalid input
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the leftover newline
        return choice;
    }

    private static void insert() {
        String query = "INSERT INTO Employee (id, emp_name, job_role, salary) VALUES (?, ?, ?, ?)";
        try (Connection conn = connection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            Employee em = getInput();
            pst.setInt(1, em.getId());
            pst.setString(2, em.getEmp_name());
            pst.setString(3, em.getJob_role());
            pst.setDouble(4, em.getSalary());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee inserted successfully.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Error: Duplicate ID or constraint violation - " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error during insertion: " + e.getMessage());
        }
    }

    private static void update() {
        System.out.print("Enter Employee ID to update: ");
        int id;

        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid numeric ID.");
            scanner.next(); // Clear invalid input
        }
        id = scanner.nextInt();
        scanner.nextLine(); // Consume the leftover newline

        System.out.println("What would you like to update?");
        System.out.println("1. Name");
        System.out.println("2. Job Role");
        System.out.println("3. Salary");
        System.out.print("Choice: ");

        int choice;
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid numeric choice.");
            scanner.next(); // Clear invalid input
        }
        choice = scanner.nextInt();
        scanner.nextLine(); // Consume the leftover newline

        String column = null;
        String newValue = null;

        switch (choice) {
            case 1:
                System.out.print("Enter new Name: ");
                column = "emp_name";
                newValue = scanner.nextLine();
                break;
            case 2:
                System.out.print("Enter new Job Role: ");
                column = "job_role";
                newValue = scanner.nextLine();
                break;
            case 3:
                System.out.print("Enter new Salary: ");
                while (!scanner.hasNextDouble()) {
                    System.out.println("Invalid input. Please enter a valid numeric salary.");
                    scanner.next(); // Clear invalid input
                }
                column = "salary";
                newValue = String.valueOf(scanner.nextDouble());
                scanner.nextLine(); // Consume the leftover newline
                break;
            default:
                System.out.println("Invalid choice. Returning to the main menu.");
                return;
        }

        String query = "UPDATE Employee SET " + column + " = ? WHERE id = ?";
        try (Connection conn = connection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, newValue);
            pst.setInt(2, id);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee updated successfully.");
            } else {
                System.out.println("No employee found with the given ID.");
            }

        } catch (SQLException e) {
            System.err.println("Error during update: " + e.getMessage());
        }
    }


    private static void delete() {
        System.out.print("Enter Employee ID to delete: ");
        int id;

        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid numeric ID.");
            scanner.next(); // Clear invalid input
        }
        id = scanner.nextInt();
        scanner.nextLine(); // Consume the leftover newline

        System.out.println("Are you sure you want to delete this employee? (yes/no)");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes")) {
            System.out.println("Delete operation canceled.");
            return;
        }

        String query = "DELETE FROM Employee WHERE id = ?";
        try (Connection conn = connection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, id);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee deleted successfully.");
            } else {
                System.out.println("No employee found with the given ID.");
            }

        } catch (SQLException e) {
            System.err.println("Error during delete operation: " + e.getMessage());
        }
    }


    private static void display() {
        String query = "SELECT * FROM Employee";
        try (Connection con = connection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            System.out.println("\nEmployee Details:");
            System.out.printf("%-10s %-20s %-20s %-10s\n", "ID", "Name", "Job Role", "Salary");
            System.out.println("-------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("emp_name");
                String jobRole = rs.getString("job_role");
                double salary = rs.getDouble("salary");

                System.out.printf("%-10d %-20s %-20s %-10.2f\n", id, name, jobRole, salary);
            }
        } catch (SQLException e) {
            System.err.println("Database error during display: " + e.getMessage());
        }
    }

    private static Employee getInput() {
        Employee employee = new Employee();

        System.out.print("Enter Employee ID: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid numeric ID.");
            scanner.next(); // Clear invalid input
        }
        employee.setId(scanner.nextInt());
        scanner.nextLine(); // Consume the leftover newline

        System.out.print("Enter Employee Name: ");
        employee.setEmp_name(scanner.nextLine());

        System.out.print("Enter Employee Job Role: ");
        employee.setJob_role(scanner.nextLine());

        System.out.print("Enter Employee Salary: ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a valid numeric salary.");
            scanner.next(); // Clear invalid input
        }
        employee.setSalary(scanner.nextDouble());
        scanner.nextLine(); // Consume the leftover newline

        return employee;
    }

    private static Connection connection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return null;
    }
}
