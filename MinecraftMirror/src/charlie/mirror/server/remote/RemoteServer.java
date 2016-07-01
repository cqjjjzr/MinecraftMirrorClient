package charlie.mirror.server.remote;

import charlie.mirror.server.MinecraftMirror;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;

public class RemoteServer {
    private static RemoteFetcher obj;

    public static void init(){
        try {
            System.setProperty("java.rmi.server.hostname","127.0.0.1");
            RMISocketFactory.setSocketFactory(new RMISocket());
            LocateRegistry.createRegistry(MinecraftMirror.configManager.getRemoteNamingPort());
            obj = new RemoteFetcherImpl();
            Naming.rebind("rmi://127.0.0.1:20100/MinecraftMirror", obj);
            MinecraftMirror.logger.info("Remote running");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class RMISocket extends RMISocketFactory {

        @Override
        public Socket createSocket(String host, int port) throws IOException {
            return new Socket(host, port);
        }

        @Override
        public ServerSocket createServerSocket(int port) throws IOException {
            if(port == 0) port = MinecraftMirror.configManager.getRemoteDataPort();
            return new ServerSocket(port);
        }
    }
}
