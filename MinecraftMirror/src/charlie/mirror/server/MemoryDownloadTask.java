package charlie.mirror.server;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class MemoryDownloadTask implements Callable<String> {
    private URL url;
    private Status status;
    private String message;
    private String tag;

    public MemoryDownloadTask(URL url, String tag){
        this.url = url;
        this.status = Status.IN_QUEUE;
        this.message = "Waiting for queue.";
        this.tag = tag;
    }

    @Override
    public String call() throws Exception {
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
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int readLength;
                int process = 0;
                byte[] buffer = new byte[1024];
                while((readLength = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer, 0, readLength);
                    process += readLength;
                    this.message = "Downloading, downloaded " + process + "B of " + length + "B. " + (((double) readLength) / length) * 100 + "%";
                }
                inputStream.close();
                connection.disconnect();
                return new String(outputStream.toByteArray());
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
        return null;
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

    public String getTag() {
        return tag;
    }
}
