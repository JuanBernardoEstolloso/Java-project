// Server.java
package finals;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.math.BigDecimal;

public class Server {
    private static final int PORT = 12345;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_auth";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
                    
                    // Read request from client
                    Request request = (Request) in.readObject();
                    
                    // Process request
                    Response response = processRequest(request);
                    
                    // Send response back to client
                    out.writeObject(response);
                    out.flush();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Response processRequest(Request request) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            switch (request.getType()) {
                case LOGIN:
                    return handleLogin(conn, request);
                case REGISTER:
                    return handleRegister(conn, request);
                case FORGOT_PASSWORD:
                    return handleForgotPassword(conn, request);
                case UPDATE_PASSWORD:
                    return handleUpdatePassword(conn, request);
                case GET_USER_DETAILS:
                    return handleGetUserDetails(conn, request);
                default:
                    return new Response(false, "Invalid request type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(false, "Database error: " + e.getMessage());
        }
    }

    private static Response handleLogin(Connection conn, Request request) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, request.getUsername());
            stmt.setString(2, request.getPassword());
            ResultSet rs = stmt.executeQuery();
            return new Response(rs.next(), rs.next() ? "Login successful" : "Invalid credentials");
        }
    }

    private static Response handleRegister(Connection conn, Request request) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ? AND code = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, request.getUsername());
            checkStmt.setString(2, request.getPassword());
            checkStmt.setString(3, request.getCode());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return new Response(false, "Account already exists");
            }
        }

        String insertQuery = "INSERT INTO users (username, password, code) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            insertStmt.setString(1, request.getUsername());
            insertStmt.setString(2, request.getPassword());
            insertStmt.setString(3, request.getCode());
            int rowsInserted = insertStmt.executeUpdate();
            return new Response(rowsInserted > 0, rowsInserted > 0 ? "Registration successful" : "Registration failed");
        }
    }

    private static Response handleForgotPassword(Connection conn, Request request) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, request.getUsername());
            stmt.setString(2, request.getCode());
            ResultSet rs = stmt.executeQuery();
            return new Response(rs.next(), rs.next() ? "Verification successful" : "Invalid username or code");
        }
    }

    private static Response handleUpdatePassword(Connection conn, Request request) throws SQLException {
        String updateQuery = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, request.getNewPassword());
            stmt.setString(2, request.getUsername());
            int rowsAffected = stmt.executeUpdate();
            return new Response(rowsAffected > 0, rowsAffected > 0 ? "Password updated" : "Password update failed");
        }
    }

    private static Response handleGetUserDetails(Connection conn, Request request) throws SQLException {
        String sql = "SELECT username, credit, balance FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, request.getUsername());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setCredit(rs.getBigDecimal("credit"));
                user.setBalance(rs.getBigDecimal("balance"));
                return new Response(true, "User details retrieved", user);
            }
            return new Response(false, "User not found");
        }
    }
}