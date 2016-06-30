package charlie.mirror.server;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.FutureTask;

import static charlie.mirror.server.MinecraftMirror.queue;

public class Checker implements Runnable {
    @Override
    public void run() {
        outside:while(true){
            try {
                Thread.sleep(10000);
                Iterator<Map.Entry<URL, DownloadTask>> iterator = MinecraftMirror.queue.entrySet().iterator();
                while(iterator.hasNext()){
                    DownloadTask task = iterator.next().getValue();
                    if(task.getStatus() == DownloadTask.Status.ERROR){
                        iterator.remove();
                        switch (task.getTag()){
                            case "mv":
                                queue.put(task.getUrl(), task);
                                FutureTask<Void> thread = new FutureTask<>(task, null);
                                MinecraftMirror.downloadPool.submit(thread);
                                MinecraftMirror.processPool.submit(new MojangVersion(task, thread));
                                MinecraftMirror.logger.info("Retry task " + task.getUrl().toString());
                                break;
                            case "ma":
                                queue.put(task.getUrl(), task);
                                MinecraftMirror.downloadPool.submit(task);
                                MinecraftMirror.processPool.submit(new MojangAssets(task.getUrl()));
                                MinecraftMirror.logger.info("Retry task " + task.getUrl().toString());
                            default:
                                queue.put(task.getUrl(), task);
                                MinecraftMirror.downloadPool.submit(task);
                                MinecraftMirror.logger.info("Retry task " + task.getUrl().toString());
                        }
                    }
                    if(task.getStatus() == DownloadTask.Status.COMPLETED || task.getStatus() == DownloadTask.Status.ERROR){
                        iterator.remove();
                        //continue outside;
                    }
                }
            } catch (Exception e) {
                MinecraftMirror.logger.warning("Checker exception:" + e.getClass().toString()  + " " + e.getMessage());
            }
            //MinecraftMirror.logger.info("All updated.");
        }
    }
}
