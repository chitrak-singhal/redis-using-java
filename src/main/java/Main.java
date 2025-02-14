import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// PING : It is used by Redis to test a connection
// PONG : Reply to an empty PING. Sent as "+PONG\r\n". This is the RESP format used by Redis
// ECHO "message" : The message is sent back as "$(size)\r\nmessage\r\n"
// SET key value : Used to set value for the key in the db. Succesful response is "+OK\r\n", else "$-1\r\n" (null in RESP)
// GET key : Used to access the value for key. If it doesn't exist then null


public class Main {
    public static void main(String[] args) throws IOException {
        ExecutorService scheduler = Executors.newCachedThreadPool();
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress("0.0.0.0", 6379)); //LocalHost : Port 6379
        //If this port is busy then you have to close it as this is the native port of Redis.

        System.out.println("App is listening on 0.0.0.0:6379");
        
        Redis db = new Redis(); //Instantiate the class
        
        while (true) { //Keep the server running
            Socket client = server.accept(); //The Redis db client begins listening to server
            scheduler.execute(() -> {
                try (client) {
                    db.handleConnection(client); //Handle input and output streams
                } catch (Exception e) {
                    e.printStackTrace(); //Error handling
                }
            });
        }
    }
}