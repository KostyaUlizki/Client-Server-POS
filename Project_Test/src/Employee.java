import java.sql.*;

public class Employee {
    protected int phoneNumber;
    protected String employeeName;
    protected String branch;

    protected employeeRole role;

    public enum employeeRole{
        Cashier,
        Manager,
        Salesman;

        public static String returnRoleBasedOnNumbers(int num){
            String returnVal;
            switch(num){
                case 1:
                    returnVal = employeeRole.Cashier.name();
                    break;
                case 2:
                    returnVal = employeeRole.Manager.name();
                    break;
                case 3:
                    returnVal = employeeRole.Salesman.name();
                    break;
                default:
                    returnVal = null;
            }
            return returnVal;
        }
    }

    static DB_Credentials db_creds = new DB_Credentials();

    final static private String url = db_creds.getUrl();
    final static private String username = db_creds.getUsername();
    final static private String password = db_creds.getPassword();

    public Employee(int phoneNumber, String employeeName, String branch, employeeRole role){
        this.phoneNumber = phoneNumber;
        this.employeeName = employeeName;
        this.branch = branch;
        this.role = role;

        createEmployee(phoneNumber, employeeName,branch,role.name());
    }

    public static int createEmployee(int phoneNumber, String employeeName, String branch, String role){
        int returnFlag = 0;
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO employee (employee_ID ,employee_name, phoneNumber, branch, role) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, null);
                stmt.setString(2, employeeName);
                stmt.setInt(3, phoneNumber);
                stmt.setString(4, branch);
                stmt.setString(5, role);
                stmt.executeUpdate();
            }

            System.out.println("Employee created successfully.");
            returnFlag = 1;
        } catch (SQLException e) {
            System.out.println("Error creating employee: " + e.getMessage());
        }
        return returnFlag;
    }
    public static int deleteEmployee(String phoneNumber){
        int returnFlag = 0;
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "DELETE from employee WHERE  phoneNumber = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, phoneNumber);
                stmt.executeUpdate();

                System.out.println("Employee deleted successfully.");
                returnFlag = 1;

                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error creating employee: " + e.getMessage());
        }
        return returnFlag;
    }
    public static String displayAllEmployees(){
        StringBuilder stringBuilder = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT * FROM employee";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet resultSet = stmt.executeQuery();
                if(!resultSet.next()){
                    stringBuilder.append("No employees found");
                }
                else{
                    stringBuilder.append("Employee ID : Employee Name : Phone Number : Branch : Role" + "-n-");
                    do{
                        stringBuilder.append(resultSet.getString("employee_ID") + " : " +
                                resultSet.getString("employee_name") + " : " +
                                resultSet.getString("phoneNumber")+ " : " +
                                resultSet.getString("branch") + " : " +
                                resultSet.getString("role") + " : " + "-n-");
                    }while(resultSet.next());
                }
                resultSet.close();
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error getting all employees : " + e.getMessage());
        }

        return stringBuilder.toString();
    }

}
