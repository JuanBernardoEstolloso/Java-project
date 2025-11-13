

import finals.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
public static User getUserDetails(String username) {
        User user = null;
        String sql = "SELECT username, credit, balance FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setUsername(rs.getString("username"));
                user.setCredit(rs.getBigDecimal("credit"));
                user.setBalance(rs.getBigDecimal("balance"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    
// Use your actual DB details here
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_auth";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Replace with your actual password if set

    public static boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if user is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
    }
    

    // Register user only if exact (username, password, code) triple does NOT exist
    public static boolean registerUser(String username, String password, String code) {
        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ? AND code = ?";
        String insertQuery = "INSERT INTO users (username, password, code) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            // Check if exact triple exists
            checkStmt.setString(1, username);
            checkStmt.setString(2, password);
            checkStmt.setString(3, code);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Exact account already exists
            }

            // Insert new user
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password); // Consider hashing for real apps
                insertStmt.setString(3, code);

                int rowsInserted = insertStmt.executeUpdate();
                return rowsInserted > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    } public static boolean verifyUserCode(String username, String code) {
        String query = "SELECT * FROM users WHERE username = ? AND code = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, code);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();  // true if found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update password for a given username
    public static boolean updatePassword(String username, String newPassword) {
        String updateQuery = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
