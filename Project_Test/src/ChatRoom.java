import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ChatRoom {
    private List<SocketData> clients = new ArrayList<>();
    private Semaphore chatSemaphore = new Semaphore(2); // Initialize with 2 permits

    public void addClient(SocketData client) throws InterruptedException {
        chatSemaphore.acquire(); // Acquire a permit (blocks if all permits are taken)
        clients.add(client);
    }

    public void removeClient(SocketData client) {
        clients.remove(client);
        chatSemaphore.release(); // Release a permit
    }

    public void broadcast(SocketData sender, String message) {
        for (SocketData client : clients) {
            if (client != sender) {
                try {
                    PrintWriter clientOutput = new PrintWriter(client.getSocket().getOutputStream(), true);
                    clientOutput.println(sender.getBranch() + ": " + message);
                } catch (IOException e) {
                    // Handle the exception (e.g., remove the client if sending fails)
                    e.printStackTrace();
                }
            }
        }

    }

    public Semaphore getChatSemaphore() {
        return chatSemaphore;
    }
}