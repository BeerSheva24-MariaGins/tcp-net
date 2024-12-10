package telran.net;
import java.net.*;
import java.io.*;

public class TcpClientServerSession implements Runnable {
    Protocol protocol;
    Socket socket;
    private boolean running = true; 
    private TcpServer server; 
    private int requestCount = 0; 
    private long startTime = System.currentTimeMillis(); 
    private static final int MAX_REQUESTS_PER_SECOND = 10; 
    private static final int MAX_WRONG_RESPONSES = 5; 
    private int wrongResponseCount = 0; 

    public TcpClientServerSession(Protocol protocol, Socket socket, TcpServer server) {
        this.protocol = protocol;
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintStream writer = new PrintStream(socket.getOutputStream())) {
            socket.setSoTimeout(1000); 
            String request = null;
            while (running && (request = reader.readLine()) != null) {
                if (!server.isRunning()) {
                    break; 
                }
                
                requestCount++;
                if (System.currentTimeMillis() - startTime >= 1000) {
                    if (requestCount > MAX_REQUESTS_PER_SECOND) {
                        System.out.println("Too many requests per second. Closing session.");
                        break;
                    }
                    requestCount = 0;
                    startTime = System.currentTimeMillis();
                }
                String response = protocol.getResponseWithJSON(request);
                writer.println(response);

                
                if (response.contains("\"responseCode\":\"WRONG_TYPE\"") || response.contains("\"responseCode\":\"WRONG_DATA\"")) {
                    wrongResponseCount++;
                    if (wrongResponseCount >= MAX_WRONG_RESPONSES) {
                        System.out.println("Too many wrong responses. Closing session.");
                        break;
                    }
                }
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Session timeout: " + e.getMessage());
            if (!server.isRunning()) {
                running = false;
            }
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}
