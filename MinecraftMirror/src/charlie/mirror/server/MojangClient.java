package charlie.mirror.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class MojangClient {
    private LinkedList<DownloadTask> queue = new LinkedList<>();

    public void update(){
        try {
            DownloadTask versionManifest = new DownloadTask(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json"), Paths.get(MinecraftMirror.configManager.getHttpRoot(), "version_manifest.json").toFile());
            MinecraftMirror.pool.submit(versionManifest); //Don't add to queue
            while(versionManifest.getStatus() != DownloadTask.Status.IN_QUEUE && versionManifest.getStatus() != DownloadTask.Status.DOWNLOADING) ;
            if(versionManifest.getStatus() == DownloadTask.Status.ERROR){
                MinecraftMirror.logger.warning("Update failed: Couldn't download version manifest:" + versionManifest.getMessage());
                return;
            }
            JSONArray versions = new JSONObject(readAll(Paths.get(MinecraftMirror.configManager.getHttpRoot(), "version_manifest.json"))).getJSONArray("versions");
            for (int i = 0; i < versions.length(); i++) {
                JSONObject obj = versions.getJSONObject(i);
                URL url = new URL(obj.getString("url"));
            }
        } catch (IOException e) {
            MinecraftMirror.logger.throwing("charlie.mirror.server.MojangClient", "update", e);
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
