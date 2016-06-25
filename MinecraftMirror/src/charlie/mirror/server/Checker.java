package charlie.mirror.server;

public class Checker implements Runnable {
    @Override
    public void run() {
        outside:while(true){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {}
            for(DownloadTask task : MinecraftMirror.queue.values()){
                if(task.getStatus() != DownloadTask.Status.COMPLETED)
                    continue outside;
                MinecraftMirror.logger.info("All updated.");
            }
        }
    }
}
