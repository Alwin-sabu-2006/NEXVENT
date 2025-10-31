public class Usersession {

    private static int userId;
    private static String username;

    public static void login(int id, String name) {
        userId = id;
        username = name;
    }

    public static void logout() {
        userId = 0;
        username = null;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUsername() {
        return username;
    }

    public static boolean isLoggedIn() {
        return userId > 0;
    }
}