import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
    private JTextArea activityArea;
    private JButton startButton;
    private ServerSocket serverSocket;
    private boolean isRunning = false;

    public Server() {
        setTitle("Parcel Server");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        activityArea = new JTextArea();
        activityArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(activityArea);

        startButton = new JButton("Start Server");
        startButton.addActionListener(e -> startServer());

        add(scrollPane, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);
    }

    public void appendActivity(String message) {
        SwingUtilities.invokeLater(() -> {
            activityArea.append(message + "\n");
        });
    }

    private void startServer() {
        if (isRunning) return;

        isRunning = true;
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(1234);
                appendActivity("Server started on port 1234...");

                while (true) {
                    Socket client = serverSocket.accept();
                    appendActivity("New client connected: " + client.getInetAddress());
                    new ClientHandler(client, this).start(); // Pass GUI for reporting
                }
            } catch (Exception ex) {
                appendActivity("Server Error: " + ex.getMessage());
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Server().setVisible(true);
        });
    }
}
