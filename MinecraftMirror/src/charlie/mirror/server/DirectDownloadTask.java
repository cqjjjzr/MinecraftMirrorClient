package charlie.mirror.server;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DirectDownloadTask implements Runnable{
    private URL url;
    private Status status;
    private String message;
    private File path;
    private String tag;
    private boolean sum = false;
    private String sha1;
    private int length;

    public DirectDownloadTask(URL url, File path, String tag){
        this.url = url;
        this.status = Status.IN_QUEUE;
        this.message = "Waiting for queue.";
        this.path = path;
        this.path.getParentFile().mkdirs();
        this.tag = tag;
    }

    public DirectDownloadTask(URL url, File path, String tag, String sha1, int length){
        this.url = url;
        this.status = Status.IN_QUEUE;
        this.message = "Waiting for queue.";
        this.path = path;
        this.path.getParentFile().mkdirs();
        this.tag = tag;
        this.sum = true;
        this.sha1 = sha1;
        this.length = length;
    }

    @Override
    public void run() {
        //System.out.println("StartTask");
        this.status = Status.DOWNLOADING;
        this.message = "Try to establish connection to server";
        try {
            if(path.exists()){
                this.message = "File already existed. Download stopped.";
                this.status = Status.COMPLETED;
            }else{
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(MinecraftMirror.configManager.getConnectTimeout());
                connection.setReadTimeout(MinecraftMirror.configManager.getReadTimeout());
                connection.setRequestProperty("User-Agent", MinecraftMirror.configManager.getUserAgent());
                connection.connect();
                int code = connection.getResponseCode();
                if(code == 200){
                    int length = connection.getContentLength();
                    this.message = "Connect established. ContentLength:" + length + ". Start downloading...";
                    long flen = path.exists() ? Files.size(path.toPath()) : -1;
                    FileOutputStream fileOutputStream = new FileOutputStream(path.getPath() + ".dl");
                    InputStream inputStream = connection.getInputStream();
                    int readLength;
                    int process = 0;
                    byte[] buffer = new byte[1024];
                    while((readLength = inputStream.read(buffer)) != -1){
                        fileOutputStream.write(buffer, 0, readLength);
                        process += readLength;
                        this.message = "Downloading, downloaded " + process + "B of " + length + "B. " + (((double) process) / length) * 100 + "%";
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    connection.disconnect();
                    if(sum){
                        this.message = "Downloaded. Checking sha1sum.";
                        File dlF = new File(path.getPath() + ".dl");
                        String fsha1 = DigestHelper.sha1(dlF, this.length);
                        if(fsha1.equalsIgnoreCase(sha1)){
                            System.gc();
                            FileUtils.forceDelete(path);
                            Files.move(new File(path.getPath() + ".dl").toPath(), path.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            this.message = "Completed.";
                            this.status = Status.COMPLETED;
                            MinecraftMirror.logger.info("Downloaded " + url.toString());
                        }else{
                            this.message = "Error checksum. Abort.";
                            this.status = Status.ERROR;
                            MinecraftMirror.logger.info("Checksum:" + sha1 +  " and " + fsha1);
                        }
                    }else{
                        System.gc();
                        FileUtils.forceDelete(path);
                        //new File(path.getPath() + ".dl").renameTo(path);
                        Files.move(new File(path.getPath() + ".dl").toPath(), path.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        this.message = "Completed.";
                        this.status = Status.COMPLETED;
                        MinecraftMirror.logger.info("Downloaded " + url.toString());
                    }
                }else{
                    status = Status.ERROR;
                    message = "Bad response code:" + code;
                }
            }
        } catch (Exception e) {
            status = Status.ERROR;
           // MinecraftMirror.logger.warning("Downloading exception:" + e.getClass().toString() + " " + e.getMessage());
            e.printStackTrace();
            this.message = "Error:" + e.toString();
        }
    }

    public enum Status{
        IN_QUEUE, DOWNLOADING, ERROR, COMPLETED
    }

    public URL getUrl() {
        return url;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public File getPath() {
        return path;
    }

    public String getTag() {
        return tag;
    }
}
