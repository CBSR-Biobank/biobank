package edu.ualberta.med.scannerconfig.dmscanlib;

@SuppressWarnings("nls")
public class LibraryLoader {

    private static LibraryLoader instance = null;

    private final boolean IS_MS_WINDOWS = System.getProperty("os.name").startsWith("Windows");
    private final boolean IS_LINUX = System.getProperty("os.name").startsWith("Linux");
    private final boolean IS_ARCH_64_BIT = System.getProperty("os.arch").equals("amd64");

    private LibraryLoader() {
        if (IS_MS_WINDOWS) {
            System.loadLibrary("OpenThreadsWin32");
            System.loadLibrary("dmscanlib");
        } else if (IS_LINUX && IS_ARCH_64_BIT){
            System.loadLibrary("dmscanlib64");
        }
    }

    public static LibraryLoader getInstance() {
        if (instance != null) return instance;

        instance = new LibraryLoader();
        return instance;
    }

    public boolean runningMsWindows() {
        return IS_MS_WINDOWS;
    }

}
