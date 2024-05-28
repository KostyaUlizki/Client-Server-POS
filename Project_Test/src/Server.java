import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.io.IOException;
import java.util.logging.*;

public class Server {
    static Vector<SocketData> allConnections = new Vector<SocketData>();
    private static Map<String, ChatRoom> chatRooms = new HashMap<>(); // Store chat rooms by name
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static BufferedWriter chatLogFile;

    public static void main(String[] args) throws IOException{

        chatLogFile = new BufferedWriter(new FileWriter("chat_log.txt", true));
        LogManager.getLogManager().readConfiguration(
                Server.class.getClassLoader().getResourceAsStream("logging.properties")
        );

        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(4999);

            System.out.println("Server started. Waiting for client connection.....");
            logger.info("Server started. Waiting for client connection.....");

            while (true) {
                // Accept client connection
                Socket clientSocket = serverSocket.accept();

                // Handle client request in a separate thread
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred", e);
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private SocketData currentSocketData;
        private String currentChatRoom = null; // Track the current chat room
        private boolean isInChatSession = false; // Flag to indicate if the client is in a chat session




        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);

            try (
                    // Create input and output streams for client communication
                    BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                    BufferedReader outputToClient = new BufferedReader(new OutputStreamWriter(clientSocket.getOutputStream());
                    PrintWriter outputToClient = new PrintWriter(clientSocket.getOutputStream(), true)


            ) {

                String username = inputFromClient.readLine();
                String password = inputFromClient.readLine();
                UserAuthentication userAuth = new UserAuthentication();
                boolean isAuthenticated = userAuth.authenticateUser(username, password);
                String usersBranch = userAuth.getBranch();
                // Send authentication & branch result to the client
                outputToClient.println(isAuthenticated);
                outputToClient.println(usersBranch);

                logger.info("Client connected: " + clientSocket.getInetAddress());
                logger.info("Username: " + username + ", Password: " + password);


                //from PFD
                currentSocketData = new SocketData(clientSocket,usersBranch);
                allConnections.add(currentSocketData);
                //


                Customer customer;
                String mainChoice;
                while(true) {
                    mainChoice= inputFromClient.readLine();

                    if (mainChoice.equals("Exit")) {
                        break;
                   }

                    switch (mainChoice) {
                        //Employee Handler
                        case "createEmployee":
                            String employeeName = inputFromClient.readLine();
                            String employeePhoneNumber = inputFromClient.readLine();
                            String employeeBranch = inputFromClient.readLine();
                            String employeeRole = inputFromClient.readLine();

                            int flag = Employee.createEmployee(Integer.parseInt(employeePhoneNumber),employeeName,employeeBranch,employeeRole);
                            if(flag == 1){
                                outputToClient.println("Employee created successfully");
                                logger.info("Employee created successfully");
                            }
                            else{
                                outputToClient.println("Couldn't create employee");
                                logger.warning("Couldn't create employee");
                            }
                            break;
                        case "deleteEmployee":
                            String employeeToDeleteNumber = inputFromClient.readLine();
                            int deleteFlag = Employee.deleteEmployee(employeeToDeleteNumber);
                            if(deleteFlag == 1){
                                outputToClient.println("Employee deleted successfully");
                                logger.info("Employee deleted successfully");
                            }
                            else {
                                outputToClient.println("Couldn't delete employee");
                                logger.warning("Couldn't delete employee");
                            }
                            break;
                        case "displayEmployees":
                            outputToClient.println(Employee.displayAllEmployees());
                            break;
                        //Customer Handler
                        case "createCustomer":
                            String id = inputFromClient.readLine();
                            String fullName = inputFromClient.readLine();
                            String phoneNumber = inputFromClient.readLine();
                            String customerType = inputFromClient.readLine();
                            customer = new Customer(id, fullName, phoneNumber, customerType);
                            outputToClient.println(customer.stringBuilder.toString());
                            break;
                        case "searchCustomer":
                            customer = new Customer(inputFromClient.readLine());
                            outputToClient.println(customer.getId());
                            outputToClient.println(customer.getFullName());
                            outputToClient.println(customer.getPhoneNumber());
                            outputToClient.println(customer.getType());

                            break;
                        //Chat handler
                        case "Chat":
                            String chatRoomName = inputFromClient.readLine();

                            // Check if the chat room exists, and create it if it doesn't
                            ChatRoom chatRoom = chatRooms.get(chatRoomName);
                            if (chatRoom == null) {
                                chatRoom = new ChatRoom();
                                chatRooms.put(chatRoomName, chatRoom);

                                for (SocketData sd : allConnections) {
                                    if (sd.getBranch().equals(chatRoomName)) {
                                        sd.getOutputStream().println(" " + currentSocketData.getBranch() + " wants to chat with your branch, enter your branch name to chat in chat room");
                                    }
                                }
                            }

                            if(chatRoom.getChatSemaphore().availablePermits() == 0){
                                outputToClient.println("2 people already in chat");
                                logger.info("Client tried to join a full chat room: " + chatRoomName);
                            }else{
                                // Add the client to the chat room
                                chatRooms.get(chatRoomName).addClient(currentSocketData);
                                // Notify the client that they have entered the chat room
                                outputToClient.println("Entered chat room: " + chatRoomName);
                                logger.info("Client entered chat room: " + chatRoomName);
                            }


                            String message = null;
                            do {
                                message = inputFromClient.readLine(); // Read the chat message from user input
                                System.out.println(message);
                                // Broadcast the message to all clients in the chat room
                                chatRooms.get(chatRoomName).broadcast(currentSocketData,message);
                                logger.info("Message in chat room " + chatRoomName + ": " + message);

                                // Log the message to the chat log file
                                chatLogFile.write(chatRoomName + " - " + message);
                                chatLogFile.newLine();
                                chatLogFile.flush();

                            } while (!message.equals("/exit"));

                            // Remove the client from the chat room when they leave
                            chatRooms.get(chatRoomName).removeClient(currentSocketData);
                            currentChatRoom = null; // Reset the current chat room
                            isInChatSession = false;
                            logger.info("Client left chat room: " + chatRoomName);
                            break;

                        //Inventory Handler
                        case "displayInventory":
                            String branch = inputFromClient.readLine();
                            Inventory inventory = new Inventory(branch);
                            outputToClient.println(inventory.displayInventoryForBranch());
                            break;
                        case "addProductToInventory":
                            String productName = inputFromClient.readLine();
                            int productPrice = Integer.parseInt(inputFromClient.readLine());
                            int productQuantity = Integer.parseInt(inputFromClient.readLine());
                            Product productToAdd = new Product(productName, productPrice);

                            String returnMessage = Inventory.addProductToInventory(productToAdd, productQuantity, userAuth.getBranch());
                            outputToClient.println(returnMessage);
                            break;
                        //Selling handler
                        case "Selling":
                            String customerId = inputFromClient.readLine();
                            String itemName = inputFromClient.readLine();
                            int quantity = Integer.parseInt(inputFromClient.readLine());
                            String branch1 = userAuth.getBranch();
                            Inventory inventory1 = new Inventory(branch1);
                            if(quantity > inventory1.getItemQuantity(itemName)){
                                outputToClient.println("there are not enough of this item, try again");
                                logger.warning("Not enough quantity, or product not found");
                            }
                            else{ //IDK why we require to have 3 different classes for customer but this is the result!!
                                Customer customer1 = new Customer(customerId);
                                switch (customer1.getType()) {
                                    case "New" -> {
                                        NewCustomer newCustomer = new NewCustomer(customerId);
                                        newCustomer.purchase(itemName, quantity, username, branch1);
                                        outputToClient.println(newCustomer.getFullName() + " bought "+quantity+" "+ itemName +" and paid :"+newCustomer.calculateTotalAmount(itemName, quantity));
                                        logger.info("A New customer" + newCustomer.getFullName() + "bought item" + itemName);
                                    }
                                    case "Returned" -> {
                                        ReturningCustomer returningCustomer = new ReturningCustomer(customerId);
                                        returningCustomer.purchase(itemName, quantity, username, branch1);
                                        outputToClient.println(returningCustomer.getFullName() + " bought "+quantity+" "+ itemName +" and paid :"+returningCustomer.calculateTotalAmount(itemName, quantity));
                                        logger.info("A Returned customer" + returningCustomer.getFullName() + "bought item" + itemName);


                                    }
                                    case "VIP" -> {
                                        VipCustomer vipCustomer = new VipCustomer(customerId);
                                        vipCustomer.purchase(itemName, quantity, username, branch1);
                                        outputToClient.println(vipCustomer.getFullName() + " bought "+quantity+" "+ itemName +" and paid :"+vipCustomer.calculateTotalAmount(itemName, quantity));
                                        logger.info("A VIP customer" + vipCustomer.getFullName() + "bought item" + itemName);

                                    }
                                    default ->
                                            customer1.purchase(itemName, quantity, username, branch1); // regular customer
                                }
                                break;
                            }
                        // Report handler
                        case "reportByBranch":
                            String reportBranch = inputFromClient.readLine();
                            String reportForClientByBranch = Invoice.GetAllBranchInvoices(reportBranch);
                            outputToClient.println(reportForClientByBranch);
                            break;
                        case "reportByProduct":
                            String reportProduct = inputFromClient.readLine();
                            String reportForClientByProduct = Invoice.GetInvoicesByProduct(reportProduct);
                            outputToClient.println(reportForClientByProduct);
                            break;
                        case "getReportFile":
                            try{
                                Invoice.createInvoiceFile();
                                outputToClient.println("File created and opened successfully");
                            }catch (Exception e){
                                outputToClient.println(e.getMessage());
                            }
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + mainChoice);
                    }
                }

            }
            catch (IOException  | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
