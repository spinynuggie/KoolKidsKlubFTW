package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class MySQLConnection {
    public static void main(String[] args) {
        // Replace with your database details
        String url = "jdbc:mysql://localhost:3306/TeamFlow"; // Ensure your DB name is correct
        String user = "root"; // Replace with your MySQL username
        String password = "Password"; // Replace with your MySQL password

        try {
            // Register MySQL JDBC driver (optional but good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database!");

            // Create a statement
            Statement stmt = conn.createStatement();

            // Example query: Selecting data from a table
            String query = "SELECT * FROM Test"; // Replace with your actual table name
            ResultSet rs = stmt.executeQuery(query);

            // Loop through the result set and print data
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
            }

            // Close connections
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
