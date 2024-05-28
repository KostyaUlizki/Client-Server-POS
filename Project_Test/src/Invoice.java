import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class Invoice {
    private String productName;
    private int quantity;
    private double priceTotal;
    private String customerName;
    private String branch;

    private static String invoiceHeader = "Invoice number : Product Name : Quantity : Total Price : Customer Name : Branch";

    static DB_Credentials db_creds = new DB_Credentials();

    static final private String DB_URL = db_creds.getUrl();
    static final private String DB_USERNAME = db_creds.getUsername();
    static final private String DB_PASSWORD = db_creds.getPassword();

    public Invoice(String productName, int quantity, double priceTotal, String customerName, String branch) {
        this.productName = productName;
        this.quantity = quantity;
        this.priceTotal = priceTotal;
        this.customerName = customerName;
        this.branch = branch;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "INSERT INTO invoices (invoiceNumber, productName, quantity, priceTotal, customer, branch)" +
                    " VALUES (?, ?, ?, ?, ? , ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, null);
                stmt.setString(2, productName);
                stmt.setInt(3, quantity);
                stmt.setDouble(4, priceTotal);
                stmt.setString(5, customerName);
                stmt.setString(6, branch);
                stmt.executeUpdate();

                stmt.close();
                conn.close();
            }
            System.out.println("Invoice created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating Invoice: " + e.getMessage());
        }

    }
    public static String GetAllBranchInvoices(String branch){
        StringBuilder BranchInvoicesSB = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT * FROM invoices WHERE branch = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, branch);
                ResultSet resultSet = stmt.executeQuery();

                if (!resultSet.next()) {
                    BranchInvoicesSB.append("No data found for branch: " + branch);
                }
                else{
                    BranchInvoicesSB.append("Invoice number : Product Name : Quantity : Total Price : Customer Name : Branch" + "-n-");
                    do {
                        BranchInvoicesSB.append(resultSet.getString("invoiceNumber") + " : " +
                                resultSet.getString("productName") + " : " +
                                resultSet.getString("quantity")+ " : " +
                                resultSet.getString("priceTotal") + " : " +
                                resultSet.getString("customer") + " : " +
                                resultSet.getString("branch") + "-n-");
                    }while (resultSet.next());
                }
                // סגירת החיבור למסד הנתונים
                resultSet.close();
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BranchInvoicesSB.toString();
    }
    public static String GetInvoicesByProduct(String productName){
        StringBuilder ProductInvoicesSB = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT * FROM invoices WHERE productName = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, productName);
                ResultSet resultSet = stmt.executeQuery();

                if (!resultSet.next()) {
                    ProductInvoicesSB.append("No data found for product: " + productName);
                }
                else{
                    ProductInvoicesSB.append(invoiceHeader + "-n-");
                    do {
                        ProductInvoicesSB.append(resultSet.getString("invoiceNumber") + " : " +
                                resultSet.getString("productName") + " : " +
                                resultSet.getString("quantity")+ " : " +
                                resultSet.getString("priceTotal") + " : " +
                                resultSet.getString("customer") + " : " +
                                resultSet.getString("branch") + "-n-");
                    }while (resultSet.next());
                }
                // סגירת החיבור למסד הנתונים
                resultSet.close();
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ProductInvoicesSB.toString();
    }
    public static void createInvoiceFile(){

        String outputFile = "invoices.txt";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT * FROM invoices";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet resultSet = stmt.executeQuery();
                if (!resultSet.next()) {
                    System.out.println("No data found");
                }
                else{
                    FileWriter writer = new FileWriter(outputFile);
                    File invoiceFile = new File(outputFile);

                    writer.write(invoiceHeader + System.lineSeparator());
                    // Iterate through the result set and write data to the text file
                    do {
                        String invoiceNumber = resultSet.getString("invoiceNumber");
                        String productName = resultSet.getString("productName");
                        String quantity = resultSet.getString("quantity");
                        String priceTotal = String.valueOf(resultSet.getString("priceTotal"));
                        String customer = resultSet.getString("customer");
                        String branch = resultSet.getString("branch");

                        // Format and write data to the text file
                        String line = invoiceNumber + " , " +
                                productName + " , " +
                                quantity + " , " +
                                priceTotal + " , " +
                                customer + " , " +
                                branch;
                        writer.write(line + System.lineSeparator());
                    }while (resultSet.next());

                    writer.close();

                    String absolutePath =invoiceFile.getAbsolutePath();

                    if(Desktop.isDesktopSupported()){
                        Desktop desktop = Desktop.getDesktop();

                        //open the file using desktop
                        desktop.open(new File(absolutePath));
                    }
                    else {
                        System.out.println("Desktop not supported will not open file");
                    }


                }
                // סגירת החיבור למסד הנתונים
                resultSet.close();
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

}
