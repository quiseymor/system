import java.io.*;
import java.net.*;

public class server3 {
    private static final int PORT = 12345; // Номер порта для сервера

    public static void main(String[] args) {
        System.out.println("Запуск сервера...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен. Начало прослушивания порта " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клиент подключен: " + clientSocket.getInetAddress());

                // Обработка клиента в отдельном потоке
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Ошибка в сервере: " + e.getMessage());
        }
        System.out.println("Сервер остановлен.");
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("factorial")) {
                    int number = Integer.parseInt(inputLine.substring(10));
                    long factorial = calculateFactorial(number);
                    out.println("Факториал: " + factorial);
                } else if (inputLine.startsWith("sumOfNumbers")) {
                    long summa = sumOfNumbers(inputLine);
                    out.println("Сумма чисел в строке: " + summa);
                } else {
                    out.println("Неизвестная команда " + inputLine);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при обработке клиента: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Отключение клиента.");
            } catch (IOException e) {
                System.err.println("Ошибка при отключении клиента: " + e.getMessage());
            }
        }
    }

    private static long calculateFactorial(int n) {
        if (n == 0)
            return 1;
        else
            return n * calculateFactorial(n - 1);
    }

    private static long sumOfNumbers(String str) {
        int sum = 0;
        StringBuilder number = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                number.append(c); // Собираем цифры
            } else {
                if (!number.isEmpty()) {
                    sum += Integer.parseInt(number.toString()); // Преобразуем и добавляем
                    number.setLength(0); // Сбрасываем для следующего числа
                }
            }
        }
        // В конце строки может быть число
        if (!number.isEmpty()) {
            sum += Integer.parseInt(number.toString());
        }
        return sum;
    }
}

