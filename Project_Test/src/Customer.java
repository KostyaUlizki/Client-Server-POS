import java.sql.*;

public class Customer {
    DB_Credentials db_creds = new DB_Credentials();

    final private String DB_URL = db_creds.getUrl();
    final private String DB_USERNAME = db_creds.getUsername();
    final private String DB_PASSWORD = db_creds.getPassword();

    private String fullName;
    private String id;
    private String phoneNumber;
    private String customerType;

    public StringBuilder stringBuilder = new StringBuilder();
    private static double totalAmount;


    public Customer(String id) {

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT * FROM customers WHERE Id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    String fullName = resultSet.getString("FullName");
                    String idNumber = resultSet.getString("Id");
                    String phoneNumber = resultSet.getString("PhoneNumber");
                    String customerType = resultSet.getString("Type");

                    this.fullName = fullName;
                    this.id = idNumber;
                    this.phoneNumber = phoneNumber;
                    this.customerType = customerType;

//                    // הדפסת הפרטים
//                    System.out.println("Customer Details:");
//                    System.out.println("    Full Name: " + fullName);
//                    System.out.println("    ID Number: " + idNumber);
//                    System.out.println("    Phone Number: " + phoneNumber);
//                    System.out.println("    Customer Type: " + customerType);
                } else {
                    System.out.println("Customer not found.");
                }

                // סגירת החיבור למסד הנתונים
                resultSet.close();
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Customer(String id, String fullName, String phoneNumber, String customerType) {
        StringBuilder st = null;
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.customerType = customerType;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "INSERT INTO customers (Id, FullName, PhoneNumber, Type) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                stmt.setString(2, fullName);
                stmt.setString(3, phoneNumber);
                stmt.setString(4, customerType);
                stmt.executeUpdate();
            }
            stringBuilder.append("Customer created successfully.");
            System.out.println("Customer created successfully.");
        } catch (SQLException e) {
            stringBuilder.append("Customer already in the system");
            System.out.println("Error creating customer: " + e.getMessage());
        }
    }



    public void purchase(String itemName, int quantity,String userName ,String branch) {
        // Common purchase logic
        this.totalAmount = calculateTotalAmount(itemName, quantity);
        System.out.println(getFullName() + " is purchasing " + quantity + " " + itemName);
        System.out.println("Total amount: " + totalAmount);

        Inventory inventory = new Inventory(branch);
        inventory.purchasedProduct(itemName,quantity); // update the Inventory


        Invoice invoice = new Invoice(itemName,quantity,totalAmount, userName,branch);

    }

    protected double calculateTotalAmount(String itemName, int quantity) {
        // Calculate total amount without discount
        return quantity * getItemPrice(itemName);  // Subclasses will override this method to apply discounts
    }

    protected double getItemPrice(String itemName){
        double num = 0;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT price FROM product WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, itemName);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    num = resultSet.getDouble("price");
                }

                // Close connection to database
                resultSet.close();
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return num;
    }



// Getters and Setters

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() { return customerType; }

    public void setType(String type) { this.customerType = type; }

}


