import java.io.*;
import java.net.*;
import java.util.Scanner;

//upload file1.txt server/
//download server/file2.txt ./

public class Client {
    private static final String SERVER_HOST = "localhost"; // Адрес сервера
    private static final int SERVER_PORT = 12345; // Порт сервера

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Подключено к файловому серверу. Введите команды:");

            String command;
            while (true) {
                System.out.print("> ");
                command = scanner.nextLine();

                if (command.equalsIgnoreCase("shutdown")) {
                    out.println(command);
                    System.out.println(in.readLine());
                    break;
                }

                String[] parts = command.split(" ", 3);
                if (parts[0].equalsIgnoreCase("upload")) {
                    uploadFile(parts[1], parts[2], out, socket);
                    System.out.println(in.readLine());
                } else if (parts[0].equalsIgnoreCase("download")) {
                    downloadFile(parts[1], parts[2], out, socket);
                    System.out.println(in.readLine());
                } else {
                    out.println(command);
                    System.out.println(in.readLine());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void uploadFile(String clientPath, String serverPath, PrintWriter out, Socket socket) {
        File file = new File(clientPath);
        if (!file.exists()) {
            System.out.println("Файл не найден: " + clientPath);
            return;
        }

        out.println("upload " + clientPath + " " + serverPath);
        try (FileInputStream fis = new FileInputStream(file)) {

            new DataOutputStream(socket.getOutputStream()).writeLong(file.length());

            OutputStream os = socket.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            System.out.println("Файл загружен на сервер: " + serverPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка загрузки файла.");
        }
    }

    private static void downloadFile(String serverPath, String clientPath, PrintWriter out, Socket socket) {
        out.println("download " + serverPath + " " + clientPath);
        File file = new File(clientPath + new File(serverPath).getName());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {

            long fileLen = new DataInputStream(socket.getInputStream()).readLong();
            long nowLen = 0;

            InputStream is = socket.getInputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while (fileLen > nowLen) {
                bytesRead = is.read(buffer);
                nowLen += bytesRead;
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("Файл скачан на клиент: " + clientPath + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка скачивания файла.");
        }
    }
}
