import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ParcelEntryGUI extends JFrame {
    private JTextField productId, productName, sender, receiver, origin, destination, price;

    public ParcelEntryGUI() {
        setTitle("Parcel Entry");
        setSize(400, 400);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        String[] labels = { "Product ID", "Product Name", "Sender", "Receiver", "Origin", "Destination", "Price" };
        JTextField[] fields = new JTextField[labels.length];
        int y = 20;
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i] + ":");
            label.setBounds(30, y, 100, 25);
            add(label);
            fields[i] = new JTextField();
            fields[i].setBounds(140, y, 200, 25);
            add(fields[i]);
            y += 40;
        }

        productId = fields[0]; productName = fields[1]; sender = fields[2]; receiver = fields[3];
        origin = fields[4]; destination = fields[5]; price = fields[6];

        JButton submit = new JButton("Submit");
        submit.setBounds(140, y, 100, 30);
        add(submit);
        submit.addActionListener(e -> handleSubmit());

        setVisible(true);
    }

    private void handleSubmit() {
        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("submitParcel");
            out.println(productId.getText());
            out.println(productName.getText());
            out.println(sender.getText());
            out.println(receiver.getText());
            out.println(origin.getText());
            out.println(destination.getText());
            out.println(price.getText());

            JOptionPane.showMessageDialog(this, in.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ParcelEntryGUI();
    }
}
