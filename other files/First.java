import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.lang.management.ManagementFactory;

import java.io.File;

import java.util.Map;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;

import static org.apache.commons.lang3.SystemUtils.*;

public class   First {
    static void error() {
        System.out.printf("Error: not set parameter \"{p}\"\n");
    };
    static void osname() {
        // Название
        System.out.printf("OS name: %s\n", OS_NAME);         // OS name
    };
    static void osver() {
        // версия
        System.out.printf("OS version: %s\n", OS_VERSION);   // OS version
    };
    static void osarch() {
        // архитектура ОС
        System.out.printf("OS architecture: %s\n", OS_ARCH); // OS architecture
    };
    static void timez() {
        // Часовой пояс
        SimpleDateFormat formatTimeZone = new SimpleDateFormat("zzz");
        Date date = new Date();
        System.out.printf("Time zone: %s\n", formatTimeZone.format(date));
    };
    static void usname() {
        // имя пользователя
        System.out.printf("Username: %s\n", getUserName()); // Username
        System.out.printf("Hostname: %s\n", getHostName()); // Hostname
    };
    static void lang() {
        // установленный в системе язык
        String lang = Locale.getDefault().getLanguage();
        System.out.printf("System language: %s\n", lang);
    };
    static void ap() {
        // Получить загрузку процессора
        Runtime runtime = Runtime.getRuntime();
        System.out.printf("available processors: %s\n", runtime.availableProcessors());
    };
    static void spage() throws IOException {
        // получить размер файла подкачки
        Process exexe = Runtime.getRuntime().exec("wmic pagefile list /format:list");
        try (InputStream is = exexe.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.copy(is, baos);
            System.out.print(baos);
        }
    };
    static void tm() {
        // узнать размер диска
        File file = new File("\\");
        long totalPhysicalMemorySize = file.getTotalSpace();
        System.out.printf("total %s mb \":\\\"\n", totalPhysicalMemorySize / 1024 / 1024);
    };
    static void fm() {
        // сколько свободного места на диске
        File file = new File("\\");
        long freePhysicalMemorySize = file.getFreeSpace();
        System.out.printf("free %s mb \":\\\"\n", freePhysicalMemorySize / 1024 / 1024);
    };
    static void ram() {
        // общий размер оперативной памяти
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long physicalMemorySize = os.getTotalPhysicalMemorySize();
        long freePhysicalMemory = os.getFreePhysicalMemorySize();
        System.out.printf("total %s mb RAM\n", physicalMemorySize / 1024 / 1024);
        System.out.printf("free %s mb RAM\n", freePhysicalMemory / 1024 / 1024);
    };
    static void ftm() {
        // размер свободной физической памяти
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long freePhysicalMemory = os.getFreePhysicalMemorySize();
        File file = new File("\\");
        long freePhysicalMemorySize = file.getFreeSpace();
        System.out.printf("all free %s mb: \n", (freePhysicalMemorySize + freePhysicalMemory) / 1024 / 1024);
        System.out.printf("\t- \":\\\" %s mb \n", freePhysicalMemorySize / 1024 / 1024);
        System.out.printf("\t- RAM %s mb \n", freePhysicalMemory / 1024 / 1024);
    };
    static void env() {
        // Получить значение всех переменных окружения сразу
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            System.out.printf("%s = %s\n", envName, env.get(envName));
        }
    };
    static void envp(String var) {
        // значение указанной переменной окружения
        System.out.printf("%s = %s\n", var, System.getenv(var));
    };
    static void mem(String var) {
        // задание варианта
        String param = var;
        try {
            int delayMinut = Integer.valueOf(param);
            if (delayMinut < 1|| delayMinut > 15) {
                System.out.println("Parameter must be in [5, 15] interval\n");
            } else {
                System.out.printf("Memory info for %s minutes\n", delayMinut);
                long delay = delayMinut * 60000L;
                long peroid = 6000L;
                Timer timer = new Timer("Timer");
                Timer timer2 = new Timer("TimerCancel");
                TimerTask task = new TimerTask() {
                    public void run() {
                        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                        long physicalMemorySize = os.getTotalPhysicalMemorySize();
                        long freePhysicalMemory = os.getFreePhysicalMemorySize();
                        long freeSwapSize = os.getFreeSwapSpaceSize();
                        long commitedVirtualMemorySize = os.getCommittedVirtualMemorySize();
                        System.out.printf("Total physical memory size = %s byte (%s mb)\n", physicalMemorySize, physicalMemorySize / 1024 / 1024);
                        System.out.printf("Free physical memory size = %s byte (%s mb)\n", freePhysicalMemory, freePhysicalMemory / 1024 / 1024);
                        System.out.printf("Free swap space size = %s byte (%s mb)\n", freeSwapSize, freeSwapSize / 1024 / 1024);
                        System.out.printf("Committed virtual memory size = %s byte (%s mb)\n\n\n", commitedVirtualMemorySize, commitedVirtualMemorySize / 1024 / 1024);
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
    };

    public static void main(String[] args) throws IOException {
        boolean goodInputFlag = false;
        boolean skip = false;
        for (int i = 0; i < args.length; i++) {
            if (skip) {
                skip = false;
                continue;
            }
            switch (args[i]) {
                case ("help"):
                    if (i == 0) {
                        goodInputFlag = true;
                        System.out.printf("Available commands: -osname, -osver, -osarch, -timez, -usname," +
                                " -lang, -ap, -spage, -tm, -fm, -ram, -ftm, -env, -envp {p}, -mem {p}\n");
                    }
                    break;
                case ("-osname"):
                    goodInputFlag = true;
                    osname();
                    break;
                case ("-osver"):
                    goodInputFlag = true;
                    osver();
                    break;
                case ("-osarch"):
                    goodInputFlag = true;
                    osarch();
                    break;
                case ("-timez"):
                    goodInputFlag = true;
                    timez();
                    break;
                case ("-usname"):
                    goodInputFlag = true;
                    usname();
                    break;
                case ("-lang"):
                    goodInputFlag = true;
                    lang();
                    break;
                case ("-ap"):
                    goodInputFlag = true;
                    ap();
                    break;
                case ("-spage"):
                    goodInputFlag = true;
                    spage();
                    break;
                case ("-tm"):
                    goodInputFlag = true;
                    tm();
                    break;
                case  ("-fm"):
                    goodInputFlag = true;
                    fm();
                    break;
                case ("-ram"):
                    goodInputFlag = true;
                    ram();
                    break;
                case ("-ftm"):
                    goodInputFlag = true;
                    ftm();
                    break;
                case ("-env"):
                    goodInputFlag = true;
                    env();
                    break;
                case ("-envp"):
                    goodInputFlag = true;
                    if (i == args.length-1) { // если надпись "-envp" последняя (то есть после нее нет параметров)
                        error();
                    } else if ("-".indexOf(args[i+1].charAt(0)) != 0) { // если следующая запись не начинается с "-"
                        envp(args[i+1]);
                        skip = true;    // использовался аргумент с индексом +1, поэтому его пропускаем в след итерации
                    } else {
                        error();
                    }
                    break;
                case ("-mem"):
                    goodInputFlag = true;
                    if (i == args.length-1) {
                        error();
                    } else if ("-".indexOf(args[i+1].charAt(0)) == -1) {
                        mem(args[i+1]);
                        skip = true;
                    } else {
                        error();
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
}
