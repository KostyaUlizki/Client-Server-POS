import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class Product {
    DB_Credentials db_creds = new DB_Credentials();

    final private String url = db_creds.getUrl();
    final private String username = db_creds.getUsername();
    final private String password = db_creds.getPassword();

    private String name;
    private int price;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO product (name, price) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setInt(2, price);
                stmt.executeUpdate();
            }

            System.out.println("Product created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating product: " + e.getMessage());
        }
    }


    public String getName() {
        return this.name;
    }
    public int getPrice(){return this.price;}


//    @Override
//    public String toString() {
//        //return ("You have" + this.quantity + "of" + this.name);
//    }
}
