package charlie.mirror.server;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownloadTask implements Runnable{
    private URL url;
    private Status status;
    private String message;
    private File path;
    private String tag;
    private boolean override;

    public DownloadTask(URL url, File path, String tag){
        this.url = url;
        this.status = Status.IN_QUEUE;
        this.message = "Waiting for queue.";
        this.path = path;
        this.path.getParentFile().mkdirs();
        this.tag = tag;
        this.override = false;
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
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(MinecraftMirror.configManager.getConnectTimeout());
            connection.setReadTimeout(MinecraftMirror.configManager.getReadTimeout());
            connection.setRequestProperty("User-Agent", MinecraftMirror.configManager.getUserAgent());
            connection.connect();
            int code = (connection instanceof HttpsURLConnection ?
                    ((HttpsURLConnection) connection).getResponseCode() :
                    ((HttpURLConnection) connection).getResponseCode());
            if(code == 200){
                int length = connection.getContentLength();
                this.message = "Connect established. ContentLength:" + length + ". Start downloading...";
                if(!path.exists() || override){
                    FileOutputStream fileOutputStream = new FileOutputStream(path);
                    InputStream inputStream = connection.getInputStream();
                    int readLength;
                    int process = 0;
                    byte[] buffer = new byte[1024];
                    while((readLength = inputStream.read(buffer)) != -1){
                        fileOutputStream.write(buffer, 0, readLength);
                        process += readLength;
                        this.message = "Downloading, downloaded " + process + "B of " + length + "B. " + ((double) readLength) / length * 100 + "%";
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    if(connection instanceof HttpsURLConnection){
                        ((HttpsURLConnection) connection).disconnect();
                    }else{
                        ((HttpURLConnection) connection).disconnect();
                    }
                    this.message = "Completed.";
                    this.status = Status.COMPLETED;
                    MinecraftMirror.logger.info("Downloaded " + url.toString());
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
            MinecraftMirror.logger.warning("Downloading exception:" + e.getClass().toString() + " " + e.getMessage());
            //e.printStackTrace();
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
