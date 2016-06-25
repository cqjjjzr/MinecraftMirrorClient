package charlie.mirror.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.FutureTask;

import static charlie.mirror.server.MinecraftMirror.*;

public class MojangVersion implements Runnable {
    private DownloadTask task;
    private FutureTask<Void> thread;

    private static final URI VERSION_ROOT = URI.create("https://launcher.mojang.com/mc/game/");
    private static final URI LIBRARIES_ROOT = URI.create("https://libraries.minecraft.net/");

    public MojangVersion(DownloadTask task, FutureTask thread){
        this.task = task;
        this.thread = thread;
    }

    @Override
    public void run() {
        while(!thread.isDone()){
            if(task.getStatus() == DownloadTask.Status.ERROR) return;
        }

        try{
            JSONObject rootObj = new JSONObject(readAll(task.getPath().toPath()));
            JSONArray librariesArr = rootObj.getJSONArray("libraries");
            for (int i = 0; i < librariesArr.length(); i++) {
                JSONObject downloadsObj = librariesArr.getJSONObject(i).getJSONObject("downloads");
                if (downloadsObj.toString().contains("classifiers")) {
                    JSONObject classifiersObj = downloadsObj.getJSONObject("classifiers");
                    boolean flag = true;
                    if(classifiersObj.toString().contains("natives-windows-32")){
                        startLibTask(classifiersObj.getJSONObject("natives-windows-32").getString("url"));
                        flag = false;
                    }
                    if(classifiersObj.toString().contains("natives-windows-64")){
                        startLibTask(classifiersObj.getJSONObject("natives-windows-64").getString("url"));
                        flag = false;
                    }
                    if (classifiersObj.toString().contains("natives-linux")) {
                        startLibTask(classifiersObj.getJSONObject("natives-linux").getString("url"));
                    }
                    if (classifiersObj.toString().contains("natives-windows") && flag) {
                        startLibTask(classifiersObj.getJSONObject("natives-windows").getString("url"));
                    }
                    if (classifiersObj.toString().contains("natives-osx")) {
                        startLibTask(classifiersObj.getJSONObject("natives-osx").getString("url"));
                    }
                }
                if (downloadsObj.toString().contains("artifact")) {
                    {
                        JSONObject artifactObj = downloadsObj.getJSONObject("artifact");
                        startLibTask(artifactObj.getString("url"));
                    }
                }
            }

            JSONObject downloadsObj  = rootObj.getJSONObject("downloads");
            if (downloadsObj.toString().contains("client")) {
                startVerTask(downloadsObj.getJSONObject("client").getString("url"));
            }
            if (downloadsObj.toString().contains("server")) {
                startVerTask(downloadsObj.getJSONObject("server").getString("url"));
            }
            if (downloadsObj.toString().contains("windows-server")) {
                startVerTask(downloadsObj.getJSONObject("windows-server").getString("url"));
            }
        }catch (Exception e){
            MinecraftMirror.logger.warning("Mojang precess exception:" + e.getClass().toString()  + " " + e.getMessage());
            //e.printStackTrace();
        }
    }

    private void startLibTask(String u) throws IOException, URISyntaxException {
        URL url = new URL(u);
        if(!queue.containsKey(url)){
            DownloadTask task = new DownloadTask(url, Paths.get(configManager.getHttpRoot(), "mc", "libraries", LIBRARIES_ROOT.relativize(url.toURI()).toString()).toFile(), "lib");
            queue.put(url, task);
            downloadPool.submit(task);
        }
    }

    private void startVerTask(String u) throws IOException, URISyntaxException {
        URL url = new URL(u);
        if(!queue.containsKey(url)){
            DownloadTask task = new DownloadTask(url, Paths.get(configManager.getHttpRoot(), "mc", "game", VERSION_ROOT.relativize(url.toURI()).toString()).toFile(), "ver");
            queue.put(url, task);
            downloadPool.submit(task);
        }
    }

    private String readAll(Path path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path.toFile());
        byte[] buf = new byte[fileInputStream.available()];
        fileInputStream.read(buf);
        fileInputStream.close();
        return new String(buf);
    }
}
