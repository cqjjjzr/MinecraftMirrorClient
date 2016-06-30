package charlie.mirror.server;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MinecraftMirror {
    public final static int VERSION = 1;

    protected static ConfigManager configManager;
    protected static Logger logger;
    protected static ExecutorService downloadPool;
    protected static ExecutorService processPool;
    protected static ConcurrentHashMap<URL, DownloadTask> queue = new ConcurrentHashMap<>();
    //protected static Hashtable<URL, DownloadTask> queue = new Hashtable<>();

    public static void main(String[] args) {
        configManager = new ConfigManager();
        configManager.init();
        logger = Logger.getGlobal();
        downloadPool = Executors.newFixedThreadPool(configManager.getMaxQueue());
        processPool = Executors.newCachedThreadPool();
        GUI.show();
        processPool.execute(new Checker());
    }
}
