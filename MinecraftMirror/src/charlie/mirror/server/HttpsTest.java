package charlie.mirror.server;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class HttpsTest {
    public static void main(String[] args) throws IOException {
        URLConnection urlConnection = new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json").openConnection();
        urlConnection.connect();
        System.out.println("hi");
    }
}
