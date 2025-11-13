// ClientService.java
package finals;

import java.io.*;
import java.net.*;
import java.math.BigDecimal;

public class ClientService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final int TIMEOUT_MS = 5000; // 5 second timeout
    
    public static boolean validateLogin(String username, String password) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            
            socket.setSoTimeout(TIMEOUT_MS);
            
            // Create and send login request
            Request request = new Request(Request.RequestType.LOGIN, username, password);
            out.writeObject(request);
            
            // Get response from server
            Response response = (Response) in.readObject();
            return response.isSuccess();
            
        } catch (SocketTimeoutException e) {
            System.err.println("Login request timed out");
            return false;
        } catch (IOException e) {
            System.err.println("Network error during login: " + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("Protocol error during login");
            return false;
        }
    }
    public static boolean updatePassword(String username, String newPassword) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            
            socket.setSoTimeout(TIMEOUT_MS); // Set timeout
            
            // Create and send password update request
            Request request = new Request(Request.RequestType.UPDATE_PASSWORD, username, null, newPassword, null);
            out.writeObject(request);
            
            // Get response from server
            Response response = (Response) in.readObject();
            return response.isSuccess();
            
        } catch (SocketTimeoutException e) {
            System.err.println("Password update request timed out");
            return false;
        } catch (IOException e) {
            System.err.println("Network error during password update: " + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("Protocol error during password update");
            return false;
        }
    }
    
  public static User getUserDetails(String username) {
    try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
         ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
        
        socket.setSoTimeout(TIMEOUT_MS);
        
        // Create and send request
        Request request = new Request(Request.RequestType.GET_USER_DETAILS, username, null);
        out.writeObject(request);
        
        // Get response
        Response response = (Response) in.readObject();
        if (response.isSuccess()) {
            return response.getUser();
        }
        return null;
        
    } catch (SocketTimeoutException e) {
        System.err.println("Request timed out");
        return null;
    } catch (IOException e) {
        System.err.println("Network error: " + e.getMessage());
        return null;
    } catch (ClassNotFoundException e) {
        System.err.println("Protocol error");
        return null;
    }
}
}