package charlie.mirror.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static charlie.mirror.server.MinecraftMirror.*;

public class ForgeClient {
    public void update(){
        try {
            MemoryDownloadTask manifestTask = new MemoryDownloadTask(new URL("http://files.minecraftforge.net/maven/net/minecraftforge/forge/json"), "fmv");
            String jsonStr = manifestTask.call();
            JSONObject rootObj = new JSONObject(jsonStr);
            URI webPath = URI.create(rootObj.getString("webpath"));
            JSONObject numberObj = rootObj.getJSONObject("number");
            for(String key : numberObj.keySet()){
                JSONObject versionObj = numberObj.getJSONObject(key);
                parseVersion(rootObj.getString("artifact"), versionObj, webPath);
            }
        } catch (Exception e) {
            MinecraftMirror.logger.warning("Forge updating exception:" + e.getClass().toString()  + " " + e.getMessage());
        }
    }

    private void parseVersion(String rootArtifact, JSONObject versionObj, URI webPath) throws MalformedURLException {
        if(versionObj.get("mcversion") == JSONObject.NULL) return;
        StringBuilder endpoint1 = new StringBuilder();
        endpoint1.append(versionObj.getString("mcversion"));
        endpoint1.append("-");
        endpoint1.append(versionObj.getString("version"));
        Object branch = versionObj.get("branch");
        if(branch != JSONObject.NULL && !((String) branch).trim().isEmpty()){
            endpoint1.append("-");
            endpoint1.append(branch);
        }
        endpoint1.append("/");
        URI pathEP1 = webPath.resolve(endpoint1.toString());
        JSONArray files = versionObj.getJSONArray("files");
        StringBuilder endpointRoot = new StringBuilder(rootArtifact);
        endpointRoot.append("-");
        endpointRoot.append(versionObj.getString("mcversion"));
        endpointRoot.append("-");
        endpointRoot.append(versionObj.getString("version"));
        if(branch != JSONObject.NULL && !((String) branch).trim().isEmpty()){
            endpointRoot.append("-");
            endpointRoot.append(branch);
        }
        for(int i = 0;i < files.length();i++){
            JSONArray file = files.getJSONArray(i);
            String filename = endpointRoot.toString() + "-" + file.getString(1) + "." + file.getString(0);
            String sha1 = file.getString(2);
            URI full = pathEP1.resolve(filename);
            DownloadTask task = new DownloadTask(full.toURL(), Paths.get(configManager.getHttpRoot(), "forge", endpoint1.toString(), filename).toFile(), sha1, -1, DigestHelper.Digest.MD5);
            MinecraftMirror.downloadPool.execute(task);
            MinecraftMirror.queue.put(full.toURL(), task);
            MinecraftMirror.fullSize += 1;
            //System.out.println(task.getUrl());
        }
    }

    public static void main(String[] args) {
        configManager = new ConfigManager();
        configManager.init();
        MinecraftMirror.logger = Logger.getGlobal();
        downloadPool = Executors.newFixedThreadPool(configManager.getMaxQueue());
        processPool = Executors.newCachedThreadPool();
        new ForgeClient().update();
    }
}
