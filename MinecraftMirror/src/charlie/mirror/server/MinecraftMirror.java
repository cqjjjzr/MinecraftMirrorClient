package charlie.mirror.server;

import charlie.mirror.server.remote.RemoteServer;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MinecraftMirror {
    public final static int VERSION = 1;

    public static ConfigManager configManager;
    public static Logger logger;
    protected static ExecutorService downloadPool;
    protected static ExecutorService processPool;
    protected static ConcurrentHashMap<URL, DownloadTask> queue = new ConcurrentHashMap<>();
    protected static ConcurrentHashMap<URL, MemoryDownloadTask> jsonQueue = new ConcurrentHashMap<>();
    //protected static Hashtable<URL, DownloadTask> queue = new Hashtable<>();
    public static int fullSize;

    public static void main(String[] args) {
        configManager = new ConfigManager();
        configManager.init();
        logger = Logger.getGlobal();
        downloadPool = Executors.newFixedThreadPool(configManager.getMaxQueue());
        processPool = Executors.newCachedThreadPool();
        Inv mojangInv = new Inv(new MojangClient(), new ForgeClient());
        processPool.execute(mojangInv);
        ForgeInv forgeInv = new ForgeInv(new ForgeClient());
        processPool.execute(forgeInv);
        processPool.execute(new Checker());
        RemoteServer.init();
    }

    public static class Inv implements Runnable {
        private MojangClient mClient;
        private ForgeClient fClient;

        public Inv(MojangClient mClient, ForgeClient fClient){
            this.fClient = fClient;
            this.mClient = mClient;
        }

        @Override
        public void run() {
            while(true){
                fullSize = 0;
                mClient.update();
                fClient.update();
                try {
                    Thread.sleep(MinecraftMirror.configManager.getIntervalHour() * 1000 * 60 * 60);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    public static class ForgeInv implements Runnable {
        private ForgeClient client;

        public ForgeInv(ForgeClient client){
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

    public static Map<URL, DownloadTask> getQueue() {
        return queue;
    }
}
