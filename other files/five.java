import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class five {
    public static void main(String[] args) {
        if (args.length == 0) {
            error(1, args);
            return;
        }
        if (Objects.equals(args[0], "help")) {
            help();
            return;
        }
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case ("-copy"):
                    if (i <= args.length-2) {
                        if ("-".indexOf(args[i+1].charAt(0)) != 0 && "-".indexOf(args[i+2].charAt(0)) != 0) {
                            int finalI = i;
                            Runnable task = () -> {
                                System.out.println("Start stream: copy");
                                copy(args[finalI +1], args[finalI +2]);
                            };
                            Thread thread = new Thread(task);
                            thread.start();
                            i += 2;
                            break;
                        }
                    }
                    error(1, args);
                    break;
                case ("-make"):
                    if (i <= args.length-1) {
                        if ("-".indexOf(args[i+1].charAt(0)) != 0) {
                            int finalI = i;
                            Runnable task = () -> {
                                System.out.println("Start stream: make");
                                make(args[finalI+1]);
                            };
                            Thread thread = new Thread(task);
                            thread.start();
                            i += 1;
                            break;
                        }
                    }
                    error(1, args);
                    break;
                case ("-count"):
                    if (i <= args.length-1) {
                        if ("-".indexOf(args[i+1].charAt(0)) != 0) {
                            int finalI = i;
                            Runnable task = () -> {
                                System.out.println("Start stream: count");
                                System.out.println("Number of including directories: " + countDirectories(args[finalI+1]));
                            };
                            Thread thread = new Thread(task);
                            thread.start();
                            i += 1;
                            break;
                        }
                    }
                    error(1, args);
                    break;
                case ("-size"):
                    if (i <= args.length-1) {
                        if ("-".indexOf(args[i+1].charAt(0)) != 0) {
                            int finalI = i;
                            Runnable task = () -> {
                                System.out.println("Start stream: size");
                                long fileSize = getFileSize(args[finalI+1]);
                                System.out.println("File size: " + fileSize + " bytes");
                            };
                            Thread thread = new Thread(task);
                            thread.start();
                            i += 1;
                            break;
                        }
                    }
                    error(1, args);
                    break;
                case ("-compare"):
                    if (i <= args.length-2) {
                        if ("-".indexOf(args[i+1].charAt(0)) != 0 && "-".indexOf(args[i+2].charAt(0)) != 0) {
                            int finalI = i;
                            Runnable task = () -> {
                                System.out.println("Start stream: compare");
                                if (compareDirectories(args[finalI+1], args[finalI+2])) {
                                    System.out.println("Directories are equivalent");
                                } else {
                                    System.out.println("Directories are not equivalent");
                                }
                            };
                            Thread thread = new Thread(task);
                            thread.start();
                            i += 2;
                            break;
                        }
                    }
                    error(1, args);
                    break;
                case ("-search"):
                    if (i <= args.length-1) {
                        if ("-".indexOf(args[i+1].charAt(0)) != 0) {
                            int finalI = i;
                            Runnable task = () -> {
                                System.out.println("Start stream: search");
                                List<File> matchingDirectories = searchDirectories(args[finalI+1]);
                                System.out.println("Matching directories: " + matchingDirectories);
                            };
                            Thread thread = new Thread(task);
                            thread.start();
                            i += 1;
                            break;
                        }
                    }
                    error(1, args);
                    break;
                case ("-searchf"):
                    if (i <= args.length-2) {
                        if ("-".indexOf(args[i+1].charAt(0)) != 0 && "-".indexOf(args[i+2].charAt(0)) != 0) {
                            int finalI = i;
                            Runnable task = () -> {
                                System.out.println("Start stream: searchf");
                                Scanner scanner=new Scanner(args[finalI+1]);
                                List<String> list=new ArrayList<>();
                                while(scanner.hasNextLine()){
                                    list.add(scanner.nextLine());
                                }
                                if(list.contains(args[finalI+2])){
                                    // found.
                                } else {
                                    // not found
                                }
                            };
                            Thread thread = new Thread(task);
                            thread.start();
                            i += 2;
                            break;
                        }
                    }
                    error(1, args);
                    break;
                default:
                    error(2, args);
            }
        }
    }
    private static void error(int ner, String[] inp) {
        String input = String.join(" ", inp);
        switch (ner) {
            case 1:
                System.out.printf("Error: not enough params. Type \"help\" for details.\n(input: \"%s\")\n", input);
                break;
            case 2:
                System.out.printf("Unknown command. Type \"help\" for details.\n(input: \"%s\")\n", input);
                break;
            default:
                error();
        }
    }
    private static void error() {
        System.out.println("Error: unknown error\n");
    }
    private static void help() {
        System.out.print("Available commands: help, -copy, -make, -count, -size, -compare, -search\n");
    }
    private static void copy(String sourcePath, String destinationPath) {
        try {
            Path source = Paths.get(sourcePath);
            Path destination = Paths.get(destinationPath);
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void make(String directoryName) {
        try {
            Path directory = Files.createDirectories(Path.of(directoryName));
            System.out.println(directory);
        } catch (Exception e) {
            error();
        }
    }
    private static int countDirectories(String directoryPath) {
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
                    count += countDirectories(file.getAbsolutePath());
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
                    // рекурсия чтобы проверить каждый каталог (поэтому абсолютный путь)
                    // File.listFiles() возвращает массив всех файлов и каталогов только на заданном пути
                    return false;
                }
            }
        }
        return true;
    }
    private static List<File> searchDirectories(String searchPattern) {
        File currentDirectory = new File(".");
        // лямбда в роли фильтра
        File[] directories = currentDirectory.listFiles((dir, name) -> name.matches(searchPattern));
        if (directories == null) {
            return List.of();
        }
        return Arrays.asList(directories);
    }
}

