import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class java2 {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: not enough params. Type \"help\" for details");
            return;
        }
        processCommand(args);
        /*ExecutorService executorService = Executors.newFixedThreadPool(args.length);

       for (String arg : args) {
            executorService.submit(() -> processCommand(arg));
        }

        executorService.shutdown();*/
    }

    private static void processCommand(String[] parts) {
        //String[] parts = command.split(" "); // Разделяем команду на части
        switch (parts[0]) {
            case "help":
                printHelp();
                break;
            case "copy":
                if (parts.length < 3) {
                    System.out.println("Error: not enough params for copy. Type \"help\" for details");
                    return;
                }
                copyFile(parts[1], parts[2]);
                break;
            case "create":
                if (parts.length < 2) {
                    System.out.println("Error: not enough params for create. Type \"help\" for details");
                    return;
                }
                createDirectory(parts[1]);
                break;
            case "count":
                if (parts.length < 2) {
                    System.out.println("Error: not enough params for count. Type \"help\" for details");
                    return;
                }
                int nestedDirectoriesCount = countNestedDirectories(parts[1]);
                System.out.println("Number of nested directories in " + parts[1] + ": " + nestedDirectoriesCount);
                break;
            case "size":
                if (parts.length < 2) {
                    System.out.println("Error: not enough params for size. Type \"help\" for details");
                    return;
                }
                long fileSize = getFileSize(parts[1]);
                System.out.println("File size of " + parts[1] + ": " + fileSize + " bytes");
                break;
            case "compare":
                if (parts.length < 3) {
                    System.out.println("Error: not enough params for compare. Type \"help\" for details");
                    return;
                }
                boolean areEquivalent = compareDirectories(parts[1], parts[2]);
                System.out.println("Directories " + parts[1] + " and " + parts[2] + " are " + (areEquivalent ? "equivalent" : "not equivalent"));
                break;
            case "search":
                if (parts.length < 2) {
                    System.out.println("Error: not enough params for search. Type \"help\" for details");
                    return;
                }
                final List<String> matchingDirectories = searchDirectories(parts[1]);
                System.out.println("Matching directories for " + parts[1] + ": " + matchingDirectories);
                break;
            case "filesearch":
                if (parts.length < 2) {
                    System.out.println("Error: not enough params for filesearch. Type \"help\" for details");
                    return;
                }
                List<String> matchedFiles = searchFiles(parts[1]);
                System.out.println("Matching files for " + parts[1] + ": " + matchedFiles);
                break;
            default:
                System.out.println("Unknown command: " + parts[0] + ". Type \"help\" for details");
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
        System.out.println("filesearch <pattern> - Search for files containing the specified pattern");
    }

    private static void copyFile(String sourcePath, String destinationPath) {
        try {
            Path source = Paths.get(sourcePath);
            Path destination = Paths.get(destinationPath);
            Files.copy(source, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
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
            System.out.println("Error: One or both paths are not directories");
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
        Arrays.sort(files1, (f1, f2) -> f1.getName().compareTo(f2.getName()));
        Arrays.sort(files2, (f1, f2) -> f1.getName().compareTo(f2.getName()));

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
        return Arrays.stream(directories)
                .filter(File::isDirectory)
                .map(File::getName)
                .toList();
    }

    private static List<String> searchFiles(String pattern) {
        File currentDirectory = new File(".");
        return searchFilesRecursive(currentDirectory, pattern);
    }

    private static List<String> searchFilesRecursive(File directory, String pattern) {
        return Arrays.stream(directory.listFiles())
                .flatMap(file -> {
                    if (file.isDirectory()) {

                        return searchFilesRecursive(file, pattern).stream();
                    } else if (file.getName().contains(pattern)) {
                       
                        return Stream.of(file.getAbsolutePath());

                    }
                    return Stream.empty();
                })
                .toList();
    }
}

