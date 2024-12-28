import java.io.*;
import java.net.*;

public class client3 {
    private static final String SERVER_ADDRESS = "localhost"; // Адрес сервера
    private static final int SERVER_PORT = 12345; // Номер порта сервера

    public static void main(String[] args) {
        System.out.println("Соединение с сервером " + SERVER_ADDRESS + ":" + SERVER_PORT + "...");
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Соединение установлено.");
            System.out.println("Доступные команды \"factorial\" и \"sumOfNumbers\"");

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("Отправка данных серверу: " + userInput);

                // Получение ответа от сервера
                String response = in.readLine();
                System.out.println("Прием данных от сервера: " + response);
            }
        } catch (IOException e) {
            System.err.println("Ошибка в клиенте: " + e.getMessage());
        } finally {
            System.out.println("Разрыв соединения с сервером.");
        }
    }
}

