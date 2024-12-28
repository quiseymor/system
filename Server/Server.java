import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 12345; // Номер порта для сервера

    public static void main(String[] args) {
        System.out.println("Запуск файлового сервера на порту " + PORT);
        ExecutorService executor = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клиент подключен: " + clientSocket.getInetAddress());
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String command;
            while ((command = in.readLine()) != null) {
                System.out.println("Получена команда: " + command);
                String response = processCommand(command);
                out.println(response);
            }
            System.out.println("Завершён ввод команд");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String processCommand(String command) {
        String[] parts = command.split(" ", 3);
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "upload":
                return handleUpload(parts[1], parts[2]);
            case "download":
                return handleDownload(parts[1], parts[2]);
            case "dir":
                return handleDir(parts[1]);
            case "mkdir":
                return handleMkdir(parts[1]);
            case "rmdir":
                return handleRmdir(parts[1]);
            case "rm":
                return handleRm(parts[1]);
            case "shutdown":
                return "Сервер остановлен. Прерывание соединения.";
            default:
                return "Неизвестная команда.";
        }
    }

    private String handleUpload(String clientPath, String serverPath) {
        File file = new File(serverPath + new File(clientPath).getName());
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
            return "Файл загружен: " + serverPath;
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка загрузки файла.";
        }
    }

    private String handleDownload(String serverPath, String clientPath) {
        File file = new File(serverPath);
        if (!file.exists()) {
            return "Файл не найден: " + serverPath;
        }
        try (FileInputStream fis = new FileInputStream(file)) {

            new DataOutputStream(socket.getOutputStream()).writeLong(file.length());

            OutputStream os = socket.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            return "Файл скачан: " + serverPath;
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка скачивания файла.";
        }
    }

    private String handleDir(String path) {
        File dir = new File(path);
        StringBuilder response = new StringBuilder();
        if (dir.exists() && dir.isDirectory()) {
            for (String fileName : dir.list()) {
                response.append(fileName).append("; ");
            }
            return response.toString();
        }
        return "Директория не найдена.";
    }

    private String handleMkdir(String path) {
        File dir = new File(path);
        if (dir.mkdirs()) {
            return "Директория создана: " + path;
        }
        return "Ошибка создания директории или она уже существует.";
    }

    private String handleRmdir(String path) {
        File dir = new File(path);
        if (dir.delete()) {
            return "Директория удалена: " + path;
        }
        return "Ошибка удаления директории или она не пуста.";
    }

    private String handleRm(String path) {
        File file = new File(path);
        if (file.delete()) {
            return "Файл удален: " + path;
        }
        return "Ошибка удаления файла или файл не найден.";
    }
}
