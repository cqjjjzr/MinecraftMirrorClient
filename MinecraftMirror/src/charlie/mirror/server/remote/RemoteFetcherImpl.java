package charlie.mirror.server.remote;

import charlie.mirror.server.DownloadTask;
import charlie.mirror.server.MinecraftMirror;

import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class RemoteFetcherImpl extends UnicastRemoteObject implements RemoteFetcher {
    protected RemoteFetcherImpl() throws RemoteException {
        //super();
    }

    @Override
    public Map<URL, DownloadTask> getTasks() throws RemoteException {
        return MinecraftMirror.getQueue();
    }
}
