import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class TrackingUpdateGUI extends JFrame {
    private JTextField productId;
    private JTextArea statusArea;

    public TrackingUpdateGUI() {
        setTitle("Tracking Update");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel label = new JLabel("Product ID:");
        label.setBounds(30, 20, 100, 25);
        add(label);

        productId = new JTextField();
        productId.setBounds(130, 20, 200, 25);
        add(productId);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(30, 60, 100, 25);
        add(statusLabel);

        statusArea = new JTextArea();
        statusArea.setBounds(130, 60, 200, 100);
        add(statusArea);

        JButton startBtn = new JButton("Start Tracking");
        JButton stopBtn = new JButton("Stop Tracking");
        startBtn.setBounds(50, 180, 130, 30);
        stopBtn.setBounds(200, 180, 130, 30);
        add(startBtn);
        add(stopBtn);

        startBtn.addActionListener(e -> sendUpdate("Start: " + statusArea.getText()));
        stopBtn.addActionListener(e -> sendUpdate("Stop: " + statusArea.getText()));

        setVisible(true);
    }

    private void sendUpdate(String status) {
        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("updateTracking");
            out.println(productId.getText());
            out.println(status);

            JOptionPane.showMessageDialog(this, in.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TrackingUpdateGUI();
    }
}
