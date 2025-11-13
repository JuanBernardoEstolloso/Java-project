// Request.java
package finals;

import java.io.Serializable;

public class Request implements Serializable {
    public enum RequestType {
        LOGIN, REGISTER, FORGOT_PASSWORD, UPDATE_PASSWORD, GET_USER_DETAILS
    }
    
    private RequestType type;
    private String username;
    private String password;
    private String newPassword;
    private String code;
    
    // Constructors for different request types
    public Request(RequestType type, String username, String password) {
        this.type = type;
        this.username = username;
        this.password = password;
    }
    
    public Request(RequestType type, String username, String password, String code) {
        this(type, username, password);
        this.code = code;
    }
    
    public Request(RequestType type, String username, String password, String newPassword, String code) {
        this(type, username, password, code);
        this.newPassword = newPassword;
    }
    
    // Getters
    public RequestType getType() { return type; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getNewPassword() { return newPassword; }
    public String getCode() { return code; }
}