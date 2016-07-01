package charlie.mirror.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.FutureTask;

import static charlie.mirror.server.MinecraftMirror.queue;

public class MojangClient {

    public void update(){
        try {
            DownloadTask versionManifest = new DownloadTask(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json"), Paths.get(MinecraftMirror.configManager.getHttpRoot(), "intlfile", "version_manifest.json").toFile(), "manifest", true);
            FutureTask<Void> verTask = new FutureTask<>(versionManifest, null);
            MinecraftMirror.downloadPool.submit(verTask); //Don't add to queue
            while(!verTask.isDone()) ;
            if(versionManifest.getStatus() == DownloadTask.Status.ERROR){
                MinecraftMirror.logger.warning("Update failed: Couldn't download version manifest:" + versionManifest.getMessage());
                return;
            }
            JSONArray versions = new JSONObject(new String(Files.readAllBytes(Paths.get(MinecraftMirror.configManager.getHttpRoot(), "intlfile", "version_manifest.json")))).getJSONArray("versions");
            for (int i = 0; i < versions.length(); i++) {
                JSONObject obj = versions.getJSONObject(i);
                URL url = new URL(obj.getString("url"));
                if(!queue.containsKey(url)){
                    DownloadTask task = new DownloadTask(url, Paths.get(MinecraftMirror.configManager.getHttpRoot(), "intlfile", obj.getString("id") + ".json").toFile(), "mv", true);
                    queue.put(url, task);
                    FutureTask<Void> thread = new FutureTask<>(task, null);
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
