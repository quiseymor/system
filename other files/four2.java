
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class four2 {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: not enough params. Type \"help\" for details");
            return;
        }
        String proc_command1 = "C:\\Users\\admin\\.jdks\\openjdk-23\\bin\\java.exe";
        String proc_command2 = "-classpath";
        String proc_command3 = "C:\\Users\\admin\\Downloads\\Михайлова\\two-libs\\commons-io-2.15.1\\commons-io-2.15.1.jar;C:\\Users\\admin\\Downloads\\Михайлова\\two-libs\\commons-io-2.15.1\\commons-io-2.15.1-tests.jar;C:\\Users\\admin\\Downloads\\Михайлова\\two-libs\\commons-io-2.15.1\\commons-io-2.15.1-javadoc.jar;C:\\Users\\admin\\Downloads\\Михайлова\\two-libs\\commons-io-2.15.1\\commons-io-2.15.1-sources.jar;C:\\Users\\admin\\Downloads\\Михайлова\\two-libs\\commons-io-2.15.1\\commons-io-2.15.1-test-sources.jar;C:\\Users\\admin\\Downloads\\Михайлова\\two-libs\\commons-lang3-3.14.0\\commons-lang3-3.14.0.jar;C:\\Users\\admin\\Downloads\\Михайлова\\two-libs\\commons-lang3-3.14.0\\commons-lang3-3.14.0-tests.jar;C:\\Users\\admin\\Downloads\\Михайлова\\two-libs\\commons-lang3-3.14.0\\commons-lang3-3.14.0-javadoc.jar;C:\\Users\\admin\\Downloads\\Михайлова\\two-libs\\commons-lang3-3.14.0\\commons-lang3-3.14.0-sources.jar;C:\\Users\\admin\\Downloads\\Михайлова\\two-libs\\commons-lang3-3.14.0\\commons-lang3-3.14.0-test-sources.jar;C:\\Users\\admin\\Downloads\\Михайлова\\jna\\jna\\jna-5.14.0.jar;C:\\Users\\admin\\Downloads\\Михайлова\\jna\\jna\\jna-platform-jpms-5.14.0.jar";
        String proc_command4 = "C:\\Users\\admin\\IdeaProjects\\untitled\\src\\four2";

        for (int i = 0; i < args.length; i++) {
            String command = args[i];
            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "copy":
                    if (args.length < 3) {
                        System.out.println("Error: not enough params. Type \"help\" for details");
                        break;
                    } else if (args.length == 3) {
                        String sourcePath = args[1];
                        String destinationPath = args[2];
                        copyFile(sourcePath, destinationPath);
                        //System.out.println("Process stopped");
                        break;
                    } else {
                        try {
                            String sourcePath = args[1];
                            String destinationPath = args[2];

                            ProcessBuilder processBuilder = new ProcessBuilder(proc_command1, proc_command2,
                                    proc_command3, proc_command4,
                                    command, sourcePath, destinationPath);
                            processBuilder.redirectErrorStream(true);
                            Process process = processBuilder.start();

                            int exitCode = process.waitFor();
                            System.out.println("Process exited with code: " + exitCode);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                System.out.println(line);
                            }

                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                case "create":
                    if (args.length < 2) {
                        System.out.println("Error: not enough params. Type \"help\" for details");
                        return;
                    }
                    String directoryName = args[1];
                    createDirectory(directoryName);
                    break;
                case "count":
                    if (args.length < 2) {
                        System.out.println("Error: not enough params. Type \"help\" for details");
                        return;
                    }
                    String directoryPath = args[1];
                    int nestedDirectoriesCount = countNestedDirectories(directoryPath);
                    System.out.println("Number of nested directories: " + nestedDirectoriesCount);
                    break;
                case "size":
                    if (args.length < 2) {
                        System.out.println("Error: not enough params. Type \"help\" for details");
                        return;
                    }
                    String filePath = args[1];
                    long fileSize = getFileSize(filePath);
                    System.out.println("File size: " + fileSize + " bytes");
                    break;
                case "compare":
                    if (args.length < 3) {
                        System.out.println("Error: not enough params. Type \"help\" for details");
                        return;
                    }
                    String directory1Path = args[1];
                    String directory2Path = args[2];
                    boolean areEquivalent = compareDirectories(directory1Path, directory2Path);
                    if (areEquivalent) {
                        System.out.println("Directories are equivalent");
                    } else {
                        System.out.println("Directories are not equivalent");
                    }
                    break;
                case "search":
                    if (args.length < 2) {
                        System.out.println("Error: not enough params. Type \"help\" for details");
                        return;
                    }
                    String searchPattern = args[1];
                    List<String> matchingDirectories = searchDirectories(searchPattern);
                    System.out.println("Matching directories: " + matchingDirectories);
                    break;
                default:
                    System.out.println("Unknown command. Type \"help\" for details");
            }
        }

    }

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("copy <source> <destination> - Copy the specified file to the given location");
        System.out.println("create <directory> - Create a directory with the specified name");
        System.out.println("count <directory> - Count the number of nested directories in the specified directory");
        System.out.println("size <file> - Get the size of the specified file");
        System.out.println("compare <directory1> <directory2> - Compare two directories for equivalence");
        System.out.println("search <pattern> - Search for directories with the specified name (wildcards allowed)");
    }

    private static void copyFile(String sourcePath, String destinationPath) {
        try {
            Path source = Paths.get(sourcePath);
            Path destination = Paths.get(destinationPath);
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void createDirectory(String directoryName) {
        File directory = new File(directoryName);
        if (directory.mkdir()) {
            System.out.println("Directory created successfully");
        } else {
            System.out.println("Error: Failed to create directory");
        }
    }

    private static int countNestedDirectories(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.out.println("Error: Not a directory");
            return 0;
        }
        int count = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    count++;
                    count += countNestedDirectories(file.getAbsolutePath());
                }
            }
        }
        return count;
    }

    private static long getFileSize(String filePath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            System.out.println("Error: Not a file");
            return 0;
        }
        return file.length();
    }

    private static boolean compareDirectories(String directory1Path, String directory2Path) {
        File directory1 = new File(directory1Path);
        File directory2 = new File(directory2Path);
        if (!directory1.isDirectory() || !directory2.isDirectory()) {
            System.out.println("Error: Not a directory");
            return false;
        }
        File[] files1 = directory1.listFiles();
        File[] files2 = directory2.listFiles();
        if (files1 == null || files2 == null) {
            return false;
        }
        if (files1.length != files2.length) {
            return false;
        }
        Arrays.sort(files1);
        Arrays.sort(files2);
        for (int i = 0; i < files1.length; i++) {
            File file1 = files1[i];
            File file2 = files2[i];
            if (!file1.getName().equals(file2.getName())) {
                return false;
            }
            if (file1.isDirectory() && file2.isDirectory()) {
                if (!compareDirectories(file1.getAbsolutePath(), file2.getAbsolutePath())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static List<String> searchDirectories(String searchPattern) {
        File currentDirectory = new File(".");
        File[] directories = currentDirectory.listFiles((dir, name) -> name.matches(searchPattern));
        if (directories == null) {
            return List.of();
        }
        return Arrays.asList(directories).stream()
                .filter(File::isDirectory)
                .map(File::getName)
                .toList();
    }


    public static void maini (String[] args) {
        if (args.length < 2) {
            System.out.println("Error: not enough params. Type \"help\" for details");
            return;
        }

        String pattern = args[0]; // Шаблон для поиска
        List<String> files = new ArrayList<>();

        for (int i = 1; i < args.length; i++) {
            files.add(args[i]);
        }

        // Запуск процессов для каждого файла
        List<Process> processes = new ArrayList<>();
        for (String fileName : files) {
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println("Error: file not found: " + fileName);
                continue;
            }
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("grep", pattern, fileName);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                processes.add(process);
            } catch (IOException e) {
                System.out.println("Error executing command for file: " + fileName);
            }
        }

        for (Process process : processes) {
            try {
                int exitCode = process.waitFor();
                System.out.println("Process exited with code: " + exitCode);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (InterruptedException e) {
                System.out.println("Process was interrupted.");
            } catch (IOException e) {
                System.out.println("Error reading process output.");
            }
        }
    }
}