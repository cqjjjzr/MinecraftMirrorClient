package charlie.mirror.server;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

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
                        MinecraftMirror.queue.put(task.getUrl(), task);
                        MinecraftMirror.downloadPool.submit(task);
                        MinecraftMirror.logger.info("Retry task " + task.getUrl().toString());
                    }
                    if(task.getStatus() == DownloadTask.Status.COMPLETED){
                        iterator.remove();
                        //continue outside;
                    }
                }

                Iterator<Map.Entry<URL, MemoryDownloadTask>> jIterator = MinecraftMirror.jsonQueue.entrySet().iterator();
                while(jIterator.hasNext()){
                    MemoryDownloadTask task = jIterator.next().getValue();
                    if(task.getStatus() == MemoryDownloadTask.Status.ERROR){
                        iterator.remove();
                        switch (task.getTag()){
                            case "mv":
                                MinecraftMirror.jsonQueue.put(task.getUrl(), task);
                                MinecraftMirror.processPool.submit(new MojangVersion(task));
                                MinecraftMirror.logger.info("Retry task " + task.getUrl().toString());
                                break;
                            case "ma":
                                MinecraftMirror.jsonQueue.put(task.getUrl(), task);
                                MinecraftMirror.processPool.submit(new MojangAssets(task.getUrl()));
                                MinecraftMirror.logger.info("Retry task " + task.getUrl().toString());
                            default:
                                MinecraftMirror.jsonQueue.put(task.getUrl(), task);
                                MinecraftMirror.downloadPool.submit(task);
                                MinecraftMirror.logger.info("Retry task " + task.getUrl().toString());
                        }
                        if(task.getStatus() == MemoryDownloadTask.Status.COMPLETED){
                            iterator.remove();
                            //continue outside;
                        }
                    }
                }
            } catch (Exception e) {
                MinecraftMirror.logger.warning("Checker exception:" + e.getClass().toString()  + " " + e.getMessage());
            }
            //MinecraftMirror.logger.info("All updated.");
        }
    }
}
