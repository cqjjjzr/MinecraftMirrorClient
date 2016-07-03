package charlie.mirror.server;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask implements Runnable, Serializable {
    private static long serialVersionUID = 1L;

    private URL url;
    private Status status;
    private String message;
    private File path;
    private boolean sum = false;
    private String sha1;
    private int length = -1;

    public DownloadTask(URL url, File path){
        this.url = url;
        this.status = Status.IN_QUEUE;
        this.message = "Waiting for queue.";
        this.path = path;
        this.path.getParentFile().mkdirs();
    }

    public DownloadTask(URL url, File path, String sha1, int length){
        this.url = url;
        this.status = Status.IN_QUEUE;
        this.message = "Waiting for queue.";
        this.path = path;
        this.path.getParentFile().mkdirs();
        this.sum = true;
        this.sha1 = sha1;
        this.length = length;
    }

    @Override
    public void run() {
        try {
            if(path.exists()  && length == FileUtils.sizeOf(path)){
                this.message = "File already existed. Download stopped.";
                this.status = DownloadTask.Status.COMPLETED;
                return;
            }
            byte[] fileContent;
            fileContent = memory();
            if(fileContent != null){
                if(sum){
                    String fsha1 = DigestHelper.sha1(fileContent);
                    if(!fsha1.equalsIgnoreCase(sha1)){
                        this.message = "Error checksum. Abort.";
                        this.status = DownloadTask.Status.ERROR;
                        MinecraftMirror.logger.info("Checksum:" + sha1 +  " and " + fsha1);
                        return;
                    }
                    if(fileContent.length != length){
                        this.message = "Error file size. Abort.";
                        this.status = DownloadTask.Status.ERROR;
                        MinecraftMirror.logger.info("File size:" + length +  " and " + fileContent.length);
                        return;
                    }
                    if(path.exists())
                        FileUtils.forceDelete(path);
                    FileUtils.writeByteArrayToFile(path, fileContent);
                    this.message = "Completed.";
                    this.status = DownloadTask.Status.COMPLETED;
                    MinecraftMirror.logger.info("Downloaded " + url.toString());
                }
            }
        } catch (Exception e) {
            status = DownloadTask.Status.ERROR;
             MinecraftMirror.logger.warning("Downloading exception:" + e.getClass().toString() + " " + e.getMessage());
            //e.printStackTrace();
            this.message = "Error:" + e.toString();
        }
    }

    public enum Status{
        IN_QUEUE, DOWNLOADING, ERROR, COMPLETED
    }

    public byte[] memory() throws Exception {
        this.status = DownloadTask.Status.DOWNLOADING;
        this.message = "Try to establish connection to server";
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
                this.message = "Downloading, downloaded " + process + "B of " + length + "B. " + (((double) process) / length) * 100 + "%";
            }
            inputStream.close();
            connection.disconnect();
            return outputStream.toByteArray();
        }else{
            status = DownloadTask.Status.ERROR;
            message = "Bad response code:" + code;
        }
        return null;
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
}
