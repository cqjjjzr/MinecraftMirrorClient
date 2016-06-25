package charlie.mirror.server;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MinecraftMirror {
    public final static int VERSION = 1;

    protected static ConfigManager configManager;
    protected static Logger logger;
    protected static ExecutorService downloadPool;
    protected static ExecutorService processPool;
    protected static LinkedHashMap<URL, DownloadTask> queue = new LinkedHashMap<>();

    public static void main(String[] args) {
        configManager = new ConfigManager();
        configManager.init();
        logger = Logger.getGlobal();
        downloadPool = Executors.newFixedThreadPool(configManager.getMaxQueue());
        processPool = Executors.newCachedThreadPool();
        new MojangClient().update();
        processPool.submit(new Checker());
    }

    private class MojangInv implements Runnable {
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
                } catch (InterruptedException e) {}
            }
        }
    }
}
