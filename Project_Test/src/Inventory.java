import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Inventory {
    static DB_Credentials db_creds = new DB_Credentials();

    static final private String DB_URL = db_creds.getUrl();
    static final private String DB_USERNAME = db_creds.getUsername();
    static final private String DB_PASSWORD = db_creds.getPassword();

    private String branch;

    public Inventory(String branch) {
        this.branch = branch;
    }

    public String displayInventoryForBranch() {
        StringBuilder inventoryDetails = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT * FROM inventory WHERE branch = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, branch);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet == null) {
                    inventoryDetails.append("No data found for branch: " + branch);
                    return inventoryDetails.toString();
                }
                inventoryDetails.append("Product Name : Quantity" + "-n-");
                while (resultSet.next()) {
                    inventoryDetails.append(resultSet.getString("name")
                            + " : " + resultSet.getString("quantity")
                            + "-n-");
                }
                // סגירת החיבור למסד הנתונים
                resultSet.close();
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventoryDetails.toString();

    }
    public static String addProductToInventory(Product product, int quantity, String branch){
        StringBuilder returnString = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT name FROM inventory WHERE name = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, product.getName());

                ResultSet resultSet = stmt.executeQuery();
                if(resultSet.next()){
                    returnString.append("Product already in database, adding to its quantity instead");
                    String updateQuery = "UPDATE inventory SET quantity = quantity + ? WHERE name = ?";

                    try(PreparedStatement updateStmt = conn.prepareStatement(updateQuery)){
                        updateStmt.setInt(1, quantity);
                        updateStmt.setString(2, product.getName());
                        updateStmt.executeUpdate();
                    }
                }
                else{
                    String insertQuery = "INSERT INTO inventory (name, quantity, branch) VALUES (?, ?, ?)";
                    try(PreparedStatement insertStmt = conn.prepareStatement(insertQuery)){
                        insertStmt.setString(1, product.getName());
                        insertStmt.setInt(2, quantity);
                        insertStmt.setString(3, branch);
                        int insertResult = insertStmt.executeUpdate();
                        if(insertResult > 0){
                            returnString.append("Product added successfully");
                        }
                        else{
                            returnString.append("No rows affected");
                        }
                    }
                }

            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return returnString.toString();
    }

    public int getItemQuantity(String itemName) {
        int num = 0;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT quantity FROM inventory WHERE name = ? AND branch = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, itemName);
                stmt.setString(2, branch);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    num = resultSet.getInt("quantity");
                }

                // סגירת החיבור למסד הנתונים
                resultSet.close();
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return num;
    }


    public void purchasedProduct(String itemName, int quantity) {

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "UPDATE inventory SET quantity = quantity - ? WHERE name = ? AND branch = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, quantity);
                stmt.setString(2, itemName);
                stmt.setString(3, branch);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}









