import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

class SystemInfoUtility {
    public static void main(String[] args) throws SocketException {
        System.out.println("Computer name: " + getComputerName());
        System.out.println("Operating system: " + getOperatingSystemInfo());
        System.out.println("Disk information: ");
        getDiskInfo();
        System.out.println("User name: " + getUserInfo());
        System.out.println("Network settings: ");
        getNetworkSettings();
    }
    public static String getComputerName() {
        char[] buffer = new char[WinBase.MAX_COMPUTERNAME_LENGTH + 1];
        Kernel32.INSTANCE.GetComputerName(buffer, new IntByReference(buffer.length));// new WinDef.DWORD(buffer.length));
        return Native.toString(buffer);
    }
    public static String getOperatingSystemInfo() {
        return String.format("%s (%s)", Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion", "ProductName"),
                Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion", "CurrentVersion"));
    }
    public static void getDiskInfo() {
        File[] drives = File.listRoots();
        if (drives != null && drives.length > 0) {
            for (File aDrive : drives) {
                long totalSpace = aDrive.getTotalSpace();
                long freeSpace = aDrive.getFreeSpace();
                System.out.printf("size of %s: %s mb. free space: %s md\n", aDrive, totalSpace / 1024 / 1024, freeSpace / 1024 / 1024);
            }
        }
    }
    public static String getUserInfo() {
        return System.getProperty("user.name");
    }
    public static void getNetworkSettings() throws SocketException {
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            System.out.printf("net config: %s\n", socket.getLocalAddress().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}


public class javasix {
    public static void main(String[] args) throws SocketException {
        System.out.println("Функции получения имени машины, информации об операционной системе, " +
                "информации о дисках, информации о пользователе, сетевые настройки");
        SystemInfoUtility utility = new SystemInfoUtility();
        utility.main(new String[] {""});
    }
};

