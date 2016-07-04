package charlie.mirror.server.remote;

import charlie.mirror.server.DownloadTask;

import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface RemoteFetcher extends Remote {
    Map<URL, DownloadTask> getTasks() throws RemoteException;
    int getFullSize() throws RemoteException;
}
