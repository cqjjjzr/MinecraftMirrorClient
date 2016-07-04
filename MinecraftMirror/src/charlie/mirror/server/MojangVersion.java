package charlie.mirror.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static charlie.mirror.server.MinecraftMirror.*;

public class MojangVersion implements Runnable {
    private MemoryDownloadTask task;

    private static final URI VERSION_ROOT = URI.create("https://launcher.mojang.com/mc/game/");
    private static final URI LIBRARIES_ROOT = URI.create("https://libraries.minecraft.net/");

    public MojangVersion(MemoryDownloadTask task){
        this.task = task;
    }

    @Override
    public void run() {
        try{
            String json = task.call();
            if(task.getStatus() == MemoryDownloadTask.Status.ERROR) return;
            JSONObject rootObj = new JSONObject(json);
            JSONArray librariesArr = rootObj.getJSONArray("libraries");
            for (int i = 0; i < librariesArr.length(); i++) {
                JSONObject downloadsObj = librariesArr.getJSONObject(i).getJSONObject("downloads");
                if (downloadsObj.toString().contains("classifiers")) {
                    JSONObject classifiersObj = downloadsObj.getJSONObject("classifiers");
                    boolean flag = true;
                    if(classifiersObj.toString().contains("natives-windows-32")){
                        JSONObject libObj = classifiersObj.getJSONObject("natives-windows-32");
                        startLibTask(libObj.getString("url"), libObj.getString("sha1"), libObj.getInt("size"));
                        flag = false;
                    }
                    if(classifiersObj.toString().contains("natives-windows-64")){
                        JSONObject libObj = classifiersObj.getJSONObject("natives-windows-64");
                        startLibTask(libObj.getString("url"), libObj.getString("sha1"), libObj.getInt("size"));
                        flag = false;
                    }
                    if (classifiersObj.toString().contains("natives-linux")) {
                        JSONObject libObj = classifiersObj.getJSONObject("natives-linux");
                        startLibTask(libObj.getString("url"), libObj.getString("sha1"), libObj.getInt("size"));
                    }
                    if (classifiersObj.toString().contains("natives-windows") && flag) {
                        JSONObject libObj = classifiersObj.getJSONObject("natives-windows");
                        startLibTask(libObj.getString("url"), libObj.getString("sha1"), libObj.getInt("size"));
                    }
                    if (classifiersObj.toString().contains("natives-osx")) {
                        JSONObject libObj = classifiersObj.getJSONObject("natives-osx");
                        startLibTask(libObj.getString("url"), libObj.getString("sha1"), libObj.getInt("size"));
                    }
                }
                if (downloadsObj.toString().contains("artifact")) {
                    {
                        JSONObject artifactObj = downloadsObj.getJSONObject("artifact");
                        startLibTask(artifactObj.getString("url"), artifactObj.getString("sha1"), artifactObj.getInt("size"));
                    }
                }
            }

            JSONObject downloadsObj  = rootObj.getJSONObject("downloads");
            if (downloadsObj.toString().contains("client")) {
                JSONObject verObj = downloadsObj.getJSONObject("client");
                startVerTask(verObj.getString("url"), verObj.getString("sha1"), verObj.getInt("size"));
            }
            if (downloadsObj.toString().contains("server")) {
                JSONObject verObj = downloadsObj.getJSONObject("server");
                startVerTask(verObj.getString("url"), verObj.getString("sha1"), verObj.getInt("size"));
            }
            if (downloadsObj.toString().contains("windows_server")) {
                JSONObject verObj = downloadsObj.getJSONObject("windows_server");
                startVerTask(verObj.getString("url"), verObj.getString("sha1"), verObj.getInt("size"));
            }

            MojangAssets mojangAssets = new MojangAssets(rootObj);
            processPool.submit(mojangAssets);
        }catch (Exception e){
            MinecraftMirror.logger.warning("Mojang precess exception:" + e.getClass().toString()  + " " + e.getMessage());
            //e.printStackTrace();
        }
    }

    private void startLibTask(String u, String hash, int length) throws IOException, URISyntaxException {
        URL url = new URL(u);
        if(!queue.containsKey(url)){
            DownloadTask task = new DownloadTask(url, Paths.get(configManager.getHttpRoot(), "mc", "libraries", LIBRARIES_ROOT.relativize(url.toURI()).toString()).toFile(), hash, length, DigestHelper.Digest.SHA1);
            queue.put(url, task);
            downloadPool.submit(task);
            MinecraftMirror.fullSize += 1;
        }
    }

    private void startVerTask(String u, String hash, int length) throws IOException, URISyntaxException {
        URL url = new URL(u);
        if(!queue.containsKey(url)){
            DownloadTask task = new DownloadTask(url, Paths.get(configManager.getHttpRoot(), "mc", "game", VERSION_ROOT.relativize(url.toURI()).toString()).toFile(), hash, length, DigestHelper.Digest.SHA1);
            queue.put(url, task);
            downloadPool.submit(task);
            MinecraftMirror.fullSize += 1;
        }
    }
}
