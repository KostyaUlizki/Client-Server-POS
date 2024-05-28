import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static Socket clientSocket;
    private static PrintWriter outputToServer;
    private static BufferedReader inputFromServer;
    private static String usersBranch;
    private static String currentChatRoom = null;
    private static Thread serverListenerThread;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        try {
            // Create a socket and connect to the server
            clientSocket = new Socket("localhost", 4999);

            // Create input and output streams for server communication
            inputFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputToServer = new PrintWriter(clientSocket.getOutputStream(), true);

            // Read username and password from the user
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter username: ");
            String username = userInput.readLine();
            System.out.print("Enter password: ");
            String password = userInput.readLine();

            // Send username and password to the server
            outputToServer.println(username);
            outputToServer.println(password);

            // Receive authentication result from the server
            boolean isAuthenticated = Boolean.parseBoolean(inputFromServer.readLine());

            //Receive user's branch
            usersBranch = inputFromServer.readLine();

            // Display authentication result
            if (isAuthenticated) {
                System.out.println("Authentication successful. You are now connected to the server.");
            } else {
                System.out.println("Authentication failed. Invalid username or password.");
                clientSocket.close();
                return;
            }

            // Create a thread for listening to server messages
            serverListenerThread = new Thread(new ServerListener());
            serverListenerThread.start();

            // Start a loop to listen for server messages and handle them

            while (true) {
                System.out.println("Enter which task you want to perform:");
                System.out.println("1. Employee management");
                System.out.println("2. Customer management");
                System.out.println("3. Inventory management");
                System.out.println("4. Selling");
                System.out.println("5. Reports");
                System.out.println("6. Chat Room");
                System.out.println("0. Exit");

                int mainChoice = scanner.nextInt();

                switch (mainChoice) {
                    case 1:
                        handleEmployeeManagement(scanner);
                        break;
                    case 2:
                        handleCustomerManagement(scanner);
                        break;
                    case 3:
                        handleInventoryManagement(scanner);
                        break;
                    case 4:
                        handleSelling(scanner);
                        break;
                    case 5:
                        handleReports(scanner);
                        break;
                    case 6:
                        handleChatRoom(scanner);
                        break;
                    case 0:
                        System.out.println("Exiting the program.");
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void handleChatRoom(Scanner scanner) throws IOException {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter the name of the branch that you want to chat with: ");
        String chatRoomName = userInput.readLine(); // Read the chat room name from user input
//        System.out.print("Enter your messages: ");
        outputToServer.println("Chat");
        outputToServer.println(chatRoomName); // Send the chat room name to the server
        do {
            String message = userInput.readLine(); // Read the chat message from user input

            if (message.equals("/exit"))
                break;


            outputToServer.println(message);
        } while (true);
    }






    // Thread for listening to chat messages from the server
    static class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    if (inputFromServer.ready()) {
                        String receivedMessage = inputFromServer.readLine();
                        if (receivedMessage != null) {
                            // Display the received chat message to the user
                            System.out.println(receivedMessage);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void handleSelling(Scanner scanner) throws IOException {

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Customer's id:");
        String id = userInput.readLine();

        System.out.println("select an item:");
        String item = userInput.readLine();

        System.out.println("how many?");
        String quantity = userInput.readLine();


        outputToServer.println("Selling");
        outputToServer.println(id);
        outputToServer.println(item);
        outputToServer.println(quantity);


        System.out.println(inputFromServer.readLine());


    }


    private static void handleInventoryManagement(Scanner scanner) throws IOException {
        while (true) {
            System.out.println("Inventory Management Menu:");
            System.out.println("1. Display Inventory");
            System.out.println("2. Add product to inventory");
            System.out.println("3. Back to main menu");

            int workerChoice = scanner.nextInt();

            switch (workerChoice) {
                case 1:
                    outputToServer.println("displayInventory");
                    outputToServer.println(usersBranch);
                    try {
                        System.out.println(inputFromServer.readLine().replace("-n-", "\n"));
                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                    }

                    break;

                case 2:
                    BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("Enter product name:");
                    String productName = userInput.readLine();

                    System.out.println("Enter product price");
                    String productPrice = userInput.readLine();

                    System.out.println("Enter quantity:");
                    String productQuantity = userInput.readLine();

                    outputToServer.println("addProductToInventory");
                    outputToServer.println(productName);
                    outputToServer.println(productPrice);
                    outputToServer.println(productQuantity);

                    System.out.println(inputFromServer.readLine());
                    break;
                case 3:
                    return; // Return to the main menu
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }


    private static void handleCustomerManagement(Scanner scanner) throws IOException {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Customer Management Menu:");
            System.out.println("1. Create a customer");
            System.out.println("2. Update a customer");
            System.out.println("3. Back to main menu");

            int workerChoice = scanner.nextInt();

            switch (workerChoice) {
                case 1:
                    System.out.print("Enter id: ");
                    String id = userInput.readLine();
                    System.out.print("Enter full name: ");
                    String fullName = userInput.readLine();
                    System.out.print("Enter phone number: ");
                    String phoneNumber = userInput.readLine();
                    System.out.print("Enter customer type (New, Returned, VIP): ");
                    String CustomerType = userInput.readLine();

                    outputToServer.println("createCustomer");
                    outputToServer.println(id);
                    outputToServer.println(fullName);
                    outputToServer.println(phoneNumber);
                    outputToServer.println(CustomerType);


                    break;
                case 2:
                    // Code for updating a worker
                    break;
                case 3:
                    return; // Return to the main menu
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
            System.out.println("\n-------------" + inputFromServer.readLine() + "-------------" + "\n");

        }
    }

    private static void handleEmployeeManagement(Scanner scanner) throws IOException{
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Employee Management Menu:");
            System.out.println("1. Create an employee");
            System.out.println("2. Delete employee");
            System.out.println("3. Display all employees");
            System.out.println("4. Back to main menu");

            int workerChoice = scanner.nextInt();

            switch (workerChoice) {
                case 1:
                    System.out.println("Employee name:");
                    String employee_name = userInput.readLine();
                    System.out.println("Employee Phone Number:");
                    String employeePhoneNumber = userInput.readLine();
                    System.out.println("Affiliated branch:");
                    String branch = userInput.readLine();
                    System.out.println("For a Cashier press 1");
                    System.out.println("For a Manager press 2");
                    System.out.println("For a Salesman press 3");
                    int roleNum = Integer.parseInt(userInput.readLine());
                    String role = Employee.employeeRole.returnRoleBasedOnNumbers(roleNum);

                    outputToServer.println("createEmployee");
                    outputToServer.println(employee_name);
                    outputToServer.println(employeePhoneNumber);
                    outputToServer.println(branch);
                    outputToServer.println(role);

                    System.out.println(inputFromServer.readLine());

                    break;
                case 2:
                    System.out.println("Employee phone number:");
                    String employeeToDeletePhoneNumber = userInput.readLine();
                    outputToServer.println("deleteEmployee");
                    outputToServer.println(employeeToDeletePhoneNumber);

                    System.out.println(inputFromServer.readLine());

                    break;
                case 3:
                    outputToServer.println("displayEmployees");
                    try {
                        System.out.println(inputFromServer.readLine().replace("-n-", "\n"));
                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                    }


                case 4:
                    return; // Return to the main menu
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void handleReports(Scanner scanner) throws IOException {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Report Menu:");
            System.out.println("1. Get report by branch name");
            System.out.println("2. Get report by item name");
            System.out.println("3. Get all transactions in a text file");
            System.out.println("4. Back to main menu");

            int reportChoice = scanner.nextInt();

            switch (reportChoice){
                case 1:
                    System.out.println("Enter desired branch name");
                    String branchName = userInput.readLine();
                    outputToServer.println("reportByBranch");
                    outputToServer.println(branchName);
                    try {
                        System.out.println(inputFromServer.readLine().replace("-n-", "\n"));
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    System.out.println("Enter desired product name");
                    String productToReport = userInput.readLine();
                    outputToServer.println("reportByProduct");
                    outputToServer.println(productToReport);

                    try {
                        System.out.println(inputFromServer.readLine().replace("-n-", "\n"));
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                    break;
                case 3:
                    outputToServer.println("getReportFile");
                    inputFromServer.readLine();
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }


        }
    }
}





