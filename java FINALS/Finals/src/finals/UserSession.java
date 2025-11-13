package finals;

public class UserSession {
    private static String username;
    private static String fullName;  // or other profile info

    public static void setUsername(String user) {
        username = user;
    }

    public static String getUsername() {
        return username;
    }

    public static void setFullName(String name) {
        fullName = name;
    }

    public static String getFullName() {
        return fullName;
    }

    // Add other profile fields as needed
}
