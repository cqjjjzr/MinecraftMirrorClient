package charlie.mirror.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class DownloadTask implements Runnable{
    private URL url;
    private Status status;
    private String message;
    private File path;
    private String tag;
    private boolean override;
    private boolean sum = false;
    private String sha1;
    private int length;

    public DownloadTask(URL url, File path, String tag){
        this.url = url;
        this.status = Status.IN_QUEUE;
        this.message = "Waiting for queue.";
        this.path = path;
        this.path.getParentFile().mkdirs();
        this.tag = tag;
        this.override = false;
    }

    public DownloadTask(URL url, File path, String tag, String sha1, int length){
        this.url = url;
        this.status = Status.IN_QUEUE;
        this.message = "Waiting for queue.";
        this.path = path;
        this.path.getParentFile().mkdirs();
        this.tag = tag;
        this.override = false;
        this.sum = true;
        this.sha1 = sha1;
        this.length = length;
    }

    public DownloadTask(URL url, File path, String tag, boolean override){
        this.url = url;
        this.status = Status.IN_QUEUE;
        this.message = "Waiting for queue.";
        this.path = path;
        this.path.getParentFile().mkdirs();
        this.tag = tag;
        this.override = override;
    }

    @Override
    public void run() {
        //System.out.println("StartTask");
        this.status = Status.DOWNLOADING;
        this.message = "Try to establish connection to server";
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(MinecraftMirror.configManager.getConnectTimeout());
            connection.setReadTimeout(MinecraftMirror.configManager.getReadTimeout());
            connection.setRequestProperty("User-Agent", MinecraftMirror.configManager.getUserAgent());
            connection.connect();
            int code = connection.getResponseCode();
            if(code == 200){
                int length = connection.getContentLength();
                this.message = "Connect established. ContentLength:" + length + ". Start downloading...";
                long flen = path.exists() ? new FileInputStream(path).available() : -1;
                if(!path.exists() || override || flen != connection.getContentLength() || (sum && length != flen)){
                    FileOutputStream fileOutputStream = new FileOutputStream(path.getPath() + ".dl");
                    InputStream inputStream = connection.getInputStream();
                    int readLength;
                    int process = 0;
                    byte[] buffer = new byte[1024];
                    while((readLength = inputStream.read(buffer)) != -1){
                        fileOutputStream.write(buffer, 0, readLength);
                        process += readLength;
                        this.message = "Downloading, downloaded " + process + "B of " + length + "B. " + (((double) readLength) / length) * 100 + "%";
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    connection.disconnect();
                    if(sum){
                        this.message = "Downloaded. Checking sha1sum.";
                        File dlF = new File(path.getPath() + ".dl");
                        String fsha1 = DigestHelper.sha1(dlF, this.length);
                        if(fsha1.equalsIgnoreCase(sha1)){
                            Files.deleteIfExists(path.toPath());
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
                        Files.deleteIfExists(path.toPath());
                        //new File(path.getPath() + ".dl").renameTo(path);
                        Files.move(new File(path.getPath() + ".dl").toPath(), path.toPath(), StandardCopyOption.ATOMIC_MOVE);
                        this.message = "Completed.";
                        this.status = Status.COMPLETED;
                        MinecraftMirror.logger.info("Downloaded " + url.toString());
                    }
                }else{
                    this.message = "File already existed. Download stopped.";
                    this.status = Status.COMPLETED;
                }
            }else{
                status = Status.ERROR;
                message = "Bad response code:" + code;
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
