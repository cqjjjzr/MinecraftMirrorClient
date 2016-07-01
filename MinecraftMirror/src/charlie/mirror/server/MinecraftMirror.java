package charlie.mirror.server;

import java.awt.*;
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
    protected static ConcurrentHashMap<URL, MemoryDownloadTask> jsonQueue = new ConcurrentHashMap<>();
    //protected static Hashtable<URL, DownloadTask> queue = new Hashtable<>();

    public static void main(String[] args) {
        configManager = new ConfigManager();
        configManager.init();
        logger = Logger.getGlobal();
        downloadPool = Executors.newFixedThreadPool(configManager.getMaxQueue());
        processPool = Executors.newCachedThreadPool();
        try{
            GUI.show();
        }catch (HeadlessException e){
            MinecraftMirror.MojangInv mojangInv = new MinecraftMirror.MojangInv(new MojangClient());
            processPool.execute(mojangInv);
        }
        processPool.execute(new Checker());
    }

    public static class MojangInv implements Runnable {
        private MojangClient client;

        public MojangInv(MojangClient client){
            this.client = client;
        }

        @Override
        public void run() {
            while(true){
                client.update();
                try {
                    Thread.sleep(MinecraftMirror.configManager.getIntervalHour() * 1000 * 60 * 60);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
