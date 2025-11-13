import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ParcelTrackingGUI extends JFrame {
    private JTextField productId;
    private JTextArea result;

    public ParcelTrackingGUI() {
        setTitle("Parcel Tracking");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel label = new JLabel("Enter Product ID:");
        label.setBounds(20, 20, 120, 25);
        add(label);

        productId = new JTextField();
        productId.setBounds(140, 20, 200, 25);
        add(productId);

        JButton trackBtn = new JButton("Track");
        trackBtn.setBounds(140, 60, 100, 30);
        add(trackBtn);

        result = new JTextArea();
        result.setBounds(20, 100, 340, 140);
        result.setEditable(false);
        add(result);

        trackBtn.addActionListener(e -> handleTrack());

        setVisible(true);
    }

    private void handleTrack() {
        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("trackParcel");
            out.println(productId.getText());

            String response = in.readLine();
            if ("found".equals(response)) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 6; i++) {
                    sb.append(in.readLine()).append("\n");
                }
                result.setText(sb.toString());
            } else {
                result.setText("Parcel not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ParcelTrackingGUI();
    }
}
