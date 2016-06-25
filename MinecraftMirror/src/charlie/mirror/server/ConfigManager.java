package charlie.mirror.server;

import java.util.prefs.Preferences;

public class ConfigManager {
    Preferences preferences;
    public void init(){
        preferences = Preferences.userNodeForPackage(this.getClass());
    }

    public String getHttpRoot(){
        return preferences.get("HttpRoot", "D:\\httproot");
    }

    public int getReadTimeout(){
        return preferences.getInt("ReadTimeOut", 7000);
    }

    public int getConnectTimeout(){
        return preferences.getInt("ConnectTimeOut", 7000);
    }

    public String getUserAgent(){
        return preferences.get("UserAgent", "Minecraft Mirror " + MinecraftMirror.VERSION);
    }

    public int getMaxQueue(){
        return preferences.getInt("MaxQueue", 50);
    }
}
