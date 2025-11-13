// Response.java
package finals;

import java.io.Serializable;

public class Response implements Serializable {
    private boolean success;
    private String message;
    private User user;
    
    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public Response(boolean success, String message, User user) {
        this(success, message);
        this.user = user;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public User getUser() { return user; }
}