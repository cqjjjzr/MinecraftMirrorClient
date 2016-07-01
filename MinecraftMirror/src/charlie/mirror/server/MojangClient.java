package charlie.mirror.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.concurrent.FutureTask;

import static charlie.mirror.server.MinecraftMirror.jsonQueue;
import static charlie.mirror.server.MinecraftMirror.queue;

public class MojangClient {

    public void update(){
        try {
            MemoryDownloadTask versionManifest = new MemoryDownloadTask(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json"), "manifest");
            FutureTask verTask = new FutureTask<>(versionManifest);
            MinecraftMirror.downloadPool.submit(verTask); //Don't add to queue
            while(!verTask.isDone()) ;
            if(versionManifest.getStatus() == MemoryDownloadTask.Status.ERROR){
                MinecraftMirror.logger.warning("Update failed: Couldn't download version manifest:" + versionManifest.getMessage());
                return;
            }
            JSONArray versions = new JSONObject((String) verTask.get()).getJSONArray("versions");
            for (int i = 0; i < versions.length(); i++) {
                JSONObject obj = versions.getJSONObject(i);
                URL url = new URL(obj.getString("url"));
                if(!queue.containsKey(url)){
                    MemoryDownloadTask task = new MemoryDownloadTask(url, "mv");
                    jsonQueue.put(url, task);
                    FutureTask thread = new FutureTask<>(task);
                    MinecraftMirror.downloadPool.submit(thread);
                    MinecraftMirror.processPool.submit(new MojangVersion(task, thread));
                }
            }
        } catch (Exception e) {
            MinecraftMirror.logger.warning("Mojang updating exception:" + e.getClass().toString()  + " " + e.getMessage());
            //e.printStackTrace();
        }
    }
}
