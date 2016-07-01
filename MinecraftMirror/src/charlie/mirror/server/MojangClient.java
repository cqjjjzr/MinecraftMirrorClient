package charlie.mirror.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import static charlie.mirror.server.MinecraftMirror.jsonQueue;
import static charlie.mirror.server.MinecraftMirror.queue;

public class MojangClient {

    public void update(){
        try {
            MemoryDownloadTask versionManifest = new MemoryDownloadTask(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json"), "manifest");
            String versionM = versionManifest.call();
            if(versionManifest.getStatus() == MemoryDownloadTask.Status.ERROR){
                MinecraftMirror.logger.warning("Update failed: Couldn't download version manifest:" + versionManifest.getMessage());
                return;
            }
            JSONArray versions = new JSONObject(versionM).getJSONArray("versions");
            for (int i = 0; i < versions.length(); i++) {
                JSONObject obj = versions.getJSONObject(i);
                URL url = new URL(obj.getString("url"));
                if(!queue.containsKey(url)){
                    MemoryDownloadTask task = new MemoryDownloadTask(url, "mv");
                    jsonQueue.put(url, task);
                    MinecraftMirror.processPool.submit(new MojangVersion(task));
                }
            }
        } catch (Exception e) {
            MinecraftMirror.logger.warning("Mojang updating exception:" + e.getClass().toString()  + " " + e.getMessage());
            //e.printStackTrace();
        }
    }
}
