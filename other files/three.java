import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.SystemUtils.*;

public class three {
    public static void main(String[] args) throws IOException {
        second(args);
        first(args);
    }

    public static void second(String[] args) {
        if (args.length == 0) {
            error(1);
            return;
        }
        switch (args[0]) {
            case ("help"):
                help();
                break;
            case ("copy"):
                if (args.length < 3) {
                    error(1);
                    break;
                }
                copy(args[1], args[2]);
                break;
            case ("make"):
                if (args.length < 2) {
                    error(1);
                    break;
                }
                make(args[1]);
                break;
            case ("count"):
                if (args.length < 2) {
                    error(1);
                    break;
                }
                System.out.println("Number of including directories: " + countDirectories(args[1]));
                break;
            case ("size"):
                if (args.length < 2) {
                    error(1);
                    break;
                }
                long fileSize = getFileSize(args[1]);
                System.out.println("File size: " + fileSize + " bytes");
                break;
            case ("compare"):
                if (args.length < 3) {
                    error(1);
                    break;
                }
                if (compareDirectories(args[1], args[2])) {
                    System.out.println("Directories are equivalent");
                } else {
                    System.out.println("Directories are not equivalent");
                }
                break;
            case ("search"):
                if (args.length < 2) {
                    error(1);
                    break;
                }
                List<File> matchingDirectories = searchDirectories(args[1]);
                System.out.println("Matching directories: " + matchingDirectories);
                break;
            default:
                error(2);
        }
    }
    public static void first(String[] args) throws IOException {
        boolean goodInputFlag = false;
        boolean envParamFlag = false;
        boolean cpuParamFlag = false;
        for (int i = 0; i < args.length; i++) {
            if (envParamFlag) {
                if ("-".indexOf(args[i].charAt(0)) == 0) {
                    Map<String, String> env = System.getenv();
                    for (String envName : env.keySet()) {
                        System.out.printf("%s = %s\n", envName, env.get(envName));
                    }
                } else {
                    System.out.printf("%s = %s\n", args[i], System.getenv(args[i]));
                }
                envParamFlag = false;
            } else if (cpuParamFlag) {
                if ("-".indexOf(args[i].charAt(0)) == -1) {
                    String param = args[i];
                    try {
                        int delayMinut = Integer.valueOf(param);
                        if (delayMinut < 1 || delayMinut > 15) {
                            System.out.println("Parameter must be in [5, 15] interval\n");
                        } else {
                            System.out.printf("Cpu working for %s minutes", delayMinut);
                            long delay = delayMinut * 60000L;
                            long peroid = 6000L;
                            Timer timer = new Timer("Timer");
                            Timer timer2 = new Timer("TimerCancle");
                            TimerTask task = new TimerTask() {
                                public void run() {
                                    Runtime runtime = Runtime.getRuntime();
                                    System.out.printf("\n\"-cpu\" available processors: %s\n", runtime.availableProcessors());
                                }
                            };
                            TimerTask taskCancel = new TimerTask() {
                                public void run() {
                                    task.cancel();
                                    timer.cancel();
                                    timer2.cancel();
                                }
                            };
                            timer.scheduleAtFixedRate(task, 0, peroid);
                            timer2.schedule(taskCancel, delay);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid integer input\n");
                    }
                } else {
                    System.out.printf("Error: not set parameter \"{p}\"\n");
                }
                cpuParamFlag = false;
            }
            switch (args[i]) {
                case ("help"):
                    if (i == 0) {
                        goodInputFlag = true;
                        System.out.printf("Available commands: -osname, -osver, -osarch, -timez, -usname," +
                                " -lang, -ap, -spage, -tm, -fm, -ram, -ftm, -env, -env {p}, -cpu {p}\n");
                    }
                    break;
                case ("-osname")://выводит имя операционной системы
                    goodInputFlag = true;
                    osname();
                    break;
                case ("-osver")://выводит версию операционной системы
                    goodInputFlag = true;
                    osver();
                    break;
                case ("-osarch")://: выводит архитектуру операционной системы
                    goodInputFlag = true;
                    osarch();
                    break;
                case ("-timez")://выводит временную зону
                    goodInputFlag = true;
                    timez();
                    break;
                case ("-usname"):// показывает имя пользователя и имя хоста.
                    goodInputFlag = true;
                    usname();
                    break;
                case ("-lang")://показывает текущий язык системы
                    goodInputFlag = true;
                    lang();
                    break;
                case ("-ap")://показывает количество доступных процессоров
                    goodInputFlag = true;
                    ap();
                    break;
                case ("-spage")://получения информации о файловой странице
                    goodInputFlag = true;
                    spage();
                    break;
                case ("-tm")://показывает общий объем физической памяти системы
                    goodInputFlag = true;
                    tm();
                    break;
                case ("-fm"):// показывает объем свободной физической памяти
                    goodInputFlag = true;
                    fm();
                    break;
                case ("-ram"):// отображает информацию о общей и свободной оперативной памяти.
                    goodInputFlag = true;
                    ram();
                    break;
                case ("-ftm"):// выводит общую и свободную память на диске
                    goodInputFlag = true;
                    ftm();
                    break;
                case ("-env"):
                    goodInputFlag = true;
                    if (i == args.length - 1) {
                        Map<String, String> env = System.getenv();
                        for (String envName : env.keySet()) {
                            System.out.printf("%s = %s\n", envName, env.get(envName));
                        }
                    } else {
                        envParamFlag = true;
                    }
                    break;
                case ("-cpu"):
                    goodInputFlag = true;
                    if (i == args.length - 1) {
                        System.out.printf("Error: not set parameter \"{p}\"\n");
                    } else {
                        cpuParamFlag = true;
                    }
                    break;
                default:
                    break;
            }
        }
        if (goodInputFlag == false) {
            System.out.printf("Error: not enough params. Type \"help\" for details\n");
        }
    }

    private static void error(int ner) {
        switch (ner) {
            case 1:
                System.out.println("Error: not enough params. Type \"help\" for details\n");
                break;
            case 2:
                System.out.println("Unknown command. Type \"help\" for details");
                break;
            default:
                error();
        }
    }

    private static void error() {
        System.out.println("Error: unknown error\n");
    }

    private static void help() {
        System.out.print("Available commands: help, copy, make, count, size, compare, search\n");
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
    private static void osname() {
        System.out.printf("OS name: %s\n", OS_NAME);         // OS name
    };
    private static void osver() {
        System.out.printf("OS version: %s\n", OS_VERSION);   // OS version
    };
    private static void osarch() {
        System.out.printf("OS architecture: %s\n", OS_ARCH); // OS architecture
    };
    private static void timez() {
        SimpleDateFormat formatTimeZone = new SimpleDateFormat("zzz");
        Date date = new Date();
        System.out.printf("Time zone: %s\n", formatTimeZone.format(date));
    };
    private static void usname() {
        System.out.printf("Username: %s\n", getUserName()); // Username
        System.out.printf("Hostname: %s\n", getHostName()); // Hostname
    };
    private static void lang() {
        String lang = Locale.getDefault().getLanguage();
        System.out.printf("System language: %s\n", lang);
    };
    private static void ap() {
        Runtime runtime = Runtime.getRuntime();
        System.out.printf("available processors: %s\n", runtime.availableProcessors());
    };
    private static void spage() throws IOException {
        Process exexe = Runtime.getRuntime().exec("wmic pagefile list /format:list");
        try (InputStream is = exexe.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.copy(is, baos);
            System.out.print(baos);
        }
    };
    private static void tm() {
        File file = new File("\\");
        long totalPhysicalMemorySize = file.getTotalSpace();
        System.out.printf("total %s mb \":\\\"\n", totalPhysicalMemorySize / 1024 / 1024);
    };
    private static void fm() {
        File file = new File("\\");
        long freePhysicalMemorySize = file.getFreeSpace();
        System.out.printf("free %s mb \":\\\"\n", freePhysicalMemorySize / 1024 / 1024);
    };
    private static void ram() {
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long physicalMemorySize = os.getTotalPhysicalMemorySize();
        long freePhysicalMemory = os.getFreePhysicalMemorySize();
        System.out.printf("total %s mb RAM\n", physicalMemorySize / 1024 / 1024);
        System.out.printf("free %s mb RAM\n", freePhysicalMemory / 1024 / 1024);
    };
    private static void ftm() {
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long freePhysicalMemory = os.getFreePhysicalMemorySize();
        File file = new File("\\");
        long freePhysicalMemorySize = file.getFreeSpace();
        System.out.printf("all free %s mb: \n", (freePhysicalMemorySize + freePhysicalMemory) / 1024 / 1024);
        System.out.printf("\t- \":\\\" %s mb \n", freePhysicalMemorySize / 1024 / 1024);
        System.out.printf("\t- RAM %s mb \n", freePhysicalMemory / 1024 / 1024);
    }
}
