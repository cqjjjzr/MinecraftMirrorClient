package charlie.mirror.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MinecraftMirror {
    public final static int VERSION = 1;

    protected static ConfigManager configManager;
    protected static Logger logger;
    protected static ExecutorService pool;

    public static void main(String[] args) {
        configManager = new ConfigManager();
        configManager.init();
        logger = Logger.getGlobal();
        pool = Executors.newCachedThreadPool();
    }
}
