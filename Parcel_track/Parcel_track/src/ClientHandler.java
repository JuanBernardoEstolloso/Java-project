import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Server gui;

    private static Set<PrintWriter> clients = Collections.synchronizedSet(new HashSet<>());

    private static final String DB_URL = "jdbc:mysql://localhost:3306/parcel_tracking";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public ClientHandler(Socket socket, Server gui) {
        this.socket = socket;
        this.gui = gui;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            clients.add(out);
        } catch (IOException e) {
            gui.appendActivity("Client I/O Error: " + e.getMessage());
        }
    }

    private void broadcast(String message) {
        gui.appendActivity(message);
        synchronized (clients) {
            for (PrintWriter client : clients) {
                client.println("Activity: " + message);
            }
        }
    }

    public void run() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            while (true) {
                String command = in.readLine();
                if (command == null) break;

                switch (command) {
                    case "login": handleLogin(conn); break;
                    case "createAccount": handleCreateAccount(conn); break;
                    case "submitParcel": handleSubmitParcel(conn); break;
                    case "trackParcel": handleTrackParcel(conn); break;
                    case "updateTracking": handleUpdateTracking(conn); break;
                    default: out.println("Unknown command."); break;
                }
            }
        } catch (Exception e) {
            gui.appendActivity("Error handling client: " + e.getMessage());
        } finally {
            clients.remove(out);
            try {
                socket.close();
            } catch (IOException e) {
                gui.appendActivity("Socket close error: " + e.getMessage());
            }
        }
    }

    private void handleLogin(Connection conn) throws SQLException, IOException {
        String user = in.readLine();
        String pass = in.readLine();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
        ps.setString(1, user);
        ps.setString(2, pass);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            out.println("success");
            broadcast("User logged in: " + user);
        } else {
            out.println("failure");
        }
    }

    private void handleCreateAccount(Connection conn) throws SQLException, IOException {
        String user = in.readLine();
        String pass = in.readLine();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
        ps.setString(1, user);
        ps.setString(2, pass);
        ps.executeUpdate();
        out.println("Account created");
        broadcast("New account: " + user);
    }

    private void handleSubmitParcel(Connection conn) throws SQLException, IOException {
        String id = in.readLine();
        String name = in.readLine();
        String sender = in.readLine();
        String receiver = in.readLine();
        String origin = in.readLine();
        String dest = in.readLine();
        double price = Double.parseDouble(in.readLine());

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO parcels (product_id, product_name, sender_name, receiver_name, origin, destination, price) VALUES (?, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, id);
        ps.setString(2, name);
        ps.setString(3, sender);
        ps.setString(4, receiver);
        ps.setString(5, origin);
        ps.setString(6, dest);
        ps.setDouble(7, price);
        ps.executeUpdate();
        out.println("Parcel submitted");
        broadcast("Parcel submitted: " + id);
    }

    private void handleTrackParcel(Connection conn) throws SQLException, IOException {
        String id = in.readLine();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM parcels WHERE product_id=?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            out.println("found");
            out.println("Product Name: " + rs.getString("product_name"));
            out.println("Sender: " + rs.getString("sender_name"));
            out.println("Receiver: " + rs.getString("receiver_name"));
            out.println("Origin: " + rs.getString("origin"));
            out.println("Destination: " + rs.getString("destination"));
            out.println("Price: " + rs.getDouble("price"));
        } else {
            out.println("not_found");
        }
    }

    private void handleUpdateTracking(Connection conn) throws SQLException, IOException {
        String id = in.readLine();
        String status = in.readLine();
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO tracking_updates (product_id, status) VALUES (?, ?)");
        ps.setString(1, id);
        ps.setString(2, status);
        ps.executeUpdate();
        out.println("Tracking updated");
        broadcast("Tracking for " + id + ": " + status);
    }
}
