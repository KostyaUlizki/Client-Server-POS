import java.sql.*;

public class UserAuthentication {
    DB_Credentials db_creds = new DB_Credentials();

    final private String DB_URL = db_creds.getUrl();
    final private String DB_USERNAME = db_creds.getUsername();
    final private String DB_PASSWORD = db_creds.getPassword();

    private String branch = null;

    public boolean authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            // Prepare the SQL statement
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Check if the result set has any rows (user found)
//            return resultSet.next();
            if (resultSet.next()) {
                branch = resultSet.getString("branch");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getBranch() {
        return branch;
    }

}
