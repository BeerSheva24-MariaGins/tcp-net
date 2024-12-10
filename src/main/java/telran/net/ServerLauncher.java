package telran.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerLauncher {
    public static void main(String[] args) {
        Protocol protocol = new MyProtocol(); // Используем реализацию MyProtocol
        TcpServer server = new TcpServer(protocol, 12345); // Укажите порт, на котором будет работать сервер
        Thread serverThread = new Thread(server);
        serverThread.start();

        // Проверка ввода с консоли для команды shutdown
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String command;
            while ((command = reader.readLine()) != null) {
                if (command.equalsIgnoreCase("shutdown")) {
                    server.shutdown();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
