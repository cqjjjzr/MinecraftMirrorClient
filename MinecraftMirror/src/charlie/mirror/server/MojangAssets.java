package charlie.mirror.server;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.FutureTask;

import static charlie.mirror.server.MinecraftMirror.*;

public class MojangAssets implements Runnable {
    private static final URI ASSETS_ROOT = URI.create("http://resources.download.minecraft.net/");

    private JSONObject verJson;
    private URL iURL;

    public MojangAssets(JSONObject verJson){
        this.verJson = verJson;
    }

    public MojangAssets(URL iURL){
        verJson = new JSONObject();
        JSONObject assetsIndex = new JSONObject();
        assetsIndex.put("url", iURL.toString());
        verJson.put("assetsIndex", assetsIndex);
    }

    @Override
    public void run() {
        try{
            URL indexURL = new URL(verJson.getJSONObject("assetIndex").getString("url"));
            DownloadTask indexTask = new DownloadTask(indexURL, Paths.get(configManager.getHttpRoot(), "intlfile", "indexes", verJson.getString("assets") + ".json").toFile(), "ma");
            FutureTask<Void> indexFuture = new FutureTask<>(indexTask, null);
            queue.put(indexURL, indexTask);
            downloadPool.submit(indexFuture);
            while(!indexFuture.isDone()) ;

            JSONObject objectsObj = new JSONObject(new String(Files.readAllBytes(Paths.get(configManager.getHttpRoot(), "intlfile", "indexes", verJson.getString("assets") + ".json")))).getJSONObject("objects");
            Set<String> objectsNameSet = objectsObj.keySet();
            for (String objectName : objectsNameSet) {
                JSONObject objectObj = objectsObj.getJSONObject(objectName);
                String hash = objectObj.getString("hash");
                URL objectURL = ASSETS_ROOT.resolve(hash.substring(0, 2) + "/").resolve(hash).toURL();
                if(!queue.containsKey(objectURL)){
                    DownloadTask objectTask = new DownloadTask(objectURL, Paths.get(configManager.getHttpRoot(), "mc", "objects", hash.substring(0, 2), hash).toFile(), "asset", hash, objectObj.getInt("size"));
                    queue.put(objectURL, objectTask);
                    downloadPool.submit(objectTask);
                }
            }
        }catch (Exception e){
            MinecraftMirror.logger.warning("Mojang assets precess exception:" + e.getClass().toString()  + " " + e.getMessage());
        }
    }
}
