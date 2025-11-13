import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, createButton;

    public LoginGUI() {
        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 20, 100, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(120, 20, 140, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 60, 100, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 60, 140, 25);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(20, 100, 100, 30);
        add(loginButton);

        createButton = new JButton("Create Account");
        createButton.setBounds(130, 100, 130, 30);
        add(createButton);

        loginButton.addActionListener(e -> handleLogin());
        createButton.addActionListener(e -> handleCreate());

        setVisible(true);
    }

    private void handleLogin() {
        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("login");
            out.println(usernameField.getText());
            out.println(new String(passwordField.getPassword()));

            String response = in.readLine();
            JOptionPane.showMessageDialog(this, response.equals("success") ? "Login successful" : "Login failed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCreate() {
        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("createAccount");
            out.println(usernameField.getText());
            out.println(new String(passwordField.getPassword()));

            JOptionPane.showMessageDialog(this, in.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}
